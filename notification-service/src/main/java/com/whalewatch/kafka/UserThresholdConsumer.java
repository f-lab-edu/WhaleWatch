package com.whalewatch.kafka;

import com.whalewatch.domain.AlertSetting;
import com.whalewatch.dto.ThresholdEventDto;
import com.whalewatch.repository.AlertRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class UserThresholdConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserThresholdConsumer.class);

    private final AlertRepository alertRepository;

    public UserThresholdConsumer(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @KafkaListener(topics = "user_threshold_topic", groupId = "whalewatch_threshold")
    public void onUserThreshold(ConsumerRecord<String, ThresholdEventDto> record, Acknowledgment ack) {
        log.info("[UserThresholdConsumer] Received: topic={}, partition={}, offset={}",
                record.topic(), record.partition(), record.offset());
        
        try {
            ThresholdEventDto event = record.value();
            
            // 유효성 검증
            validateEvent(event);

            AlertSetting setting = new AlertSetting();
            setting.setCoin(event.getCoin());
            setting.setThreshold(event.getThreshold());
            setting.setChatId(event.getChatId());

            AlertSetting saved = alertRepository.save(setting);
            log.info("[UserThresholdConsumer] Success: id={}, coin={}, threshold={}",
                    saved.getId(), saved.getCoin(), saved.getThreshold());
            
            ack.acknowledge();
            
        } catch (IllegalArgumentException e) {
            // 유효성 오류 - DLQ로 전송
            log.error("[UserThresholdConsumer] Validation error - sending to DLQ: {}", e.getMessage());
            throw e;
            
        } catch (Exception e) {
            // DB 오류 등 - 재시도
            log.error("[UserThresholdConsumer] Processing error - will retry: {}", e.getMessage());
            throw new RuntimeException("Failed to save threshold setting", e);
        }
    }
    
    private void validateEvent(ThresholdEventDto event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        if (event.getCoin() == null || event.getCoin().isEmpty()) {
            throw new IllegalArgumentException("Coin cannot be null or empty");
        }
        if (event.getChatId() == null) {
            throw new IllegalArgumentException("ChatId cannot be null");
        }
        if (event.getThreshold() <= 0) {
            throw new IllegalArgumentException("Threshold must be positive");
        }
    }
}
