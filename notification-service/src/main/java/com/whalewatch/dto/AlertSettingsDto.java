package com.whalewatch.dto;

public class AlertSettingsDto {
    private int id;
    private String coin;
    private double threshold;

    public AlertSettingsDto(int id,String coin, double threshold) {
        this.id = id;
        this.coin = coin;
        this.threshold = threshold;
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

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

}
