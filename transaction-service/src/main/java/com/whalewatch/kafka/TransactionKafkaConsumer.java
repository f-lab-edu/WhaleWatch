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
        TransactionEventDto event = record.value();
        Transaction tx = convertToTransaction(event);
        transactionRepository.save(tx);
        log.info("Persisted transaction id: {}", tx.getId());
        ack.acknowledge();
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
