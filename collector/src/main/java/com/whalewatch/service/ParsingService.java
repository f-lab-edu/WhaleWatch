package com.whalewatch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whalewatch.TradeDto;
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

    // 업비트 메시지 파싱
    public void parsingMessage(String jsonMessage) {
        try {
            JsonNode root = objectMapper.readTree(jsonMessage);

            // 바이낸스 root -> "stream"
            if(root.has("stream")){
                TradeDto dto = parseBinance(root);
                if(dto != null){
                    dto.setCode(unifyCode(dto.getCode()));
                    dto.setExchange("BINANCE");
                    filteringService.adminFiltering(dto);
                }
            }else{
                // 업비트 root는 "stream" 필드 X
                TradeDto dto = objectMapper.readValue(jsonMessage,TradeDto.class);

                dto.setCode(unifyCode(dto.getCode()));
                dto.setExchange("UPBIT");
                filteringService.adminFiltering(dto);
            }
        }catch (Exception e){
            log.error("[ParsingService] parseMessage fail: {}", jsonMessage, e);
        }
    }

    private TradeDto parseBinance(JsonNode root){
        JsonNode data = root.path("data");
        if (data.isMissingNode()) return null;

        String eventType = data.path("e").asText("");
        String symbol = data.path("s").asText("");
        double price = data.path("p").asDouble(0.0);
        double volume = data.path("q").asDouble(0.0);
        long tradeTime = data.path("T").asLong(0L);
        boolean isMaker = data.path("m").asBoolean(false);

        TradeDto dto = new TradeDto();
        dto.setType(eventType);
        dto.setCode(symbol);
        dto.setTradePrice(price);
        dto.setTradeVolume(volume);
        dto.setTradeTimestamp(tradeTime);
        dto.setAskBid(isMaker ? "ASK" : "BID");

        return dto;
    }

    // 업비트, 바이낸스 코드값 통일
    private String unifyCode(String original){
        if (original == null) return null;
        // 업비트: "KRW-BTC" -> "BTC"
        if (original.startsWith("KRW-")) {
            return original.substring(4);
        }
        // 바이낸스: "BTCUSDT" -> "BTC"
        if (original.endsWith("USDT")) {
            return original.substring(0, original.length() - 4);
        }
        return original;
    }
}
