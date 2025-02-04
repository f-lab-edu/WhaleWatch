package com.whalewatch.service;

import com.whalewatch.ExchangesProperties;
import org.springframework.web.socket.WebSocketHandler;

import java.util.Map;

public class WebsocketService extends AbstractWebSocketService {

    private final String exchangeName;
    private final ExchangesProperties.ExchangeConfig config;

    public WebsocketService(String exchangeName, ExchangesProperties.ExchangeConfig config, ParsingService parsingService) {
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
        boolean isBinary = "binary".equalsIgnoreCase(config.getWebsocketType());

        String subscriptionJson = null;
        if (isBinary) {
            subscriptionJson = "[" +
                    "{\"ticket\":\"test\"}," +
                    "{\"type\":\"trade\",\"codes\":[\"KRW-BTC\",\"KRW-ETH\",\"KRW-SOL\"]}," +
                    "{\"format\":\"DEFAULT\"}" +
                    "]";
        }

        return new WebSocketListener(
                parsingService,
                exchangeName,
                isBinary,
                subscriptionJson
        );
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
