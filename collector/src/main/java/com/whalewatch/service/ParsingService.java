package com.whalewatch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public void parsingMessage(String jsonMessage) {
        try {
            JsonNode root = objectMapper.readTree(jsonMessage);
            String exchange;
            if (root.has("stream")) {
                exchange = "binance";
            } else {
                exchange = "upbit";
            }
            TradeDto dto = parseDynamic(exchange, root);
            if (dto != null) {
                dto.setCode(unifyCode(dto.getCode()));
                dto.setExchange(exchange.toUpperCase());
                filteringService.adminFiltering(dto);
            }
        } catch (Exception e) {
            log.error("[ParsingService] parseMessage fail: {}", jsonMessage, e);
        }
    }
    // 설정파일 매핑을 통해 JSON에서 Dto 필드 추출
    private TradeDto parseDynamic(String exchange, JsonNode root) {
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
            String typeMapping = mapping.get("type");
            if (typeMapping != null) {
                dto.setType(getFieldValue(root, typeMapping));
            }
            String codeMapping = mapping.get("code");
            if (codeMapping != null) {
                dto.setCode(getFieldValue(root, codeMapping));
            }
            String priceMapping = mapping.get("tradePrice");
            if (priceMapping != null) {
                String priceStr = getFieldValue(root, priceMapping);
                if (priceStr != null && !priceStr.isEmpty()) {
                    dto.setTradePrice(Double.parseDouble(priceStr));
                }
            }
            String volumeMapping = mapping.get("tradeVolume");
            if (volumeMapping != null) {
                String volStr = getFieldValue(root, volumeMapping);
                if (volStr != null && !volStr.isEmpty()) {
                    dto.setTradeVolume(Double.parseDouble(volStr));
                }
            }
            String timeMapping = mapping.get("tradeTimestamp");
            if (timeMapping != null) {
                String timeStr = getFieldValue(root, timeMapping);
                if (timeStr != null && !timeStr.isEmpty()) {
                    dto.setTradeTimestamp(Long.parseLong(timeStr));
                }
            }
            String askBidMapping = mapping.get("askBid");
            if (askBidMapping != null) {
                String askBidValue = getFieldValue(root, askBidMapping);
                // askBid 값이 boolean 형태의 문자열이면 변환
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

    private String getFieldValue(JsonNode root, String mappingKey) {
        String[] parts = mappingKey.split("\\.");
        JsonNode current = root;
        for (String part : parts) {
            current = current.path(part);
            if (current.isMissingNode()) {
                return null;
            }
        }
        return current.asText();
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