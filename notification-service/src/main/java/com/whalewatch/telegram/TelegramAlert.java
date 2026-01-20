package com.whalewatch.telegram;

import com.whalewatch.domain.AlertSetting;
import com.whalewatch.domain.UserAlert;
import com.whalewatch.dto.TransactionEventDto;
import com.whalewatch.dto.TelegramAlertMessage;
import com.whalewatch.repository.AlertRepository;
import com.whalewatch.service.UserAlertService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class TelegramAlert {

    private static final Logger log = LoggerFactory.getLogger(TelegramAlert.class);
    private static final String TELEGRAM_ALERT_TOPIC = "telegram_alert_request";

    private final AlertRepository alertRepository;
    private final UserAlertService userAlertService;
    private final KafkaTemplate<String, TelegramAlertMessage> kafkaTemplate;


    public TelegramAlert(AlertRepository alertRepository,
                         UserAlertService userAlertService,
                         KafkaTemplate<String, TelegramAlertMessage> kafkaTemplate) {
        this.alertRepository = alertRepository;
        this.userAlertService = userAlertService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = "transaction_event",
            groupId = "whalewatch_alert",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, TransactionEventDto> record, Acknowledgment ack) {
        log.info("[TelegramAlert] Received: topic={}, partition={}, offset={}",
                record.topic(), record.partition(), record.offset());
        
        try {
            TransactionEventDto event = record.value();
            
            // 데이터 유효성 검증
            if (event == null || event.getCoin() == null) {
                throw new IllegalArgumentException("Invalid event data");
            }
            
            processAlertEvent(event);
            ack.acknowledge();
            
        } catch (IllegalArgumentException e) {
            // 유효성 오류 - 재시도 불필요
            log.error("[TelegramAlert] Validation error - sending to DLQ: {}", e.getMessage());
            throw e;
            
        } catch (Exception e) {
            // 기타 오류 - 재시도
            log.error("[TelegramAlert] Processing error - will retry: {}", e.getMessage());
            throw new RuntimeException("Failed to process alert event", e);
        }
    }
    
    private void processAlertEvent(TransactionEventDto event) {
        // 해당 코인의 AlertSetting을 조회
        List<AlertSetting> alertSettings = alertRepository.findByCoinAndThresholdLessThanEqual(
                event.getCoin(), event.getTradeVolume());
        
        if (alertSettings.isEmpty()) {
            log.info("[TelegramAlert] No alert settings for coin: {}", event.getCoin());
            return;
        }

        int successCount = 0;
        int failCount = 0;

        for (AlertSetting setting : alertSettings) {
            try {
                processAlertSetting(setting, event);
                successCount++;
            } catch (Exception e) {
                log.error("[TelegramAlert] Failed to process alert for chatId={}: {}",
                        setting.getChatId(), e.getMessage());
                failCount++;
            }
        }
        
        log.info("[TelegramAlert] Processed {} alerts: success={}, fail={}",
                alertSettings.size(), successCount, failCount);
    }
    
    private void processAlertSetting(AlertSetting setting, TransactionEventDto event) {
        // 사용자 알림 저장
        UserAlert userAlert = new UserAlert(
                setting.getChatId(),
                event.getCoin(),
                event.getTradePrice(),
                event.getTradeVolume(),
                event.getAskBid(),
                event.getTradeTimestamp()
        );
        UserAlert savedAlert = userAlertService.createUserAlert(userAlert);
        log.debug("[TelegramAlert] UserAlert created: id={}", savedAlert.getId());

        Long chatId = setting.getChatId();
        if (chatId == null) {
            log.warn("[TelegramAlert] AlertSetting {} has no chatId", setting.getId());
            return;
        }
        
        // 텔레그램 알림 메시지 생성 및 전송
        String message = String.format(
                "[Alert]: A trade of at least %.2f occurred for %s at price %.2f (askBid=%s)",
                event.getTradeVolume(),
                event.getCoin(),
                event.getTradePrice(),
                event.getAskBid()
        );

        TelegramAlertMessage alertMessage = new TelegramAlertMessage(chatId, message);
        sendTelegramAlertAsync(alertMessage);
    }
    
    private void sendTelegramAlertAsync(TelegramAlertMessage alertMessage) {
        CompletableFuture<SendResult<String, TelegramAlertMessage>> future =
                kafkaTemplate.send(TELEGRAM_ALERT_TOPIC, alertMessage);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[TelegramAlert] Sent to Kafka: chatId={}, partition={}, offset={}",
                        alertMessage.getChatId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("[TelegramAlert] Failed to send to Kafka: chatId={}, error={}",
                        alertMessage.getChatId(), ex.getMessage());
            }
        });
    }
}
