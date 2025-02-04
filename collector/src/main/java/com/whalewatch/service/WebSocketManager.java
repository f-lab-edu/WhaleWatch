package com.whalewatch.service;

import com.whalewatch.ExchangesProperties;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WebSocketManager {

    private final Map<String, ExchangeWebsocketService> services;

    public WebSocketManager(ExchangesProperties exchangesProperties, ParsingService parsingService) {
        services = exchangesProperties.getExchanges().entrySet().stream()
                .filter(entry -> entry.getValue().isEnabled())
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toUpperCase(),
                        entry -> new ExchangeWebsocketService(entry.getKey(), entry.getValue(), parsingService)
                ));
    }

    // 모든 거래소의 웹소켓 연결
    public void startAll() {
        services.values().forEach(ExchangeWebsocketService::startConnection);
    }

    // 특정 거래소의 연결 종료
    public void stopExchange(String exchangeName) {
        ExchangeWebsocketService service = services.get(exchangeName.toUpperCase());
        if (service != null) {
            service.stopConnection();
        }
    }

    // 특정 거래소의 연결 재시작
    public void restartExchange(String exchangeName) {
        ExchangeWebsocketService service = services.get(exchangeName.toUpperCase());
        if (service != null) {
            service.stopConnection();
            service.startConnection();
        }
    }
}