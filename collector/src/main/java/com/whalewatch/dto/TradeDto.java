package com.whalewatch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeDto {

    private String type;
    private String code;

    @JsonProperty("trade_price")
    private Double tradePrice;

    @JsonProperty("trade_volume")
    private Double tradeVolume;

    @JsonProperty("ask_bid")
    private String askBid;

    @JsonProperty("change_price")
    private Double changePrice;

    private Long timestamp;
    @JsonProperty("trade_timestamp")
    private Long tradeTimestamp;

    private String exchange;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getAskBid() {
        return askBid;
    }

    public void setAskBid(String askBid) {
        this.askBid = askBid;
    }


    public Double getChangePrice() {
        return changePrice;
    }

    public void setChangePrice(Double changePrice) {
        this.changePrice = changePrice;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTradeTimestamp() {
        return tradeTimestamp;
    }

    public void setTradeTimestamp(Long tradeTimestamp) {
        this.tradeTimestamp = tradeTimestamp;
    }

    public String getExchange() {
        return exchange;
    }
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    @Override
    public String toString() {
        return "TradeDto{" +
                "type='" + type + '\'' +
                ", code='" + code + '\'' +
                ", tradePrice=" + tradePrice +
                ", tradeVolume=" + tradeVolume +
                ", askBid='" + askBid + '\'' +
                ", changePrice=" + changePrice +
                ", timestamp=" + timestamp +
                ", tradeTimestamp=" + tradeTimestamp +
                ", exchange='" + exchange + '\'' +
                '}';
    }
}
