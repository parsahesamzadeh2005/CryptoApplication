package com.example.cryptoapplication.models;

public class CoinModel {
    private String id;
    private String symbol;
    private String name;
    private String image;
    private double currentPrice;
    private long marketCap;
    private int marketCapRank;
    private long fullyDilutedValuation;
    private long totalVolume;
    private double high24h;
    private double low24h;
    private double priceChange24h;
    private double priceChangePercentage24h;
    private long marketCapChange24h;
    private double marketCapChangePercentage24h;
    private double circulatingSupply;
    private double totalSupply;
    private double maxSupply;
    private long lastUpdated;
    private long cachedAt;

    public CoinModel() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }

    public long getMarketCap() { return marketCap; }
    public void setMarketCap(long marketCap) { this.marketCap = marketCap; }

    public int getMarketCapRank() { return marketCapRank; }
    public void setMarketCapRank(int marketCapRank) { this.marketCapRank = marketCapRank; }

    public long getFullyDilutedValuation() { return fullyDilutedValuation; }
    public void setFullyDilutedValuation(long fullyDilutedValuation) { this.fullyDilutedValuation = fullyDilutedValuation; }

    public long getTotalVolume() { return totalVolume; }
    public void setTotalVolume(long totalVolume) { this.totalVolume = totalVolume; }

    public double getHigh24h() { return high24h; }
    public void setHigh24h(double high24h) { this.high24h = high24h; }

    public double getLow24h() { return low24h; }
    public void setLow24h(double low24h) { this.low24h = low24h; }

    public double getPriceChange24h() { return priceChange24h; }
    public void setPriceChange24h(double priceChange24h) { this.priceChange24h = priceChange24h; }

    public double getPriceChangePercentage24h() { return priceChangePercentage24h; }
    public void setPriceChangePercentage24h(double priceChangePercentage24h) { this.priceChangePercentage24h = priceChangePercentage24h; }

    public long getMarketCapChange24h() { return marketCapChange24h; }
    public void setMarketCapChange24h(long marketCapChange24h) { this.marketCapChange24h = marketCapChange24h; }

    public double getMarketCapChangePercentage24h() { return marketCapChangePercentage24h; }
    public void setMarketCapChangePercentage24h(double marketCapChangePercentage24h) { this.marketCapChangePercentage24h = marketCapChangePercentage24h; }

    public double getCirculatingSupply() { return circulatingSupply; }
    public void setCirculatingSupply(double circulatingSupply) { this.circulatingSupply = circulatingSupply; }

    public double getTotalSupply() { return totalSupply; }
    public void setTotalSupply(double totalSupply) { this.totalSupply = totalSupply; }

    public double getMaxSupply() { return maxSupply; }
    public void setMaxSupply(double maxSupply) { this.maxSupply = maxSupply; }

    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }

    public long getCachedAt() { return cachedAt; }
    public void setCachedAt(long cachedAt) { this.cachedAt = cachedAt; }
}