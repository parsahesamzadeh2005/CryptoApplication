package com.example.cryptoapplication.models;

public class TransactionRecord {
    private long id;
    private long userId;
    private String type; // BUY or WITHDRAW
    private String coinId; // nullable for WITHDRAW
    private double quantity; // nullable for WITHDRAW, use 0
    private double pricePerCoin; // nullable for WITHDRAW, use 0
    private double fiatAmount; // total fiat delta (positive for BUY spent, positive for WITHDRAW withdrawn)
    private long timestamp;

    public TransactionRecord() {}

    public TransactionRecord(long id, long userId, String type, String coinId,
                             double quantity, double pricePerCoin, double fiatAmount, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.coinId = coinId;
        this.quantity = quantity;
        this.pricePerCoin = pricePerCoin;
        this.fiatAmount = fiatAmount;
        this.timestamp = timestamp;
    }

    public long getId() { return id; }
    public long getUserId() { return userId; }
    public String getType() { return type; }
    public String getCoinId() { return coinId; }
    public double getQuantity() { return quantity; }
    public double getPricePerCoin() { return pricePerCoin; }
    public double getFiatAmount() { return fiatAmount; }
    public long getTimestamp() { return timestamp; }

    public void setId(long id) { this.id = id; }
    public void setUserId(long userId) { this.userId = userId; }
    public void setType(String type) { this.type = type; }
    public void setCoinId(String coinId) { this.coinId = coinId; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public void setPricePerCoin(double pricePerCoin) { this.pricePerCoin = pricePerCoin; }
    public void setFiatAmount(double fiatAmount) { this.fiatAmount = fiatAmount; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}