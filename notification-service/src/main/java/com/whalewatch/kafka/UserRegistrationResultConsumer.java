package com.whalewatch.kafka;


import com.whalewatch.dto.UserRegistrationResultEventDto;
import com.whalewatch.telegram.TelegramWebhookUserBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationResultConsumer {
    private static final Logger log = LoggerFactory.getLogger(UserRegistrationResultConsumer.class);
    private final TelegramWebhookUserBot telegramWebhookUserBot;

    public UserRegistrationResultConsumer(TelegramWebhookUserBot telegramWebhookUserBot) {
        this.telegramWebhookUserBot = telegramWebhookUserBot;
    }

    @KafkaListener(topics = "user_registration_result_topic", groupId = "whalewatch_registration_result")
    public void onRegistrationResult(UserRegistrationResultEventDto event) {
        log.info("Received UserRegistrationResultEvent: {}", event);

        String msg;
        if (event.isSuccess()) {
            msg = "Registration success: " + event.getMessage();
        } else {
            msg = "Registration failed: " + event.getMessage();
        }
        telegramWebhookUserBot.sendTextMessage(event.getChatId(), msg);
    }
}
