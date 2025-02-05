package com.whalewatch.service;

import com.whalewatch.domain.AlertSetting;
import com.whalewatch.domain.Transaction;
import com.whalewatch.domain.UserAlert;
import com.whalewatch.repository.AlertRepository;
import com.whalewatch.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final AlertRepository alertRepository;
    private final UserAlertService userAlertService;

    public TransactionService(TransactionRepository transactionRepository,
                              AlertRepository alertRepository,
                              UserAlertService userAlertService) {
        this.transactionRepository = transactionRepository;
        this.alertRepository = alertRepository;
        this.userAlertService = userAlertService;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(int id) {
        return transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public Transaction createTransaction(Transaction tx) {
        long start = System.currentTimeMillis();
        Transaction savedTx = transactionRepository.save(tx);
        long duration = System.currentTimeMillis() - start;
        log.info("Alert savs {} ms", duration);
        return savedTx;
    }

}
