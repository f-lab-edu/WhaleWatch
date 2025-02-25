package com.whalewatch.service;

import com.whalewatch.ExchangesProperties;
import com.whalewatch.TradeDto;
import com.whalewatch.domain.Transaction;
import com.whalewatch.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FilteringService {

    private static final Logger log = LoggerFactory.getLogger(FilteringService.class);
    private final ExchangesProperties exchangesProperties;
    private final TransactionService transactionService;

    public FilteringService(TransactionService transactionService, ExchangesProperties exchangesProperties) {
        this.transactionService = transactionService;
        this.exchangesProperties = exchangesProperties;
    }

    public void adminFiltering(TradeDto dto) {
        if (dto.getCode() == null || dto.getTradeVolume() == null || dto.getExchange() == null) {
            return;
        }
        String exchangeKey = dto.getExchange().toLowerCase();
        ExchangesProperties.ExchangeConfig config = exchangesProperties.getExchanges().get(exchangeKey);
        if (config == null) {
            log.error("No config found : {}", dto.getExchange());
            return;
        }
        Double threshold = config.getThreshold().get(dto.getCode());
        if (threshold == null) {
            return;
        }
        if (dto.getTradeVolume() > threshold) {
            log.info("[ADMIN][{}] coin={}, volume={} > threshold({}) => Save DB",
                    dto.getExchange(), dto.getCode(), dto.getTradeVolume(), threshold);
            Transaction tx = new Transaction(
                    dto.getCode(),
                    dto.getTradePrice(),
                    dto.getTradeVolume(),
                    dto.getAskBid(),
                    dto.getTradeTimestamp()
            );
            transactionService.createTransaction(tx);
        }
    }
}
