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

            // 2) 관리자 필터링 로직 => Transaction DB 저장
            filteringService.adminFiltering(tradeDto);


        } catch (Exception e) {
            log.error("Failed to parse JSON message: {}", jsonMessage, e);
        }
    }

}
