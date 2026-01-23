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
    private static final String RESULT_TOPIC = "user_registration_result_topic";

    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserRegistrationConsumer(UserRepository userRepository,
                                    KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = "user_registration_topic",
            groupId = "whalewatch_registration"
    )
    public void onUserRegistration(ConsumerRecord<String, UserRegistrationEventDto> record,
                                   Acknowledgment ack) {
        log.info("[UserRegistrationConsumer] Received: topic={}, partition={}, offset={}",
                record.topic(), record.partition(), record.offset());

        UserRegistrationEventDto event = record.value();
        
        try {
            // 유효성 검증
            validateEvent(event);
            
            User user = new User(event.getEmail(), event.getUsername());
            user.setTelegramChatId(event.getChatId());

            // DB에 저장
            User saved = userRepository.save(user);
            log.info("[UserRegistrationConsumer] Success: id={}, email={}, chatId={}",
                    saved.getId(), saved.getEmail(), saved.getTelegramChatId());

            // 성공 결과 이벤트 발행
            sendResultEvent(event.getChatId(), true,
                    "Registration succeeded! Username: " + event.getUsername());
            
            ack.acknowledge();
            
        } catch (IllegalArgumentException e) {
            // 유효성 오류 - DLQ로 전송, 사용자에게 실패 알림
            log.error("[UserRegistrationConsumer] Validation error: {}", e.getMessage());
            sendResultEvent(event.getChatId(), false, "Registration failed: " + e.getMessage());
            throw e;
            
        } catch (Exception e) {
            // DB 오류 등 - 재시도
            log.error("[UserRegistrationConsumer] Processing error - will retry: {}", e.getMessage());
            throw new RuntimeException("Failed to register user", e);
        }
    }
    
    private void validateEvent(UserRegistrationEventDto event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        if (event.getEmail() == null || event.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (event.getUsername() == null || event.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (event.getChatId() == null) {
            throw new IllegalArgumentException("ChatId cannot be null");
        }
    }
    
    private void sendResultEvent(Long chatId, boolean success, String message) {
        try {
            UserRegistrationResultEventDto resultEvent = new UserRegistrationResultEventDto(
                    chatId, success, message);
            kafkaTemplate.send(RESULT_TOPIC, resultEvent);
            log.debug("[UserRegistrationConsumer] Result event sent: chatId={}, success={}",
                    chatId, success);
        } catch (Exception e) {
            log.error("[UserRegistrationConsumer] Failed to send result event: {}", e.getMessage());
        }
    }
}