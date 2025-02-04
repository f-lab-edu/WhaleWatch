package com.whalewatch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.nio.charset.StandardCharsets;

public class UpbitWebSocketListener extends BinaryWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(UpbitWebSocketListener.class);
    private final ParsingService parsingService;

    public UpbitWebSocketListener(ParsingService parsingService) {
        this.parsingService = parsingService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("[Upbit Listener] Connected: {}", session.getRemoteAddress());
        // Request 전송
        String subscriptionJson = "[" +
                "{\"ticket\":\"test\"}," +
                "{\"type\":\"trade\",\"codes\":[\"KRW-BTC\",\"KRW-ETH\",\"KRW-SOL\"]}," +
                "{\"format\":\"DEFAULT\"}" +
                "]";
        session.sendMessage(new TextMessage(subscriptionJson));
        log.info("[Upbit Listener] Sent subscription message: {}", subscriptionJson);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        String payload = new String(message.getPayload().array(), StandardCharsets.UTF_8);
        try {
            parsingService.parsingMessage(payload);
        } catch (Exception e) {
            log.error("[Upbit Listener] Error parsing message: {}", payload, e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("[Upbit Listener] Transport error: ", exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("[Upbit Listener] Closed, status: {}", status);
    }
}
