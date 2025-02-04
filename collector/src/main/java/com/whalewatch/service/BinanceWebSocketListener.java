package com.whalewatch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


public class BinanceWebSocketListener extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(BinanceWebSocketListener.class);
    private final ParsingService parsingService;

    public BinanceWebSocketListener(ParsingService parsingService) {
        this.parsingService = parsingService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("[Binance Listener] Connected: {}", session.getRemoteAddress());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        try {
            parsingService.parsingMessage(payload);
        } catch (Exception e) {
            log.error("[Binance Listener] Parse error", e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("[Binance Listener] Transport error: ", exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("[Binance Listener] Closed, status: {}", status);
    }
}
