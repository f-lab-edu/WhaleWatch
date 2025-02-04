package com.whalewatch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public abstract class AbstractWebSocketService {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected final ParsingService parsingService;
    protected WebSocketSession session;

    public AbstractWebSocketService(ParsingService parsingService) {
        this.parsingService = parsingService;
    }

    public abstract String getExchangeName();

    public abstract WebSocketHandler getHandler();

    public abstract String getUrl();

    public void startConnection() {
        try {
            WebSocketClient client = new StandardWebSocketClient();
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
            URI uri = new URI(getUrl());
            session = client.doHandshake(getHandler(), headers, uri).get();
            log.info("[{}] WebSocket connected: {}", getExchangeName(), uri);
        } catch (InterruptedException | ExecutionException e) {
            log.error("[{}] WebSocket connection error: {}", getExchangeName(), e.getMessage(), e);
        } catch (Exception ex) {
            log.error("[{}] Unexpected error: {}", getExchangeName(), ex.getMessage(), ex);
        }
    }

    public void stopConnection() {
        if (session != null && session.isOpen()) {
            try {
                session.close();
                log.info("[{}] WebSocket connection closed.", getExchangeName());
            } catch (Exception e) {
                log.error("[{}] Error closing connection: {}", getExchangeName(), e.getMessage());
            }
        }
    }
}
