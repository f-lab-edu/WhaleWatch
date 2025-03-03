package com.whalewatch.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_alert")
public class UserAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String coin;
    private Double tradePrice;
    private Double tradeVolume;
    private Long tradeTimestamp;

    private LocalDateTime alertedAt;

    private Long chatId;
    private String askBid;


    protected UserAlert() {}

    public UserAlert(Long chatId,
                     String coin,
                     Double tradePrice,
                     Double tradeVolume,
                     String askBid,
                     Long tradeTimestamp) {
        this.chatId = chatId;
        this.coin = coin;
        this.tradePrice = tradePrice;
        this.tradeVolume = tradeVolume;
        this.askBid = askBid;
        this.tradeTimestamp = tradeTimestamp;
        this.alertedAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getAskBid() {
        return askBid;
    }

    public void setAskBid(String askBid) {
        this.askBid = askBid;
    }
}
