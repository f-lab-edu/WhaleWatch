package com.whalewatch.controller;

import com.whalewatch.dto.DlqMessageDto;
import com.whalewatch.kafka.DlqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DLQ(Dead Letter Queue) 모니터링 API
 */
@RestController
@RequestMapping("/api/dlq")
public class DlqController {

    private static final Logger log = LoggerFactory.getLogger(DlqController.class);
    
    private final DlqService dlqService;

    public DlqController(DlqService dlqService) {
        this.dlqService = dlqService;
    }

    /**
     * DLQ 통계 조회
     * GET /api/dlq/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = dlqService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * 모든 DLQ 토픽 목록 조회
     * GET /api/dlq/topics
     */
    @GetMapping("/topics")
    public ResponseEntity<Set<String>> getDlqTopics() {
        Set<String> topics = dlqService.getDlqTopics();
        return ResponseEntity.ok(topics);
    }

    /**
     * 모든 DLQ 메시지 조회
     * GET /api/dlq/messages
     */
    @GetMapping("/messages")
    public ResponseEntity<List<DlqMessageDto>> getAllMessages() {
        List<DlqMessageDto> messages = dlqService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    /**
     * 최근 N개 DLQ 메시지 조회
     * GET /api/dlq/messages/recent?limit=10
     */
    @GetMapping("/messages/recent")
    public ResponseEntity<List<DlqMessageDto>> getRecentMessages(
            @RequestParam(defaultValue = "10") int limit) {
        List<DlqMessageDto> messages = dlqService.getRecentMessages(limit);
        return ResponseEntity.ok(messages);
    }

    /**
     * 특정 DLQ 토픽의 메시지 조회
     * GET /api/dlq/messages/topic/{topicName}
     */
    @GetMapping("/messages/topic/{topicName}")
    public ResponseEntity<List<DlqMessageDto>> getMessagesByTopic(@PathVariable String topicName) {
        // .dlq 접미사 추가 (없는 경우)
        String dlqTopic = topicName.endsWith(".dlq") ? topicName : topicName + ".dlq";
        List<DlqMessageDto> messages = dlqService.getMessagesByTopic(dlqTopic);
        return ResponseEntity.ok(messages);
    }

    /**
     * 특정 DLQ 메시지 상세 조회
     * GET /api/dlq/messages/{id}
     */
    @GetMapping("/messages/{id}")
    public ResponseEntity<DlqMessageDto> getMessageById(@PathVariable String id) {
        return dlqService.getMessageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 특정 시간 이후의 DLQ 메시지 조회
     * GET /api/dlq/messages/since?hours=1
     */
    @GetMapping("/messages/since")
    public ResponseEntity<List<DlqMessageDto>> getMessagesSince(
            @RequestParam(defaultValue = "1") int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        List<DlqMessageDto> messages = dlqService.getMessagesSince(since);
        return ResponseEntity.ok(messages);
    }

    /**
     * DLQ 메시지 삭제 (재처리 완료 후)
     * DELETE /api/dlq/messages/{id}
     */
    @DeleteMapping("/messages/{id}")
    public ResponseEntity<Map<String, Object>> deleteMessage(@PathVariable String id) {
        boolean deleted = dlqService.deleteMessage(id);
        if (deleted) {
            log.info("[DlqController] Deleted DLQ message: id={}", id);
            return ResponseEntity.ok(Map.of("success", true, "id", id));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 전체 DLQ 메시지 수 조회
     * GET /api/dlq/count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getTotalCount() {
        int count = dlqService.getTotalMessageCount();
        return ResponseEntity.ok(Map.of("totalCount", count));
    }

    /**
     * 특정 토픽의 DLQ 메시지 수 조회
     * GET /api/dlq/count/{topicName}
     */
    @GetMapping("/count/{topicName}")
    public ResponseEntity<Map<String, Object>> getCountByTopic(@PathVariable String topicName) {
        String dlqTopic = topicName.endsWith(".dlq") ? topicName : topicName + ".dlq";
        int count = dlqService.getMessageCount(dlqTopic);
        return ResponseEntity.ok(Map.of("topic", dlqTopic, "count", count));
    }
}
