package com.whalewatch.service;

import java.io.Serializable;

public class ThresholdSettingData implements Serializable {
    private String coin;
    private Double threshold;

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