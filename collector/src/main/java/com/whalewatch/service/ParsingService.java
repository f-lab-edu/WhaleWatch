package com.whalewatch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.whalewatch.ExchangesProperties;
import com.whalewatch.TradeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ParsingService {

    private static final Logger log = LoggerFactory.getLogger(ParsingService.class);
    private final ObjectMapper objectMapper;
    private final FilteringService filteringService;
    private final ExchangesProperties exchangesProperties;

    public ParsingService(ObjectMapper objectMapper, FilteringService filteringService, ExchangesProperties exchangesProperties) {
        this.objectMapper = objectMapper;
        this.filteringService = filteringService;
        this.exchangesProperties = exchangesProperties;
    }

    // 웹소켓 수신 메시지를 파싱
    public void parsingMessage(String jsonMessage, String exchangeName) {
        try {
            // JSON 문자열 그대로 parseDynamic 메서드에 전달
            TradeDto dto = parseDynamic(exchangeName.toLowerCase(), jsonMessage);
            if (dto != null) {
                dto.setCode(unifyCode(dto.getCode()));
                dto.setExchange(exchangeName.toUpperCase());
                filteringService.adminFiltering(dto);
            }
        } catch (Exception e) {
            log.error("[ParsingService] parseMessage fail: {}", jsonMessage, e);
        }
    }

    // 설정파일 매핑을 통해 JSON에서 Dto 필드 추출
    private TradeDto parseDynamic(String exchange, String jsonMessage) {
        ExchangesProperties.ExchangeConfig config = exchangesProperties.getExchanges().get(exchange.toLowerCase());
        if (config == null) {
            log.error("No configuration found : {}", exchange);
            return null;
        }
        Map<String, String> mapping = config.getMapping();
        if (mapping == null || mapping.isEmpty()) {
            log.error("No mapping : {}", exchange);
            return null;
        }
        TradeDto dto = new TradeDto();
        try {
            // type 필드 추출
            String typeMapping = mapping.get("type");
            if (typeMapping != null) {
                dto.setType(getFieldValue(jsonMessage, typeMapping));
            }
            // code 필드 추출
            String codeMapping = mapping.get("code");
            if (codeMapping != null) {
                dto.setCode(getFieldValue(jsonMessage, codeMapping));
            }
            // tradePrice 필드 추출
            String priceMapping = mapping.get("tradePrice");
            if (priceMapping != null) {
                String priceStr = getFieldValue(jsonMessage, priceMapping);
                if (priceStr != null && !priceStr.isEmpty()) {
                    dto.setTradePrice(Double.parseDouble(priceStr));
                }
            }
            // tradeVolume 필드 추출
            String volumeMapping = mapping.get("tradeVolume");
            if (volumeMapping != null) {
                String volStr = getFieldValue(jsonMessage, volumeMapping);
                if (volStr != null && !volStr.isEmpty()) {
                    dto.setTradeVolume(Double.parseDouble(volStr));
                }
            }
            // tradeTimestamp 필드 추출
            String timeMapping = mapping.get("tradeTimestamp");
            if (timeMapping != null) {
                String timeStr = getFieldValue(jsonMessage, timeMapping);
                if (timeStr != null && !timeStr.isEmpty()) {
                    dto.setTradeTimestamp(Long.parseLong(timeStr));
                }
            }
            // askBid 필드 추출 (boolean 형태의 문자열이면 변환)
            String askBidMapping = mapping.get("askBid");
            if (askBidMapping != null) {
                String askBidValue = getFieldValue(jsonMessage, askBidMapping);
                if (askBidValue != null && (askBidValue.equalsIgnoreCase("true") || askBidValue.equalsIgnoreCase("false"))) {
                    boolean isMaker = Boolean.parseBoolean(askBidValue);
                    dto.setAskBid(isMaker ? "ASK" : "BID");
                } else {
                    dto.setAskBid(askBidValue);
                }
            }
        } catch (Exception e) {
            log.error("Error in dynamic mapping {}: {}", exchange, e.getMessage(), e);
            return null;
        }
        return dto;
    }

    private String getFieldValue(String json, String jsonPathExpression) {
        try {
            Object value = JsonPath.read(json, jsonPathExpression);
            return value != null ? value.toString() : null;
        } catch (PathNotFoundException e) {
            log.warn("JSONPath expression not found: {}", jsonPathExpression);
            return null;
        } catch (Exception e) {
            log.error("Error evaluating JSONPath expression {}: {}", jsonPathExpression, e.getMessage(), e);
            return null;
        }
    }

    // 코인 형식 정규화
    private String unifyCode(String original) {
        if (original == null) return null;
        if (original.startsWith("KRW-")) {
            return original.substring(4);
        }
        if (original.endsWith("USDT")) {
            return original.substring(0, original.length() - 4);
        }
        return original;
    }
}