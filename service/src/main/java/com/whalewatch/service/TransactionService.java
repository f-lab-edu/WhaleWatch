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

    private final TransactionRepository transactionRepository;
    private final AlertRepository alertRepository;
    private final UserAlertService userAlertService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final String TRANSACTION_ALERT = "transaction_alert";

    public TransactionService(TransactionRepository transactionRepository,
                              AlertRepository alertRepository,
                              UserAlertService userAlertService,
                              KafkaTemplate<String, Object> kafkaTemplate) {
        this.transactionRepository = transactionRepository;
        this.alertRepository = alertRepository;
        this.userAlertService = userAlertService;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(int id) {
        return transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public Transaction createTransaction(Transaction tx) {
        Transaction savedTx = transactionRepository.save(tx);

        TransactionEventDto event = new TransactionEventDto(
                savedTx.getId(),
                savedTx.getCoin(),
                savedTx.getTradePrice(),
                savedTx.getTradeVolume(),
                savedTx.getAskBid(),
                savedTx.getTradeTimestamp()
        );

        kafkaTemplate.send(TRANSACTION_ALERT, event);
        log.info("Kafka message sent for transaction id {}", savedTx.getId());
        return savedTx;
    }

}
