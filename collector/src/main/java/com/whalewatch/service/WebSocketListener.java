package com.whalewatch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.nio.charset.StandardCharsets;

public class WebSocketListener extends BinaryWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(WebSocketListener.class);

    private final ParsingService parsingService;

    public WebSocketListener(ParsingService parsingService) {
        this.parsingService = parsingService;
    }

    //연결
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Listener Connected: {}", session.getRemoteAddress());

        String subscriptionJson = "[" +
                "{\"ticket\":\"test\"}," +
                "{\"type\":\"trade\",\"codes\":[\"KRW-BTC\",\"KRW-ETH\"]}," +
                "{\"format\":\"DEFAULT\"}" +
                "]";
        session.sendMessage(new TextMessage(subscriptionJson));
        log.info("message: {}", subscriptionJson);
    }

    //메시지 수신
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        // Binary 데이터를 String으로 변환
        String payload = new String(message.getPayload().array(), StandardCharsets.UTF_8);

        try {
            parsingService.parsingMessage(payload);  // JSON 변환 및 필터링
        } catch (Exception e) {
            log.error("Error parsing WebSocket message: {}", payload, e);
        }
    }

    //에러 발생
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Error: ", exception);
    }

    //연결 종료
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Closed Status: {}", status);
    }





}
