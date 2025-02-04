package com.whalewatch.service;

import com.whalewatch.ExchangesProperties;
import org.springframework.web.socket.WebSocketHandler;

import java.util.Map;

public class ExchangeWebsocketService extends AbstractWebSocketService {

    private final String exchangeName;
    private final ExchangesProperties.ExchangeConfig config;

    public ExchangeWebsocketService(String exchangeName, ExchangesProperties.ExchangeConfig config, ParsingService parsingService) {
        super(parsingService);
        this.exchangeName = exchangeName.toUpperCase();
        this.config = config;
    }

    @Override
    public String getExchangeName() {
        return exchangeName;
    }

    @Override
    public WebSocketHandler getHandler() {
        if ("binary".equalsIgnoreCase(config.getWebsocketType())) {
            return new UpbitWebSocketListener(parsingService);
        } else {
            return new BinanceWebSocketListener(parsingService);
        }
    }

    @Override
    public String getUrl() {
        return config.getUrl();
    }

    public Map<String, String> getMapping() {
        return config.getMapping();
    }

    public Map<String, Double> getThreshold() {
        return config.getThreshold();
    }
}
