package com.example.cryptoapplication.database.dao;

import com.example.cryptoapplication.models.CoinModel;

import java.util.List;

/**
 * DAO interface for Coin-related database operations.
 * Extends BaseDao for common CRUD operations and adds coin-specific methods.
 */
public interface CoinDao extends BaseDao<CoinModel> {
    
    /**
     * Find a coin by its coin ID
     * @param coinId The coin ID to search for
     * @return The coin if found, null otherwise
     */
    CoinModel findByCoinId(String coinId);
    
    /**
     * Find coins by symbol
     * @param symbol The symbol to search for
     * @return List of coins with the given symbol
     */
    List<CoinModel> findBySymbol(String symbol);
    
    /**
     * Search coins by name (partial match)
     * @param query The search query
     * @return List of coins matching the search query
     */
    List<CoinModel> searchByName(String query);
    
    /**
     * Get top coins by market cap
     * @param limit The maximum number of coins to return
     * @return List of top coins
     */
    List<CoinModel> getTopByMarketCap(int limit);
    
    /**
     * Get coins with positive price change (gainers)
     * @param limit The maximum number of coins to return
     * @return List of top gainers
     */
    List<CoinModel> getTopGainers(int limit);
    
    /**
     * Get coins with negative price change (losers)
     * @param limit The maximum number of coins to return
     * @return List of top losers
     */
    List<CoinModel> getTopLosers(int limit);
    
    /**
     * Get favorite coins for a user
     * @param userId The ID of the user
     * @return List of favorite coins
     */
    List<CoinModel> getFavoritesByUser(long userId);
    
    /**
     * Add a coin to user's favorites
     * @param userId The ID of the user
     * @param coinId The ID of the coin
     * @return true if successful, false otherwise
     */
    boolean addToFavorites(long userId, String coinId);
    
    /**
     * Remove a coin from user's favorites
     * @param userId The ID of the user
     * @param coinId The ID of the coin
     * @return true if successful, false otherwise
     */
    boolean removeFromFavorites(long userId, String coinId);
    
    /**
     * Check if a coin is in user's favorites
     * @param userId The ID of the user
     * @param coinId The ID of the coin
     * @return true if favorite, false otherwise
     */
    boolean isFavorite(long userId, String coinId);
    
    /**
     * Update coin price and market data
     * @param coinId The ID of the coin
     * @param currentPrice The current price
     * @param priceChange24h The 24h price change
     * @param priceChangePercentage24h The 24h price change percentage
     * @param marketCap The market cap
     * @param lastUpdated The last updated timestamp
     * @return The number of rows affected
     */
    int updatePriceData(String coinId, double currentPrice, double priceChange24h, 
                       double priceChangePercentage24h, long marketCap, long lastUpdated);
    
    /**
     * Get coins that need price update (older than specified timestamp)
     * @param maxAgeMillis Maximum age in milliseconds
     * @return List of coins that need updating
     */
    List<CoinModel> getCoinsNeedingUpdate(long maxAgeMillis);
    
    /**
     * Delete old cached coins (older than specified timestamp)
     * @param maxAgeMillis Maximum age in milliseconds
     * @return Number of coins deleted
     */
    int deleteOldCoins(long maxAgeMillis);
}