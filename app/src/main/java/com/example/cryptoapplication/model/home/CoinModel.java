package com.example.cryptoapplication.model.home;

import com.google.gson.annotations.SerializedName;

/**
 * Model class for cryptocurrency data
 * Maps to CoinGecko API response format
 */
public class CoinModel {
    @SerializedName("id")
    private String id;
    
    @SerializedName("symbol")
    private String symbol;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("current_price")
    private double currentPrice;
    
    @SerializedName("image")
    private String image;
    
    @SerializedName("price_change_percentage_24h")
    private double priceChangePercentage24h;
    
    // Constructor for manual creation (backward compatibility)
    public CoinModel(String id, String symbol, String name, String price, String image) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = Double.parseDouble(price.replace("$", "").replace(",", ""));
        this.image = image;
    }

    // Overloaded constructor used in unit tests
    public CoinModel(String id, String symbol, String name, double currentPrice, String image, double priceChangePercentage24h) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = currentPrice;
        this.image = image;
        this.priceChangePercentage24h = priceChangePercentage24h;
    }
    
    // Getters
    public String getId() { 
        return id; 
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public String getFullName() {
        return name;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPrice() {
        return String.format("$%.2f", currentPrice);
    }
    
    public double getCurrentPrice() {
        return currentPrice;
    }
    
    public String getImage() { 
        return image; 
    }
    
    public double getPriceChangePercentage24h() {
        return priceChangePercentage24h;
    }
    
    public String getFormattedPriceChange() {
        return String.format("%s%.2f%%", priceChangePercentage24h >= 0 ? "+" : "", Math.abs(priceChangePercentage24h));
    }
}
