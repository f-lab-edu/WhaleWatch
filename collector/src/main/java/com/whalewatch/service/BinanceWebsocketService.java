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
public class BinanceWebsocketService {

    private static final Logger log = LoggerFactory.getLogger(BinanceWebsocketService.class);

    private final ParsingService parsingService;
    private WebSocketSession session;

    public BinanceWebsocketService(ParsingService parsingService) {
        this.parsingService = parsingService;
    }

    @PostConstruct
    public void init() {
        startConnection();
    }

    public void startConnection() {
        try {
            WebSocketClient client = new StandardWebSocketClient();
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
            headers.setSecWebSocketProtocol(Collections.singletonList("json"));


            String combinedStreams = "btcusdt@trade/ethusdt@trade/solusdt@trade";

            String url = "wss://stream.binance.com:9443/stream?streams=" + combinedStreams;
            URI uri = new URI(url);

            BinanceWebSocketListener listener = new BinanceWebSocketListener(parsingService);
            session = client.doHandshake(listener, headers, uri).get();

            log.info("[Binance] WebSocket connected : {}", url);

        } catch (InterruptedException | ExecutionException e) {
            log.error("[Binance] Failed to connect WebSocket", e);
        } catch (Exception ex) {
            log.error("[Binance] Unexpected error", ex);
        }
    }
}

