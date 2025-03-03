package com.whalewatch.kafka;

import com.whalewatch.domain.User;
import com.whalewatch.dto.UserRegistrationEventDto;
import com.whalewatch.dto.UserRegistrationResultEventDto;
import com.whalewatch.repository.UserRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserRegistrationConsumer.class);

    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserRegistrationConsumer(UserRepository userRepository,
                                    KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = "user_registration_topic",
            groupId = "whalewatch_registration"   // 원하는 groupId
    )
    public void onUserRegistration(ConsumerRecord<String, UserRegistrationEventDto> record,
                                   Acknowledgment ack) {
        UserRegistrationEventDto event = record.value();
        log.info("[UserRegistrationConsumer] Received: {}", event);

        try {
            User user = new User(event.getEmail(), event.getUsername());
            user.setTelegramChatId(event.getChatId());

            // DB에 저장
            userRepository.save(user);
            log.info("User saved. email={}, username={}, chatId={}",
                    user.getEmail(), user.getUsername(), user.getTelegramChatId());

            // 성공 결과 이벤트 발행
            UserRegistrationResultEventDto successEvent = new UserRegistrationResultEventDto(
                    event.getChatId(),
                    true,
                    "Registration succeeded! Username: " + event.getUsername()
            );
            kafkaTemplate.send("user_registration_result_topic", successEvent);
        } catch (Exception e) {
            log.error("User registration failed: {}", e.getMessage(), e);
            // 실패 결과 이벤트 발행
            UserRegistrationResultEventDto failEvent = new UserRegistrationResultEventDto(
                    event.getChatId(),
                    false,
                    "Registration failed: " + e.getMessage()
            );
            kafkaTemplate.send("user_registration_result_topic", failEvent);
        }
        ack.acknowledge();
    }
}