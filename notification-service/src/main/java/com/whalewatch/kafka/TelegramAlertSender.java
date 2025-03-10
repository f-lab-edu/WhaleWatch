package com.whalewatch.kafka;

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
        telegramWebhookUserBot.sendTextMessage(message.getChatId(), message.getMessage());
        log.info("Telegram alert sent to chatId {}: {}", message.getChatId(), message.getMessage());
        ack.acknowledge();
    }
}
