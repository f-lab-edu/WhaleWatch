package com.whalewatch.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_alert")
public class UserAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Integer userId;

    private String coin;
    private Double tradePrice;
    private Double tradeVolume;
    private Long tradeTimestamp;

    private LocalDateTime alertedAt;

    protected UserAlert() {}

    public UserAlert(Integer userId, String coin, Double tradePrice,
                     Double tradeVolume, Long tradeTimestamp) {
        this.userId = userId;
        this.coin = coin;
        this.tradePrice = tradePrice;
        this.tradeVolume = tradeVolume;
        this.tradeTimestamp = tradeTimestamp;
        this.alertedAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public Double getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(Double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public Double getTradeVolume() {
        return tradeVolume;
    }

    public void setTradeVolume(Double tradeVolume) {
        this.tradeVolume = tradeVolume;
    }

    public Long getTradeTimestamp() {
        return tradeTimestamp;
    }

    public void setTradeTimestamp(Long tradeTimestamp) {
        this.tradeTimestamp = tradeTimestamp;
    }

    public LocalDateTime getAlertedAt() {
        return alertedAt;
    }

    public void setAlertedAt(LocalDateTime alertedAt) {
        this.alertedAt = alertedAt;
    }
}
