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
        Transaction savedTx = transactionRepository.save(tx);

        // 트랜잭션 저장 후 시점
        long startAlertTime = System.currentTimeMillis();

        // AlertSetting 조회
        List<AlertSetting> settings = alertRepository.findByCoin(savedTx.getCoin());

        //  임계값 비교
        for (AlertSetting setting : settings) {
            if (savedTx.getTradeVolume() >= setting.getThreshold()) {
                // 임계값 초과 UserAlert 생성
                UserAlert userAlert = new UserAlert(
                        setting.getUserId(),
                        savedTx.getCoin(),
                        savedTx.getTradePrice(),
                        savedTx.getTradeVolume(),
                        savedTx.getTradeTimestamp()
                );
                userAlertService.createUserAlert(userAlert);
            }
        }
        // Alert db 저장 후 시점
        long endAlertTime = System.currentTimeMillis();

        long alertInsertionTime = endAlertTime - startAlertTime;
        log.info("Transaction save {}ms",alertInsertionTime);
        return savedTx;
    }

}
