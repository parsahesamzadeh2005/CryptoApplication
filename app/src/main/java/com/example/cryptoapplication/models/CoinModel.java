package com.example.cryptoapplication.models;

import com.google.gson.annotations.SerializedName;

/**
 * Consolidated model class for cryptocurrency data
 * Maps to CoinGecko API response format and supports database operations
 */
public class CoinModel {
    @SerializedName("id")
    private String id;
    
    @SerializedName("symbol")
    private String symbol;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("image")
    private String image;
    
    @SerializedName("current_price")
    private double currentPrice;
    
    @SerializedName("market_cap")
    private double marketCap;
    
    @SerializedName("market_cap_rank")
    private int marketCapRank;
    
    @SerializedName("fully_diluted_valuation")
    private double fullyDilutedValuation;
    
    @SerializedName("total_volume")
    private double totalVolume;
    
    @SerializedName("high_24h")
    private double high24h;
    
    @SerializedName("low_24h")
    private double low24h;
    
    @SerializedName("price_change_24h")
    private double priceChange24h;
    
    @SerializedName("price_change_percentage_24h")
    private double priceChangePercentage24h;
    
    @SerializedName("market_cap_change_24h")
    private double marketCapChange24h;
    
    @SerializedName("market_cap_change_percentage_24h")
    private double marketCapChangePercentage24h;
    
    @SerializedName("circulating_supply")
    private double circulatingSupply;
    
    @SerializedName("total_supply")
    private double totalSupply;
    
    @SerializedName("max_supply")
    private double maxSupply;
    
    @SerializedName("last_updated")
    private String lastUpdated;
    
    // Database-specific field (not from API)
    private long cachedAt;

    // Default constructor
    public CoinModel() {}
    
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
    
    public void setId(String id) { 
        this.id = id; 
    }

    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) { 
        this.symbol = symbol; 
    }
    
    public String getFullName() {
        return name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) { 
        this.name = name; 
    }
    
    public String getPrice() {
        return String.format("$%.2f", currentPrice);
    }
    
    public double getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(double currentPrice) { 
        this.currentPrice = currentPrice; 
    }
    
    public String getImage() { 
        return image; 
    }
    
    public void setImage(String image) { 
        this.image = image; 
    }
    
    public double getPriceChangePercentage24h() {
        return priceChangePercentage24h;
    }
    
    public void setPriceChangePercentage24h(double priceChangePercentage24h) { 
        this.priceChangePercentage24h = priceChangePercentage24h; 
    }
    
    public String getFormattedPriceChange() {
        return String.format("%s%.2f%%", priceChangePercentage24h >= 0 ? "+" : "", Math.abs(priceChangePercentage24h));
    }

    public double getMarketCap() { return marketCap; }
    public void setMarketCap(double marketCap) { this.marketCap = marketCap; }

    public int getMarketCapRank() { return marketCapRank; }
    public void setMarketCapRank(int marketCapRank) { this.marketCapRank = marketCapRank; }

    public double getFullyDilutedValuation() { return fullyDilutedValuation; }
    public void setFullyDilutedValuation(double fullyDilutedValuation) { this.fullyDilutedValuation = fullyDilutedValuation; }

    public double getTotalVolume() { return totalVolume; }
    public void setTotalVolume(double totalVolume) { this.totalVolume = totalVolume; }

    public double getHigh24h() { return high24h; }
    public void setHigh24h(double high24h) { this.high24h = high24h; }

    public double getLow24h() { return low24h; }
    public void setLow24h(double low24h) { this.low24h = low24h; }

    public double getPriceChange24h() { return priceChange24h; }
    public void setPriceChange24h(double priceChange24h) { this.priceChange24h = priceChange24h; }

    public double getMarketCapChange24h() { return marketCapChange24h; }
    public void setMarketCapChange24h(double marketCapChange24h) { this.marketCapChange24h = marketCapChange24h; }

    public double getMarketCapChangePercentage24h() { return marketCapChangePercentage24h; }
    public void setMarketCapChangePercentage24h(double marketCapChangePercentage24h) { this.marketCapChangePercentage24h = marketCapChangePercentage24h; }

    public double getCirculatingSupply() { return circulatingSupply; }
    public void setCirculatingSupply(double circulatingSupply) { this.circulatingSupply = circulatingSupply; }

    public double getTotalSupply() { return totalSupply; }
    public void setTotalSupply(double totalSupply) { this.totalSupply = totalSupply; }

    public double getMaxSupply() { return maxSupply; }
    public void setMaxSupply(double maxSupply) { this.maxSupply = maxSupply; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }

    public long getCachedAt() { return cachedAt; }
    public void setCachedAt(long cachedAt) { this.cachedAt = cachedAt; }
}