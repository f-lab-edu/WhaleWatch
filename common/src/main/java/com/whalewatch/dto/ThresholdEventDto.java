package com.whalewatch.dto;

import java.io.Serializable;

public class ThresholdEventDto implements Serializable {
    private Long chatId;
    private String coin;
    private Double threshold;

    public ThresholdEventDto() {
    }

    public ThresholdEventDto(Long chatId, String coin, Double threshold) {
        this.chatId = chatId;
        this.coin = coin;
        this.threshold = threshold;
    }

    public Long getChatId() {
        return chatId;
    }
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getCoin() {
        return coin;
    }
    public void setCoin(String coin) {
        this.coin = coin;
    }

    public Double getThreshold() {
        return threshold;
    }
    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }
}
