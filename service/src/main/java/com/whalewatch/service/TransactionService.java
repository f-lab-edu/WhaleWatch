package com.whalewatch.service;

import com.whalewatch.domain.AlertSetting;
import com.whalewatch.domain.Transaction;
import com.whalewatch.domain.UserAlert;
import com.whalewatch.dto.TransactionEventDto;
import com.whalewatch.repository.AlertRepository;
import com.whalewatch.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final AlertRepository alertRepository;
    private final UserAlertService userAlertService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final String TRANSACTION_EVENT = "transaction_event";

    public TransactionService(AlertRepository alertRepository,
                              UserAlertService userAlertService,
                              KafkaTemplate<String, Object> kafkaTemplate) {
        this.alertRepository = alertRepository;
        this.userAlertService = userAlertService;
        this.kafkaTemplate = kafkaTemplate;
    }


    public Transaction createTransaction(Transaction tx) {
        TransactionEventDto event = new TransactionEventDto(
                tx.getId(),
                tx.getCoin(),
                tx.getTradePrice(),
                tx.getTradeVolume(),
                tx.getAskBid(),
                tx.getTradeTimestamp()
        );

        kafkaTemplate.send(TRANSACTION_EVENT, event);
        log.info("Kafka event sent for transaction id {}", tx.getId());
        return tx;
    }

}
