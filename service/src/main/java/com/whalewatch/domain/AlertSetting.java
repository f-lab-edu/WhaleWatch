package com.whalewatch.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AlertSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String coin;
    private int threshold;
    private boolean notifyByEmail;

    protected AlertSetting() {}

    public AlertSetting(String coin, int threshold, boolean notifyByEmail) {
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

    public int getThreshold() {
        return threshold;
    }

    public boolean isNotifyByEmail() {
        return notifyByEmail;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public void setNotifyByEmail(boolean notifyByEmail) {
        this.notifyByEmail = notifyByEmail;
    }
}
