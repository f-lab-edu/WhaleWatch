package com.whalewatch.service;

import com.whalewatch.WhaleWatchApplication;
import com.whalewatch.domain.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = WhaleWatchApplication.class)
public class TransactionServiceTest {

    @Autowired
    TransactionService transactionService;

    @Test
    void testGetAllTransactionsHasDummyData() {
        var transactions = transactionService.getAllTransactions();
        Assertions.assertFalse(transactions.isEmpty());
        Assertions.assertTrue(transactions.stream()
                .anyMatch(t -> t.getHash().equals("0xabc123") && t.getCoin().equals("BTC") && t.getAmount() == 20000));
        Assertions.assertTrue(transactions.stream()
                .anyMatch(t -> t.getHash().equals("0xabc123") && t.getCoin().equals("ETH") && t.getAmount() == 15000));
    }

}
