package com.whalewatch.controller;

import com.whalewatch.domain.Transaction;
import com.whalewatch.dto.TransactionDto;
import com.whalewatch.service.TransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/list")
    public List<TransactionDto> getTransactions(){
        List<Transaction> list = transactionService.getAllTransactions();
        return list.stream().map(t-> new TransactionDto(t.getId(), t.getHash(), t.getCoin(), t.getAmount()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TransactionDto getTransactionByID(@PathVariable int id){
        Transaction trans = transactionService.getTransactionById(id);
        return new TransactionDto(trans.getId(), trans.getHash(), trans.getCoin(), trans.getAmount());
    }
}
