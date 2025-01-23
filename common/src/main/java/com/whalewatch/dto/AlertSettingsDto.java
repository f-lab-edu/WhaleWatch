package com.whalewatch.dto;

public class AlertSettingsDto {
    private int id;
    private Integer userId;
    private String coin;
    private double threshold;
    private boolean notifyByEmail;

    public AlertSettingsDto(int id,String coin,int userid, double threshold, boolean notifyByEmail) {
        this.id = id;
        this.userId = userid;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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
}
