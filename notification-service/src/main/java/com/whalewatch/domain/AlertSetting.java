package com.whalewatch.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "alert_setting")
public class AlertSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Long chatId;

    private String coin;
    private double threshold;

    public AlertSetting() {}

    public AlertSetting(String coin, double threshold) {
        this.coin = coin;
        this.threshold = threshold;
    }

    public int getId() {
        return id;
    }

    public String getCoin() {
        return coin;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
