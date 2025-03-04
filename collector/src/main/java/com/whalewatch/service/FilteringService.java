package com.whalewatch.service;

import com.whalewatch.config.ExchangesProperties;
import com.whalewatch.dto.TradeDto;
import com.whalewatch.dto.TransactionEventDto;
import com.whalewatch.kafka.TransactionProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FilteringService {

    private static final Logger log = LoggerFactory.getLogger(FilteringService.class);

    private final ExchangesProperties exchangesProperties;
    private final TransactionProducer transactionProducer;

    public FilteringService(ExchangesProperties exchangesProperties,
                            TransactionProducer transactionProducer) {
        this.exchangesProperties = exchangesProperties;
        this.transactionProducer = transactionProducer;
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
            TransactionEventDto eventDto = new TransactionEventDto(
                    0,
                    dto.getCode(),
                    dto.getTradePrice(),
                    dto.getTradeVolume(),
                    dto.getAskBid(),
                    dto.getTradeTimestamp()
            );

            transactionProducer.sendTransactionEvent(eventDto);
        }
    }
}
