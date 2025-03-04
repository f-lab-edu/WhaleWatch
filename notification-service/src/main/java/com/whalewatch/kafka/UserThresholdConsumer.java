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
        ThresholdEventDto event = record.value();
        log.info("[UserThresholdConsumer] Received threshold event: {}", event);

        try {
            AlertSetting setting = new AlertSetting();
            setting.setCoin(event.getCoin());
            setting.setThreshold(event.getThreshold());
            setting.setChatId(event.getChatId());

            AlertSetting saved = alertRepository.save(setting);
            log.info("[UserThresholdConsumer] AlertSetting created: id={}, coin={}, threshold={}",
                    saved.getId(), saved.getCoin(), saved.getThreshold());
        } catch (Exception e) {
            log.error("Error saving threshold: {}", e.getMessage(), e);
        } finally {
            ack.acknowledge();
        }
    }
}
