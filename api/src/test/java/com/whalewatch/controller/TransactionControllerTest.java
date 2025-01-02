package com.whalewatch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whalewatch.domain.Transaction;
import com.whalewatch.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();
        // 기본 데이터 2개 삽입
        transactionRepository.save(new Transaction("0xabc123", "BTC", 20000));
        transactionRepository.save(new Transaction("0xdef456", "ETH", 15000));
    }

    @Test
    void testGetAllTransactions() throws Exception {
        mockMvc.perform(get("/api/transactions/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].hash", is("0xabc123")))
                .andExpect(jsonPath("$[1].coin", is("ETH")));
    }

    @Test
    void testGetTransactionById() throws Exception {
        List<Transaction> list = transactionRepository.findAll();
        Transaction first = list.get(0);

        mockMvc.perform(get("/api/transactions/" + first.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hash", is("0xabc123")))
                .andExpect(jsonPath("$.coin", is("BTC")));
    }

}
