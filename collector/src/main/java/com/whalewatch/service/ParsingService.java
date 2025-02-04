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

    // 웹소켓에서 수신한 메시지를 파싱하는 메서드
    public void parsingMessage(String jsonMessage) {
        try {
            JsonNode root = objectMapper.readTree(jsonMessage);
            String exchange;
            // 간단한 규칙: "stream" 필드가 있으면 Binance, 없으면 Upbit로 판단
            if (root.has("stream")) {
                exchange = "binance";
            } else {
                exchange = "upbit";
            }
            // 실제 동적 매핑 구현
            TradeDto dto = parseDynamic(exchange, root);
            if (dto != null) {
                // 코드 값을 정규화 (예: "KRW-BTC" → "BTC", "BTCUSDT" → "BTC")
                dto.setCode(unifyCode(dto.getCode()));
                dto.setExchange(exchange.toUpperCase());
                // 파싱된 데이터를 필터링 서비스로 전달
                filteringService.adminFiltering(dto);
            }
        } catch (Exception e) {
            log.error("[ParsingService] parseMessage fail: {}", jsonMessage, e);
        }
    }

    // 실제 동적 매핑 구현 메서드
    private TradeDto parseDynamic(String exchange, JsonNode root) {
        // 설정 파일에서 해당 거래소의 설정(매핑 규칙)을 조회합니다.
        ExchangesProperties.ExchangeConfig config = exchangesProperties.getExchanges().get(exchange.toLowerCase());
        if (config == null) {
            log.error("No configuration found for exchange: {}", exchange);
            return null;
        }
        Map<String, String> mapping = config.getMapping();
        if (mapping == null || mapping.isEmpty()) {
            log.error("No mapping rules defined for exchange: {}", exchange);
            return null;
        }
        TradeDto dto = new TradeDto();
        try {
            // 각 필드에 대해 mapping 규칙을 적용하여 JSON에서 값을 추출합니다.
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
                // 만약 askBid 값이 boolean 형태의 문자열이면 변환하여 저장
                if (askBidValue != null && (askBidValue.equalsIgnoreCase("true") || askBidValue.equalsIgnoreCase("false"))) {
                    boolean isMaker = Boolean.parseBoolean(askBidValue);
                    dto.setAskBid(isMaker ? "ASK" : "BID");
                } else {
                    dto.setAskBid(askBidValue);
                }
            }
        } catch (Exception e) {
            log.error("Error in dynamic mapping for exchange {}: {}", exchange, e.getMessage(), e);
            return null;
        }
        return dto;
    }

    // dot(.) 구분자로 지정된 JSON 경로를 따라 값을 추출하는 헬퍼 메서드
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

    // 코드 정규화: 예를 들어 "KRW-BTC"는 "BTC"로, "BTCUSDT"는 "BTC"로 변경
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