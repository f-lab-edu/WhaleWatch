package com.whalewatch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.nio.charset.StandardCharsets;

public class WebSocketListener extends AbstractWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(WebSocketListener.class);

    private final ParsingService parsingService;
    private final String exchangeName;
    private final boolean isBinary;
    private final String subscriptionJson;

    public WebSocketListener(ParsingService parsingService,
                                      String exchangeName,
                                      boolean isBinary,
                                      String subscriptionJson) {
        this.parsingService = parsingService;
        this.exchangeName = exchangeName;
        this.isBinary = isBinary;
        this.subscriptionJson = subscriptionJson;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("[{} Listener] Connected: {}", exchangeName, session.getRemoteAddress());

        // Request시 subscription을 보내야 하는 경우
        if (subscriptionJson != null) {
            session.sendMessage(new TextMessage(subscriptionJson));
            log.info("[{} Listener] Sent subscription message: {}", exchangeName, subscriptionJson);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        try {
            parsingService.parsingMessage(payload);
        } catch (Exception e) {
            log.error("[{} Listener] Parse error", exchangeName, e);
        }
    }


    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        String payload = new String(message.getPayload().array(), StandardCharsets.UTF_8);
        try {
            parsingService.parsingMessage(payload);
        } catch (Exception e) {
            log.error("[{} Listener] Error parsing message: {}", exchangeName, payload, e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("[{} Listener] Transport error: ", exchangeName, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("[{} Listener] Closed, status: {}", exchangeName, status);
    }
}
