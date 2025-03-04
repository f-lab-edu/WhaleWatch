package com.whalewatch.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "app_transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String coin;
    private Double tradePrice;   // 체결 가격
    private Double tradeVolume;  // 체결량
    private String askBid;       // "ASK" or "BID"
    private Long tradeTimestamp;

    public Transaction() {
    }

    public Transaction(String coin, Double tradePrice,
                       Double tradeVolume, String askBid, Long tradeTimestamp) {
        this.coin = coin;
        this.tradePrice = tradePrice;
        this.tradeVolume = tradeVolume;
        this.askBid = askBid;
        this.tradeTimestamp = tradeTimestamp;
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

    public String getAskBid() {
        return askBid;
    }

    public void setAskBid(String askBid) {
        this.askBid = askBid;
    }

    public Long getTradeTimestamp() {
        return tradeTimestamp;
    }

    public void setTradeTimestamp(Long tradeTimestamp) {
        this.tradeTimestamp = tradeTimestamp;
    }
}
