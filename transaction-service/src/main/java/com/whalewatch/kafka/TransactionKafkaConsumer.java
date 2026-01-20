package com.whalewatch.kafka;

import com.whalewatch.domain.Transaction;
import com.whalewatch.dto.TransactionEventDto;
import com.whalewatch.repository.TransactionRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class TransactionKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionKafkaConsumer.class);
    private final TransactionRepository transactionRepository;

    public TransactionKafkaConsumer(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @KafkaListener(
            topics = "transaction_event",
            groupId = "whalewatch_group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void persistTransaction(ConsumerRecord<String, TransactionEventDto> record, Acknowledgment ack) {
        log.info("[TransactionConsumer] Received: topic={}, partition={}, offset={}",
                record.topic(), record.partition(), record.offset());
        
        try {
            TransactionEventDto event = record.value();
            
            // 데이터 유효성 검증
            validateEvent(event);
            
            Transaction tx = convertToTransaction(event);
            Transaction saved = transactionRepository.save(tx);
            
            log.info("[TransactionConsumer] Success: id={}, coin={}, volume={}",
                    saved.getId(), saved.getCoin(), saved.getTradeVolume());
            
            ack.acknowledge();
            
        } catch (IllegalArgumentException e) {
            // 데이터 유효성 오류 - 재시도 불필요, DLQ로 전송
            log.error("[TransactionConsumer] Validation error - sending to DLQ: {}", e.getMessage());
            throw e; // ErrorHandler가 DLQ로 전송
            
        } catch (Exception e) {
            // 기타 오류 - 재시도 후 실패 시 DLQ로 전송
            log.error("[TransactionConsumer] Processing error - will retry: {}", e.getMessage());
            throw new RuntimeException("Failed to process transaction event", e);
        }
    }
    
    private void validateEvent(TransactionEventDto event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        if (event.getCoin() == null || event.getCoin().isEmpty()) {
            throw new IllegalArgumentException("Coin cannot be null or empty");
        }
        if (event.getTradeVolume() == null || event.getTradeVolume() <= 0) {
            throw new IllegalArgumentException("Trade volume must be positive");
        }
    }

    private Transaction convertToTransaction(TransactionEventDto event) {
        Transaction tx = new Transaction();
        tx.setCoin(event.getCoin());
        tx.setTradePrice(event.getTradePrice());
        tx.setTradeVolume(event.getTradeVolume());
        tx.setAskBid(event.getAskBid());
        tx.setTradeTimestamp(event.getTradeTimestamp());
        return tx;
    }
}
