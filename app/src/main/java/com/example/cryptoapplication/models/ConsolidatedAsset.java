package com.example.cryptoapplication.models;

/**
 * Represents a consolidated view of user's holdings for a specific coin
 * Shows total quantity, current price, and total value
 */
public class ConsolidatedAsset {
    private String coinId;
    private String coinName;
    private String coinSymbol;
    private double totalQuantity;
    private double currentPrice;
    private double totalValue;
    private int transactionCount;
    
    public ConsolidatedAsset() {}
    
    public ConsolidatedAsset(String coinId, String coinName, String coinSymbol) {
        this.coinId = coinId;
        this.coinName = coinName;
        this.coinSymbol = coinSymbol;
        this.totalQuantity = 0.0;
        this.currentPrice = 0.0;
        this.totalValue = 0.0;
        this.transactionCount = 0;
    }
    
    // Getters and Setters
    public String getCoinId() { return coinId; }
    public void setCoinId(String coinId) { this.coinId = coinId; }
    
    public String getCoinName() { return coinName; }
    public void setCoinName(String coinName) { this.coinName = coinName; }
    
    public String getCoinSymbol() { return coinSymbol; }
    public void setCoinSymbol(String coinSymbol) { this.coinSymbol = coinSymbol; }
    
    public double getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(double totalQuantity) { this.totalQuantity = totalQuantity; }
    
    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }
    
    public double getTotalValue() { return totalValue; }
    public void setTotalValue(double totalValue) { this.totalValue = totalValue; }
    
    public int getTransactionCount() { return transactionCount; }
    public void setTransactionCount(int transactionCount) { this.transactionCount = transactionCount; }
}