package com.whalewatch.service;

import com.whalewatch.domain.Transaction;
import com.whalewatch.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(SpringExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void getAllTransactions() {
        // given
        Transaction t1 = new Transaction("0xabc123", "BTC", 10000);
        Transaction t2 = new Transaction("0xdef456", "ETH", 15000);
        given(transactionRepository.findAll()).willReturn(Arrays.asList(t1, t2));

        // when
        List<Transaction> result = transactionService.getAllTransactions();

        // then
        assertEquals(2, result.size()); // 리스트 크기 검증

        // 첫 번째 트랜잭션 검증
        assertEquals("BTC", result.get(0).getCoin());
        assertEquals("0xabc123", result.get(0).getHash());
        assertEquals(10000, result.get(0).getAmount());

        // 두 번째 트랜잭션 검증
        assertEquals("ETH", result.get(1).getCoin());
        assertEquals("0xdef456", result.get(1).getHash());
        assertEquals(15000, result.get(1).getAmount());
    }

    @Test
    void getTransactionById() {
        // given
        int txId = 1;
        Transaction t1 = new Transaction("0xabc123", "BTC", 10000);

        given(transactionRepository.findById(txId)).willReturn(Optional.of(t1));

        // when
        Transaction result = transactionService.getTransactionById(txId);

        // then
        assertNotNull(result); // 반환값이 null이 아님을 확인
        assertEquals("BTC", result.getCoin()); // 코인 이름 검증
        assertEquals("0xabc123", result.getHash()); // 트랜잭션 해시 검증
        assertEquals(10000, result.getAmount()); // 금액 검증
    }

    @Test
    void createTransaction() {
        // given
        Transaction input = new Transaction("0x123", "DOGE", 5000);
        given(transactionRepository.save(input)).willReturn(input);

        // when
        Transaction result = transactionService.createTransaction(input);

        // then
        assertNotNull(result); // 반환값이 null이 아님을 확인
        assertEquals("DOGE", result.getCoin()); // 코인 이름 검증
        assertEquals("0x123", result.getHash()); // 트랜잭션 해시 검증
        assertEquals(5000, result.getAmount()); // 금액 검증
    }
}
