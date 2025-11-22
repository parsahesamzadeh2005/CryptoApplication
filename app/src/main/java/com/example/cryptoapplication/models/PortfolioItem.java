package com.example.cryptoapplication.models;

public class PortfolioItem {
    private long id;
    private long userId;
    private String coinId;
    private double quantity;
    private double purchasePrice;
    private long purchaseDate;
    private double currentPrice;
    private double totalValue;
    private double profitLoss;
    private double transactionFee;
    private String transactionType;
    private long createdAt;
    private long updatedAt;

    public PortfolioItem() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getCoinId() { return coinId; }
    public void setCoinId(String coinId) { this.coinId = coinId; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public double getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(double purchasePrice) { this.purchasePrice = purchasePrice; }

    public long getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(long purchaseDate) { this.purchaseDate = purchaseDate; }

    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }

    public double getTotalValue() { return totalValue; }
    public void setTotalValue(double totalValue) { this.totalValue = totalValue; }

    public double getProfitLoss() { return profitLoss; }
    public void setProfitLoss(double profitLoss) { this.profitLoss = profitLoss; }

    public double getTransactionFee() { return transactionFee; }
    public void setTransactionFee(double transactionFee) { this.transactionFee = transactionFee; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}