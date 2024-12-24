package com.whalewatch.common.dto;

public class TransactionDto {
    private int id;
    private String hash;
    private String coin;
    private int amount;

    public TransactionDto() {}

    public TransactionDto(int id, String hash, String coin, int amount) {
        this.id = id;
        this.hash = hash;
        this.coin = coin;
        this.amount = amount;
    }

    public int getId() { return id; }
    public String getHash() { return hash; }
    public String getCoin() { return coin; }
    public int getAmount() { return amount; }
}
