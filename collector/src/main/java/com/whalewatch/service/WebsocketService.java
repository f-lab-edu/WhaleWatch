package com.whalewatch.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

@Service
public class WebsocketService {
    private static final Logger log = LoggerFactory.getLogger(WebsocketService.class);

    private final ParsingService parsingService;
    private WebSocketSession session;

    public WebsocketService(ParsingService parsingService) {
        this.parsingService = parsingService;
    }

    @PostConstruct
    public void init() {
        startConnection();
    }

    public void startConnection() {
        try {
            WebSocketClient client = new StandardWebSocketClient();
            WebSocketListener listener = new WebSocketListener(parsingService);
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

            headers.setSecWebSocketProtocol(Collections.singletonList("json"));

            URI uri = new URI("wss://api.upbit.com/websocket/v1");

            session = client.doHandshake(listener, headers, uri).get();

            log.info("WebSocket connection: {}", uri);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to WebSocket connection", e);
        } catch (Exception ex) {
            log.error("Unexpected error", ex);
        }
    }

}
