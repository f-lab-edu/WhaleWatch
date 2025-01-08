package com.whalewatch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whalewatch.TradeDto;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ParsingService {

    private static final Logger log = LoggerFactory.getLogger(ParsingService.class);

    private final ObjectMapper objectMapper;
    private final FilteringService filteringService;

    public ParsingService(ObjectMapper objectMapper,
                               FilteringService filteringService) {
        this.objectMapper = objectMapper;
        this.filteringService = filteringService;
    }

    public void parsingMessage(String jsonMessage) {
        try {
            // JSON → TradeDto
            TradeDto tradeDto = objectMapper.readValue(jsonMessage, TradeDto.class);

            // 필터링
            boolean pass = filteringService.shouldAlert(tradeDto);
            if (pass) {
                log.info("[ALERT] Coin={}, volume={} exceeded threshold => {}",
                        tradeDto.getCode(),
                        tradeDto.getTradeVolume(),
                        tradeDto);
            }

        } catch (Exception e) {
            log.debug("Exception message: {}", jsonMessage, e);
        }
    }

    @PostConstruct
    public void testHandleMessage() {
        // 애플리케이션 시작 시 가짜 데이터 처리 테스트
        String sampleJson = "{\"type\":\"trade\",\"code\":\"KRW-BTC\","
                + "\"trade_price\":50000.0,\"trade_volume\":1.0,"
                + "\"ask_bid\":\"ASK\",\"change_price\":10.0,"
                + "\"timestamp\":1620000000000,\"trade_timestamp\":1620000000000}";
        log.info("Testing handleMessage with sample data...");
        parsingMessage(sampleJson);
    }



}
