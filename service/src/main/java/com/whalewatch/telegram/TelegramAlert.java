package com.whalewatch.telegram;

import com.whalewatch.domain.AlertSetting;
import com.whalewatch.domain.UserAlert;
import com.whalewatch.dto.TransactionEventDto;
import com.whalewatch.repository.AlertRepository;
import com.whalewatch.service.UserAlertService;
import com.whalewatch.service.UserService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TelegramAlert {

    private static final Logger log = LoggerFactory.getLogger(TelegramAlert.class);

    private final AlertRepository alertRepository;
    private final UserAlertService userAlertService;
    private final UserService userService;
    private final TelegramUserBot telegramUserBot;

    public TelegramAlert(AlertRepository alertRepository,
                         UserAlertService userAlertService,
                         UserService userService,
                         TelegramUserBot telegramUserBot) {
        this.alertRepository = alertRepository;
        this.userAlertService = userAlertService;
        this.userService = userService;
        this.telegramUserBot = telegramUserBot;
    }

    @KafkaListener(
            topics = "transaction_alert",
            groupId = "whalewatch_group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, TransactionEventDto> record, Acknowledgment ack) {
        TransactionEventDto event = record.value();
        log.info("Received transaction event: {}", event);

        // 해당 코인의 AlertSetting을 조회
        List<AlertSetting> alertSettings = alertRepository.findByCoin(event.getCoin());
        if (alertSettings.isEmpty()) {
            log.info("No alert settings for coin: {}", event.getCoin());
            ack.acknowledge();
            return;
        }

        for (AlertSetting setting : alertSettings) {
            if (event.getTradeVolume() >= setting.getThreshold()) {
                // DB에 사용자 알림 기록 저장
                UserAlert userAlert = new UserAlert(
                        setting.getUserId(),
                        event.getCoin(),
                        event.getTradePrice(),
                        event.getTradeVolume(),
                        event.getTradeTimestamp()
                );
                userAlertService.createUserAlert(userAlert);
                log.info("User alert created for userId {} for coin {}", setting.getUserId(), event.getCoin());

                // UserService를 통해 사용자의 Telegram Chat ID 조회 후 알림 전송
                try {
                    Long chatId = userService.getUserInfo(setting.getUserId()).getTelegramChatId();
                    if (chatId != null) {
                        String message = String.format("Alert: A trade of at least %.2f occurred for %s", event.getTradeVolume(), event.getCoin());
                        telegramUserBot.sendTextMessage(chatId, message);
                        log.info("Telegram alert sent to chatId {}: {}", chatId, message);
                    } else {
                        log.warn("No Telegram chat ID for userId {}", setting.getUserId());
                    }
                } catch (Exception e) {
                    log.error("Error sending Telegram alert for userId {}: {}", setting.getUserId(), e.getMessage());
                }
            }
        }
        ack.acknowledge();
    }
}
