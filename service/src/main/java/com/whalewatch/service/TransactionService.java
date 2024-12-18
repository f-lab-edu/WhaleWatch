package com.whalewatch.service;

import com.whalewatch.domain.Transaction;
import com.whalewatch.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        transactionRepository.save(new Transaction("0xabc123","BTC",20000));
        transactionRepository.save(new Transaction("0xabc123","ETH",15000));
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(int id) {
        return transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public Transaction createTransaction(Transaction tx) {
        return transactionRepository.save(tx);
    }
}
