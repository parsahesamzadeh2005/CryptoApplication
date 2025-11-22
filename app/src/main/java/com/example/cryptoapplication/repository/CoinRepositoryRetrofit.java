package com.example.cryptoapplication.repository;

import com.example.cryptoapplication.model.home.CoinModel;
import com.example.cryptoapplication.network.CoinGeckoApi;
import com.example.cryptoapplication.network.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Enhanced repository using Retrofit for API calls
 * Provides better error handling and type safety
 */
public class CoinRepositoryRetrofit {
    
    private final CoinGeckoApi coinGeckoApi;
    
    public CoinRepositoryRetrofit() {
        this.coinGeckoApi = RetrofitClient.getCoinGeckoApi();
    }
    
    /**
     * Fetch coins from API with proper error handling
     * @return List of coins or empty list if error occurs
     */
    public List<CoinModel> getCoins() {
        try {
            Response<List<CoinModel>> response = coinGeckoApi.getTopCoins("usd", 30, "market_cap_desc").execute();
            
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                // Log error and return empty list
                System.err.println("API Error: " + response.code() + " - " + response.message());
                return new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Return empty list on network error
            return new ArrayList<>();
        }
    }
    
    /**
     * Get top gainers (coins with positive 24h change)
     * @param allCoins All available coins
     * @return List of top gainers
     */
    public List<CoinModel> getTopGainersTab(List<CoinModel> allCoins) {
        List<CoinModel> gainers = new ArrayList<>();
        for (CoinModel coin : allCoins) {
            if (coin.getPriceChangePercentage24h() > 0) {
                gainers.add(coin);
            }
        }
        // Sort by percentage gain (descending) and return top 10
        gainers.sort((a, b) -> Double.compare(b.getPriceChangePercentage24h(), a.getPriceChangePercentage24h()));
        return gainers.subList(0, Math.min(gainers.size(), 10));
    }
    
    /**
     * Get top losers (coins with negative 24h change)
     * @param allCoins All available coins
     * @return List of top losers
     */
    public List<CoinModel> getTopLosersTab(List<CoinModel> allCoins) {
        List<CoinModel> losers = new ArrayList<>();
        for (CoinModel coin : allCoins) {
            if (coin.getPriceChangePercentage24h() < 0) {
                losers.add(coin);
            }
        }
        // Sort by percentage loss (ascending) and return top 10
        losers.sort((a, b) -> Double.compare(a.getPriceChangePercentage24h(), b.getPriceChangePercentage24h()));
        return losers.subList(0, Math.min(losers.size(), 10));
    }
    
    /**
     * Get all coins (limited to top 10 for performance)
     * @param allCoins All available coins
     * @return List of top coins
     */
    public List<CoinModel> getAllCoinsTab(List<CoinModel> allCoins) {
        return allCoins.subList(0, Math.min(allCoins.size(), 10));
    }
}