package com.whalewatch.kafka;

import com.whalewatch.dto.UserOtpEventDto;
import com.whalewatch.telegram.TelegramWebhookUserBot;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class UserOtpConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserOtpConsumer.class);

    private final TelegramWebhookUserBot telegramBot;

    public UserOtpConsumer(TelegramWebhookUserBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @KafkaListener(topics = "user_otp_topic", groupId = "whalewatch_otp")
    public void onUserOtp(ConsumerRecord<String, UserOtpEventDto> record, Acknowledgment ack) {
        UserOtpEventDto event = record.value();
        log.info("[UserOtpConsumer] Received OTP event: {}", event);

        try {
            telegramBot.sendTextMessage(event.getChatId(), event.getMessage());
            log.info("[UserOtpConsumer] OTP message sent to chatId={}", event.getChatId());
        } catch (Exception e) {
            log.error("Failed to send OTP via telegramBot: {}", e.getMessage(), e);
        } finally {
            ack.acknowledge();
        }
    }
}