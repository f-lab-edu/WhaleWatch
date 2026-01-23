package com.whalewatch.dto;

import java.time.LocalDateTime;

/**
 * DLQ(Dead Letter Queue)에 저장된 실패 메시지 정보를 담는 DTO
 */
public class DlqMessageDto {

    private String id;
    private String originalTopic;
    private String dlqTopic;
    private int partition;
    private long offset;
    private String key;
    private String payload;
    private String errorMessage;
    private String exceptionType;
    private LocalDateTime failedAt;
    private int retryCount;

    public DlqMessageDto() {
    }

    public DlqMessageDto(String originalTopic, String dlqTopic, int partition, long offset,
                         String key, String payload, String errorMessage, String exceptionType) {
        this.id = originalTopic + "-" + partition + "-" + offset;
        this.originalTopic = originalTopic;
        this.dlqTopic = dlqTopic;
        this.partition = partition;
        this.offset = offset;
        this.key = key;
        this.payload = payload;
        this.errorMessage = errorMessage;
        this.exceptionType = exceptionType;
        this.failedAt = LocalDateTime.now();
        this.retryCount = 0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginalTopic() {
        return originalTopic;
    }

    public void setOriginalTopic(String originalTopic) {
        this.originalTopic = originalTopic;
    }

    public String getDlqTopic() {
        return dlqTopic;
    }

    public void setDlqTopic(String dlqTopic) {
        this.dlqTopic = dlqTopic;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(LocalDateTime failedAt) {
        this.failedAt = failedAt;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    @Override
    public String toString() {
        return "DlqMessageDto{" +
                "id='" + id + '\'' +
                ", originalTopic='" + originalTopic + '\'' +
                ", partition=" + partition +
                ", offset=" + offset +
                ", errorMessage='" + errorMessage + '\'' +
                ", failedAt=" + failedAt +
                '}';
    }
}
