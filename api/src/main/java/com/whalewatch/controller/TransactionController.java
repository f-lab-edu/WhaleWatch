package com.whalewatch.controller;

import com.whalewatch.domain.Transaction;
import com.whalewatch.dto.TransactionDto;
import com.whalewatch.mapper.TransactionMapper;
import com.whalewatch.transaction.TransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    public TransactionController(TransactionService transactionService, TransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
    }

    @GetMapping("/list")
    public List<TransactionDto> getTransactions(){
        return transactionService.getAllTransactions().stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TransactionDto getTransactionByID(@PathVariable int id){
        Transaction trans = transactionService.getTransactionById(id);
        return transactionMapper.toDto(trans);
    }
}
