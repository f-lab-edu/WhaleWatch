package com.whalewatch.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "alert_setting")
public class AlertSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Integer userId;

    private Long chatId;

    private String coin;
    private double threshold;
    private boolean notifyByEmail;

    public AlertSetting() {}

    public AlertSetting(String coin, double threshold, boolean notifyByEmail) {
        this.coin = coin;
        this.threshold = threshold;
        this.notifyByEmail = notifyByEmail;
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

    public boolean isNotifyByEmail() {
        return notifyByEmail;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public void setNotifyByEmail(boolean notifyByEmail) {
        this.notifyByEmail = notifyByEmail;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
