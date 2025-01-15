package com.whalewatch.dto;

public class AlertSettingsDto {
    private int id;
    private String coin;
    private double threshold;
    private boolean notifyByEmail;

    public AlertSettingsDto(int id,String coin, double threshold, boolean notifyByEmail) {
        this.id = id;
        this.coin = coin;
        this.threshold = threshold;
        this.notifyByEmail = notifyByEmail;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public void setNotifyByEmail(boolean notifyByEmail) {
        this.notifyByEmail = notifyByEmail;
    }
}
