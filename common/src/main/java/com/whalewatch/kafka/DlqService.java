package com.whalewatch.kafka;

import com.whalewatch.dto.DlqMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * DLQ(Dead Letter Queue) 메시지 관리 서비스
 * 인메모리 저장소를 사용하여 DLQ 메시지를 관리
 */
@Service
public class DlqService {

    private static final Logger log = LoggerFactory.getLogger(DlqService.class);
    private static final int MAX_MESSAGES = 1000; // 최대 저장 메시지 수

    // 인메모리 저장소 (토픽별로 분리)
    private final Map<String, List<DlqMessageDto>> dlqMessages = new ConcurrentHashMap<>();
    
    // 통계 정보
    private final Map<String, Long> messageCountByTopic = new ConcurrentHashMap<>();
    private final Map<String, Long> messageCountByExceptionType = new ConcurrentHashMap<>();

    /**
     * DLQ 메시지 저장
     */
    public void saveMessage(DlqMessageDto message) {
        String topic = message.getDlqTopic();
        
        dlqMessages.computeIfAbsent(topic, k -> Collections.synchronizedList(new ArrayList<>()));
        List<DlqMessageDto> messages = dlqMessages.get(topic);
        
        // 최대 메시지 수 초과 시 오래된 메시지 삭제
        if (messages.size() >= MAX_MESSAGES) {
            messages.remove(0);
        }
        
        messages.add(message);
        
        // 통계 업데이트
        messageCountByTopic.merge(topic, 1L, Long::sum);
        if (message.getExceptionType() != null) {
            messageCountByExceptionType.merge(message.getExceptionType(), 1L, Long::sum);
        }
        
        log.info("[DlqService] Saved DLQ message: id={}, topic={}, error={}",
                message.getId(), topic, message.getErrorMessage());
    }

    /**
     * 특정 DLQ 토픽의 메시지 조회
     */
    public List<DlqMessageDto> getMessagesByTopic(String dlqTopic) {
        return dlqMessages.getOrDefault(dlqTopic, Collections.emptyList());
    }

    /**
     * 모든 DLQ 메시지 조회
     */
    public List<DlqMessageDto> getAllMessages() {
        return dlqMessages.values().stream()
                .flatMap(List::stream)
                .sorted(Comparator.comparing(DlqMessageDto::getFailedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 최근 N개 메시지 조회
     */
    public List<DlqMessageDto> getRecentMessages(int limit) {
        return getAllMessages().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 특정 시간 이후의 메시지 조회
     */
    public List<DlqMessageDto> getMessagesSince(LocalDateTime since) {
        return getAllMessages().stream()
                .filter(m -> m.getFailedAt().isAfter(since))
                .collect(Collectors.toList());
    }

    /**
     * 특정 메시지 조회
     */
    public Optional<DlqMessageDto> getMessageById(String id) {
        return getAllMessages().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst();
    }

    /**
     * 특정 메시지 삭제 (재처리 후)
     */
    public boolean deleteMessage(String id) {
        for (List<DlqMessageDto> messages : dlqMessages.values()) {
            boolean removed = messages.removeIf(m -> m.getId().equals(id));
            if (removed) {
                log.info("[DlqService] Deleted DLQ message: id={}", id);
                return true;
            }
        }
        return false;
    }

    /**
     * DLQ 통계 조회
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMessages", getAllMessages().size());
        stats.put("messageCountByTopic", new HashMap<>(messageCountByTopic));
        stats.put("messageCountByExceptionType", new HashMap<>(messageCountByExceptionType));
        stats.put("currentStoredMessages", dlqMessages.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size())));
        return stats;
    }

    /**
     * 모든 DLQ 토픽 목록 조회
     */
    public Set<String> getDlqTopics() {
        return new HashSet<>(dlqMessages.keySet());
    }

    /**
     * 특정 토픽의 메시지 수 조회
     */
    public int getMessageCount(String dlqTopic) {
        return dlqMessages.getOrDefault(dlqTopic, Collections.emptyList()).size();
    }

    /**
     * 전체 메시지 수 조회
     */
    public int getTotalMessageCount() {
        return dlqMessages.values().stream()
                .mapToInt(List::size)
                .sum();
    }
}
