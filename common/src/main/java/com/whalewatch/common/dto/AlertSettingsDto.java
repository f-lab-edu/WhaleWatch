package com.whalewatch.common.dto;

public class AlertSettingsDto {
    private String coin;
    private int threshold;
    private boolean notifyByEmail;

    public AlertSettingsDto() {}

    public AlertSettingsDto(String coin, int threshold, boolean notifyByEmail) {
        this.coin = coin;
        this.threshold = threshold;
        this.notifyByEmail = notifyByEmail;
    }

    public String getCoin() { return coin; }
    public int getThreshold() { return threshold; }
    public boolean isNotifyByEmail() { return notifyByEmail; }
}