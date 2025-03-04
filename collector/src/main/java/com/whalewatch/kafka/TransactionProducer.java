package com.whalewatch.kafka;

import com.whalewatch.dto.TransactionEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionProducer {
    private static final Logger log = LoggerFactory.getLogger(TransactionProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TransactionProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransactionEvent(TransactionEventDto event) {
        String topicName = "transaction_event";
        kafkaTemplate.send(topicName, event);
        log.info("[TransactionProducer] Sent event to Kafka topic '{}': {}", topicName, event);
    }
}
