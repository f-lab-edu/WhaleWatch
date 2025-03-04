package com.whalewatch.dto;

import java.time.LocalDateTime;

public class UserAlertDto {
    private int id;
    private String coin;
    private Double tradePrice;
    private Double tradeVolume;
    private Long tradeTimestamp;
    private LocalDateTime alertedAt;

    public UserAlertDto(int id,String coin, Double tradePrice, Double tradeVolume, Long tradeTimestamp, LocalDateTime alertedAt) {
        this.id = id;
        this.coin = coin;
        this.tradePrice = tradePrice;
        this.tradeVolume = tradeVolume;
        this.tradeTimestamp = tradeTimestamp;
        this.alertedAt = alertedAt;
    }

    // getters, setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCoin() { return coin; }
    public void setCoin(String coin) { this.coin = coin; }

    public Double getTradePrice() { return tradePrice; }
    public void setTradePrice(Double tradePrice) { this.tradePrice = tradePrice; }

    public Double getTradeVolume() { return tradeVolume; }
    public void setTradeVolume(Double tradeVolume) { this.tradeVolume = tradeVolume; }

    public Long getTradeTimestamp() { return tradeTimestamp; }
    public void setTradeTimestamp(Long tradeTimestamp) { this.tradeTimestamp = tradeTimestamp; }

    public LocalDateTime getAlertedAt() { return alertedAt; }
    public void setAlertedAt(LocalDateTime alertedAt) { this.alertedAt = alertedAt; }
}
