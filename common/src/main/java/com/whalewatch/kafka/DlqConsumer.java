package com.whalewatch.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whalewatch.dto.DlqMessageDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * DLQ(Dead Letter Queue) Consumer
 * 실패한 메시지를 수집하고 DlqService에 저장
 */
@Component
public class DlqConsumer {

    private static final Logger log = LoggerFactory.getLogger(DlqConsumer.class);
    private static final String DLQ_EXCEPTION_HEADER = "kafka_dlt-exception-message";
    private static final String DLQ_EXCEPTION_TYPE_HEADER = "kafka_dlt-exception-class-name";
    private static final String DLQ_ORIGINAL_TOPIC_HEADER = "kafka_dlt-original-topic";

    private final DlqService dlqService;
    private final ObjectMapper objectMapper;

    public DlqConsumer(DlqService dlqService, ObjectMapper objectMapper) {
        this.dlqService = dlqService;
        this.objectMapper = objectMapper;
    }

    /**
     * transaction_event.dlq 토픽 구독
     */
    @KafkaListener(
            topics = "transaction_event.dlq",
            groupId = "dlq_monitor_group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeTransactionDlq(ConsumerRecord<String, Object> record, Acknowledgment ack) {
        processDlqMessage(record, "transaction_event");
        ack.acknowledge();
    }

    /**
     * telegram_alert_request.dlq 토픽 구독
     */
    @KafkaListener(
            topics = "telegram_alert_request.dlq",
            groupId = "dlq_monitor_group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeTelegramAlertDlq(ConsumerRecord<String, Object> record, Acknowledgment ack) {
        processDlqMessage(record, "telegram_alert_request");
        ack.acknowledge();
    }

    /**
     * user_registration_topic.dlq 토픽 구독
     */
    @KafkaListener(
            topics = "user_registration_topic.dlq",
            groupId = "dlq_monitor_group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUserRegistrationDlq(ConsumerRecord<String, Object> record, Acknowledgment ack) {
        processDlqMessage(record, "user_registration_topic");
        ack.acknowledge();
    }

    /**
     * user_threshold_topic.dlq 토픽 구독
     */
    @KafkaListener(
            topics = "user_threshold_topic.dlq",
            groupId = "dlq_monitor_group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUserThresholdDlq(ConsumerRecord<String, Object> record, Acknowledgment ack) {
        processDlqMessage(record, "user_threshold_topic");
        ack.acknowledge();
    }

    /**
     * DLQ 메시지 처리 공통 로직
     */
    private void processDlqMessage(ConsumerRecord<String, Object> record, String originalTopic) {
        try {
            String errorMessage = extractHeader(record, DLQ_EXCEPTION_HEADER);
            String exceptionType = extractHeader(record, DLQ_EXCEPTION_TYPE_HEADER);
            String payload = convertPayloadToString(record.value());

            DlqMessageDto dlqMessage = new DlqMessageDto(
                    originalTopic,
                    record.topic(),
                    record.partition(),
                    record.offset(),
                    record.key(),
                    payload,
                    errorMessage,
                    exceptionType
            );

            dlqService.saveMessage(dlqMessage);

            log.warn("[DLQ] Captured failed message: topic={}, partition={}, offset={}, error={}",
                    record.topic(), record.partition(), record.offset(), errorMessage);

        } catch (Exception e) {
            log.error("[DLQ] Error processing DLQ message: {}", e.getMessage(), e);
        }
    }

    /**
     * Kafka 헤더에서 값 추출
     */
    private String extractHeader(ConsumerRecord<String, Object> record, String headerName) {
        Header header = record.headers().lastHeader(headerName);
        if (header != null && header.value() != null) {
            return new String(header.value(), StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * 페이로드를 문자열로 변환
     */
    private String convertPayloadToString(Object payload) {
        if (payload == null) {
            return null;
        }
        if (payload instanceof String) {
            return (String) payload;
        }
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            return payload.toString();
        }
    }
}
