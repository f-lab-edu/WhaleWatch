package com.whalewatch.service;

import com.whalewatch.TradeDto;
import com.whalewatch.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FilteringService {

    private static final Logger log = LoggerFactory.getLogger(ParsingService.class);
    private final Map<String, Double> volumeThresholdMap = new ConcurrentHashMap<>();

    private final TransactionService transactionService;

    public FilteringService(TransactionService transactionService) {
        this.transactionService = transactionService;

        // 초기값
        volumeThresholdMap.put("KRW-BTC", 0.7);
        volumeThresholdMap.put("KRW-ETH", 20.0);
        volumeThresholdMap.put("KRW-SOL", 220.0);

    }

    public void adminFiltering(TradeDto dto) {
        if (dto.getCode() == null || dto.getTradeVolume() == null) {
            return;
        }

        Double threshold = volumeThresholdMap.get(dto.getCode());
        if (threshold == null) {
            return;
        }

        if (dto.getTradeVolume() > threshold) {
            log.info("[ADMIN] coin={}, volume={} > threshold({}) => Save DB",
                    dto.getCode(), dto.getTradeVolume(), threshold);


            Transaction tx = new Transaction(
                    dto.getCode(),
                    dto.getTradePrice(),
                    dto.getTradeVolume(),
                    dto.getAskBid(),
                    dto.getTradeTimestamp()
            );

            // createTransaction
            transactionService.createTransaction(tx);
        }
    }


}
