package com.whalewatch.kafka;

import com.whalewatch.dto.TelegramAlertMessage;
import com.whalewatch.telegram.TelegramWebhookUserBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class TelegramAlertSender {

    private static final Logger log = LoggerFactory.getLogger(TelegramAlertSender.class);
    private final TelegramWebhookUserBot telegramWebhookUserBot;

    public TelegramAlertSender(TelegramWebhookUserBot telegramWebhookUserBot) {
        this.telegramWebhookUserBot = telegramWebhookUserBot;
    }

    @KafkaListener(
            topics = "telegram_alert_request",
            groupId = "telegram_sender",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void sendTelegramAlert(TelegramAlertMessage message, Acknowledgment ack) {
        log.info("[TelegramAlertSender] Received: chatId={}", message.getChatId());
        
        try {
            // 유효성 검증
            if (message == null || message.getChatId() == null) {
                throw new IllegalArgumentException("Invalid message: chatId is required");
            }
            
            telegramWebhookUserBot.sendTextMessage(message.getChatId(), message.getMessage());
            log.info("[TelegramAlertSender] Success: chatId={}", message.getChatId());
            ack.acknowledge();
            
        } catch (IllegalArgumentException e) {
            // 유효성 오류 - DLQ로 전송
            log.error("[TelegramAlertSender] Validation error - sending to DLQ: {}", e.getMessage());
            throw e;
            
        } catch (Exception e) {
            // 네트워크/API 오류 - 재시도
            log.error("[TelegramAlertSender] Send error - will retry: chatId={}, error={}",
                    message.getChatId(), e.getMessage());
            throw new RuntimeException("Failed to send telegram message", e);
        }
    }
}
