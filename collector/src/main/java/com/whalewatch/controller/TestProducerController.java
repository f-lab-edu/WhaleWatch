package com.whalewatch.controller;

import com.whalewatch.dto.TransactionEventDto;
import com.whalewatch.kafka.TransactionProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 부하테스트용 Producer API
 * k6 부하테스트에서 사용
 */
@RestController
@RequestMapping("/api/test")
public class TestProducerController {

    private static final Logger log = LoggerFactory.getLogger(TestProducerController.class);
    private final TransactionProducer transactionProducer;
    private final Random random = new Random();

    public TestProducerController(TransactionProducer transactionProducer) {
        this.transactionProducer = transactionProducer;
    }

    /**
     * 단일 메시지 전송
     * POST /api/test/send
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody TransactionEventDto event) {
        try {
            transactionProducer.sendTransactionEvent(event);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Message sent successfully",
                    "coin", event.getCoin()
            ));
        } catch (Exception e) {
            log.error("Failed to send test message: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * 랜덤 메시지 전송 (부하테스트용)
     * POST /api/test/send/random
     */
    @PostMapping("/send/random")
    public ResponseEntity<Map<String, Object>> sendRandomMessage() {
        String[] coins = {"BTC", "ETH", "SOL"};
        String coin = coins[random.nextInt(coins.length)];
        
        TransactionEventDto event = new TransactionEventDto(
                0,
                coin,
                50000.0 + random.nextDouble() * 10000,
                1.0 + random.nextDouble() * 10,
                random.nextBoolean() ? "ASK" : "BID",
                System.currentTimeMillis()
        );

        try {
            transactionProducer.sendTransactionEvent(event);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Random message sent",
                    "coin", coin
            ));
        } catch (Exception e) {
            log.error("Failed to send random message: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * 배치 메시지 전송
     * POST /api/test/send/batch?count=100
     */
    @PostMapping("/send/batch")
    public ResponseEntity<Map<String, Object>> sendBatchMessages(@RequestParam(defaultValue = "100") int count) {
        String[] coins = {"BTC", "ETH", "SOL"};
        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < count; i++) {
            String coin = coins[random.nextInt(coins.length)];
            TransactionEventDto event = new TransactionEventDto(
                    0,
                    coin,
                    50000.0 + random.nextDouble() * 10000,
                    1.0 + random.nextDouble() * 10,
                    random.nextBoolean() ? "ASK" : "BID",
                    System.currentTimeMillis()
            );

            try {
                transactionProducer.sendTransactionEvent(event);
                successCount++;
            } catch (Exception e) {
                failCount++;
                log.error("Failed to send batch message {}: {}", i, e.getMessage());
            }
        }

        return ResponseEntity.ok(Map.of(
                "total", count,
                "success", successCount,
                "failed", failCount,
                "successRate", (double) successCount / count * 100
        ));
    }
}
