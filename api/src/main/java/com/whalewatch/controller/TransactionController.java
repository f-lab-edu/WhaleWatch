package com.whalewatch.controller;

import com.whalewatch.dto.TransactionDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @GetMapping("/list")
    public List<TransactionDto> getTransactions(){
        return Arrays.asList(
                new TransactionDto(1,"0xabc123","BTC",20000),
                new TransactionDto(2,"0xabc123","ETH",15000)
        );
    }

    @GetMapping("/{id}")
    public TransactionDto getTransactionByID(@PathVariable int id){
        return new TransactionDto(id,"0xabc123","BTC",20000);
    }
}
