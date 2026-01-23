package com.whalewatch.kafka;

import com.whalewatch.dto.TransactionEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class TransactionProducer {
    private static final Logger log = LoggerFactory.getLogger(TransactionProducer.class);
    private static final String TOPIC_NAME = "transaction_event";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TransactionProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransactionEvent(TransactionEventDto event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(TOPIC_NAME, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[TransactionProducer] Success: topic={}, partition={}, offset={}, coin={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        event.getCoin());
            } else {
                log.error("[TransactionProducer] Failed to send event: coin={}, error={}",
                        event.getCoin(), ex.getMessage(), ex);
                // 실패 시 재시도 또는 DLQ 전송 로직 추가 가능
                handleSendFailure(event, ex);
            }
        });
    }

    private void handleSendFailure(TransactionEventDto event, Throwable ex) {
        // 전송 실패 시 처리 로직
        log.warn("[TransactionProducer] Handling send failure for event: {}", event);
        // 추후 DLQ 전송 또는 재시도 로직 구현
    }
}
