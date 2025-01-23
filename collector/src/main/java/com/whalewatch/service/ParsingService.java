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
    private final UserFilteringService userFilteringService;

    public ParsingService(ObjectMapper objectMapper,
                               FilteringService filteringService, UserFilteringService userFilteringService) {
        this.objectMapper = objectMapper;
        this.filteringService = filteringService;
        this.userFilteringService = userFilteringService;
    }

    public void parsingMessage(String jsonMessage) {
        try {
            // JSON → TradeDto
            TradeDto tradeDto = objectMapper.readValue(jsonMessage, TradeDto.class);

            // 2) 관리자 필터링 로직 => Transaction DB 저장
            filteringService.adminFiltering(tradeDto);

            // 3) 사용자 필터링 로직 => UserAlert(또는 로그) 저장
            userFilteringService.userFiltering(tradeDto);

        } catch (Exception e) {
            log.error("Failed to parse JSON message: {}", jsonMessage, e);
        }
    }

}
