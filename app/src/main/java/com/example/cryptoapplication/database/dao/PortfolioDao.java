package com.example.cryptoapplication.database.dao;

import com.example.cryptoapplication.models.PortfolioItem;

import java.util.List;

/**
 * DAO interface for Portfolio-related database operations.
 * Extends BaseDao for common CRUD operations and adds portfolio-specific methods.
 */
public interface PortfolioDao extends BaseDao<PortfolioItem> {
    
    /**
     * Get all portfolio items for a specific user
     * @param userId The ID of the user
     * @return List of portfolio items
     */
    List<PortfolioItem> getByUserId(long userId);
    
    /**
     * Get portfolio items for a specific coin
     * @param coinId The ID of the coin
     * @return List of portfolio items
     */
    List<PortfolioItem> getByCoinId(String coinId);
    
    /**
     * Get portfolio items for a specific user and coin
     * @param userId The ID of the user
     * @param coinId The ID of the coin
     * @return List of portfolio items
     */
    List<PortfolioItem> getByUserAndCoin(long userId, String coinId);
    
    /**
     * Get the total quantity of a coin held by a user
     * @param userId The ID of the user
     * @param coinId The ID of the coin
     * @return Total quantity held
     */
    double getTotalQuantity(long userId, String coinId);
    
    /**
     * Get the total investment amount for a user
     * @param userId The ID of the user
     * @return Total investment amount
     */
    double getTotalInvestment(long userId);
    
    /**
     * Get the total current value of a user's portfolio
     * @param userId The ID of the user
     * @return Total current value
     */
    double getTotalValue(long userId);
    
    /**
     * Add a transaction to the portfolio (buy/sell)
     * @param userId The ID of the user
     * @param coinId The ID of the coin
     * @param quantity The quantity (positive for buy, negative for sell)
     * @param pricePerCoin The price per coin
     * @param transactionType The type of transaction (BUY/SELL)
     * @return true if successful, false otherwise
     */
    boolean addTransaction(long userId, String coinId, double quantity, 
                        double pricePerCoin, String transactionType);
    
    /**
     * Remove all portfolio items for a specific user and coin
     * @param userId The ID of the user
     * @param coinId The ID of the coin
     * @return Number of items removed
     */
    int removeAllByUserAndCoin(long userId, String coinId);
    
    /**
     * Get portfolio performance summary for a user
     * @param userId The ID of the user
     * @return Portfolio summary data
     */
    PortfolioSummary getPortfolioSummary(long userId);
    
    /**
     * Get distinct coins in a user's portfolio
     * @param userId The ID of the user
     * @return List of distinct coin IDs
     */
    List<String> getDistinctCoins(long userId);
    
    /**
     * Check if a user has any holdings of a specific coin
     * @param userId The ID of the user
     * @param coinId The ID of the coin
     * @return true if user has holdings, false otherwise
     */
    boolean hasHoldings(long userId, String coinId);
    
    /**
     * Update the current price for all portfolio items of a specific coin
     * @param coinId The ID of the coin
     * @param currentPrice The current price
     * @return Number of items updated
     */
    int updateCurrentPrice(String coinId, double currentPrice);
    
    /**
     * Get portfolio items with profit/loss calculation
     * @param userId The ID of the user
     * @return List of portfolio items with calculated profit/loss
     */
    List<PortfolioItemWithProfit> getPortfolioWithProfit(long userId);
    
    /**
     * Portfolio summary data class
     */
    class PortfolioSummary {
        public double totalInvestment;
        public double totalValue;
        public double totalProfit;
        public double totalProfitPercentage;
        public int numberOfCoins;
        public int numberOfTransactions;
        
        public PortfolioSummary(double totalInvestment, double totalValue, double totalProfit, 
                              double totalProfitPercentage, int numberOfCoins, int numberOfTransactions) {
            this.totalInvestment = totalInvestment;
            this.totalValue = totalValue;
            this.totalProfit = totalProfit;
            this.totalProfitPercentage = totalProfitPercentage;
            this.numberOfCoins = numberOfCoins;
            this.numberOfTransactions = numberOfTransactions;
        }

        // Convenience getters for usage examples
        public double getTotalInvestment() { return totalInvestment; }
        public double getCurrentValue() { return totalValue; }
        public double getTotalProfitLoss() { return totalProfit; }
        public double getTotalProfitLossPercentage() { return totalProfitPercentage; }
    }
    
    /**
     * Portfolio item with profit calculation
     */
    class PortfolioItemWithProfit {
        public PortfolioItem item;
        public double currentValue;
        public double profit;
        public double profitPercentage;
        
        public PortfolioItemWithProfit(PortfolioItem item, double currentValue, 
                                     double profit, double profitPercentage) {
            this.item = item;
            this.currentValue = currentValue;
            this.profit = profit;
            this.profitPercentage = profitPercentage;
        }
    }
}