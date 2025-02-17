package com.whalewatch.dto;

public class TransactionEventDto {
    private int id;
    private String coin;
    private Double tradePrice;
    private Double tradeVolume;
    private String askBid;
    private Long tradeTimestamp;

    public TransactionEventDto() {
    }

    public TransactionEventDto(int id, String coin, Double tradePrice, Double tradeVolume, String askBid, Long tradeTimestamp) {
        this.id = id;
        this.coin = coin;
        this.tradePrice = tradePrice;
        this.tradeVolume = tradeVolume;
        this.askBid = askBid;
        this.tradeTimestamp = tradeTimestamp;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCoin() { return coin; }
    public void setCoin(String coin) { this.coin = coin; }

    public Double getTradePrice() { return tradePrice; }
    public void setTradePrice(Double tradePrice) { this.tradePrice = tradePrice; }

    public Double getTradeVolume() { return tradeVolume; }
    public void setTradeVolume(Double tradeVolume) { this.tradeVolume = tradeVolume; }

    public String getAskBid() { return askBid; }
    public void setAskBid(String askBid) { this.askBid = askBid; }

    public Long getTradeTimestamp() { return tradeTimestamp; }
    public void setTradeTimestamp(Long tradeTimestamp) { this.tradeTimestamp = tradeTimestamp; }
}
