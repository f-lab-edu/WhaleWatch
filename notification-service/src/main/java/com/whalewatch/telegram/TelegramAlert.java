package com.whalewatch.telegram;

import com.whalewatch.domain.AlertSetting;
import com.whalewatch.domain.UserAlert;
import com.whalewatch.dto.TransactionEventDto;
import com.whalewatch.repository.AlertRepository;
import com.whalewatch.service.UserAlertService;
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
    private final TelegramWebhookUserBot telegramWebhookUserBot;

    public TelegramAlert(AlertRepository alertRepository,
                         UserAlertService userAlertService,
                         TelegramWebhookUserBot telegramWebhookUserBot) {
        this.alertRepository = alertRepository;
        this.userAlertService = userAlertService;
        this.telegramWebhookUserBot = telegramWebhookUserBot;
    }

    @KafkaListener(
            topics = "transaction_event",
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
                // 사용자 알림 기록
                UserAlert savedAlert = userAlertService.createUserAlert(
                        new UserAlert(setting.getUserId(), event.getCoin(),
                                event.getTradePrice(), event.getTradeVolume(),
                                event.getTradeTimestamp())
                );
                log.info("User alert created for userId {} with alertId {}",
                        setting.getUserId(), savedAlert.getId());

                Long chatId = setting.getChatId();
                if (chatId != null) {
                    String message = String.format("Alert ID [%d]: A trade of at least %.2f occurred for %s",
                            savedAlert.getId(), event.getTradeVolume(), event.getCoin());
                    telegramWebhookUserBot.sendTextMessage(chatId, message);
                    log.info("Telegram alert sent to chatId {}: {}", chatId, message);
                } else {
                    log.warn("AlertSetting {} has no chatId for userId {}", setting.getId(), setting.getUserId());
                }
            }
        }
        ack.acknowledge();
    }
}
