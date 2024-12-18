package com.whalewatch.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String hash;
    private String coin;
    private int amount;

    protected Transaction() {
    }

    public Transaction(String hash, String coin, int amount) {
        this.hash = hash;
        this.coin = coin;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    public String getCoin() {
        return coin;
    }

    public int getAmount() {
        return amount;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
