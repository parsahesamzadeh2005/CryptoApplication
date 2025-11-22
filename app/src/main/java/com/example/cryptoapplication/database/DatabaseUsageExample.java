package com.example.cryptoapplication.database;

import android.content.Context;
import android.util.Log;

import com.example.cryptoapplication.database.dao.PortfolioDao;
import com.example.cryptoapplication.models.CoinModel;
import com.example.cryptoapplication.models.PortfolioItem;
import com.example.cryptoapplication.models.User;
import com.example.cryptoapplication.service.AuthService;
import com.example.cryptoapplication.service.AuthResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Example usage of the SQLite database integration
 * This class demonstrates how to use the database services in your application
 */
public class DatabaseUsageExample {
    
    private static final String TAG = "DatabaseUsageExample";
    
    private final DatabaseService databaseService;
    private final AuthService authService;
    
    public DatabaseUsageExample(Context context) {
        this.databaseService = DatabaseService.getInstance(context);
        this.authService = new AuthService(context);
    }
    
    /**
     * Example: User authentication and management
     */
    public void demonstrateUserManagement() {
        Log.i(TAG, "=== User Management Demo ===");
        
        // Register a new user
        AuthResult regResult = authService.register("john_doe", "john@example.com", "password123");
        boolean registrationSuccess = regResult.getStatus() == AuthResult.AuthStatus.SUCCESS;
        Log.i(TAG, "Registration successful: " + registrationSuccess + (registrationSuccess ? "" : " (" + regResult.getMessage() + ")"));
        
        // Login user
        AuthResult loginResult = authService.login("john@example.com", "password123");
        boolean loginSuccess = loginResult.getStatus() == AuthResult.AuthStatus.SUCCESS;
        Log.i(TAG, "Login successful: " + loginSuccess + (loginSuccess ? "" : " (" + loginResult.getMessage() + ")"));
        
        // Get current user
        if (loginSuccess) {
            User currentUser = authService.getCurrentUser();
            Log.i(TAG, "Current user: " + (currentUser != null ? currentUser.getUsername() : "null"));
        }
        
        // Update user profile
        User user = authService.getCurrentUser();
        if (user != null) {
            // Set any fields that exist on the User model
            user.setProfileImage("profile_image_url");
            boolean updated = databaseService.updateUserProfile(user);
            Log.i(TAG, "User profile updated: " + updated);
        }
        
        // Logout
        authService.logout();
        Log.i(TAG, "User logged out");
    }
    
    /**
     * Example: Coin data management
     */
    public void demonstrateCoinManagement() {
        Log.i(TAG, "=== Coin Management Demo ===");
        
        // Add sample coins to cache (align with CoinModel no-arg constructor)
        CoinModel bitcoin = new CoinModel();
        bitcoin.setId("bitcoin");
        bitcoin.setName("Bitcoin");
        bitcoin.setSymbol("BTC");
        bitcoin.setCurrentPrice(50000.0);
        bitcoin.setPriceChangePercentage24h(5.5);
        bitcoin.setImage("https://example.com/bitcoin.png");

        CoinModel ethereum = new CoinModel();
        ethereum.setId("ethereum");
        ethereum.setName("Ethereum");
        ethereum.setSymbol("ETH");
        ethereum.setCurrentPrice(3000.0);
        ethereum.setPriceChangePercentage24h(-2.3);
        ethereum.setImage("https://example.com/ethereum.png");

        databaseService.cacheCoins(Arrays.asList(bitcoin, ethereum));
        Log.i(TAG, "Coins cached");
        
        // Search for coins
        List<CoinModel> searchResults = databaseService.searchCoins("bit");
        Log.i(TAG, "Search results for 'bit': " + searchResults.size() + " coins found");
        
        // Get all cached coins
        List<CoinModel> allCoins = databaseService.getAllCoins();
        Log.i(TAG, "Total cached coins: " + allCoins.size());
        
        // Update coin price
        bitcoin.setCurrentPrice(51000.0);
        bitcoin.setPriceChangePercentage24h(7.2);
        databaseService.cacheCoins(Collections.singletonList(bitcoin));
        Log.i(TAG, "Bitcoin price updated via cache");
        
        // Add to favorites (assuming user is logged in)
        User currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            boolean favAdded = databaseService.addToFavorites(bitcoin.getId());
            Log.i(TAG, "Bitcoin added to favorites: " + favAdded);

            // Get user favorites
            List<CoinModel> favorites = databaseService.getFavoriteCoins();
            Log.i(TAG, "User favorites: " + (favorites != null ? favorites.size() : 0) + " coins");
        }
    }
    
    /**
     * Example: Portfolio management
     */
    public void demonstratePortfolioManagement() {
        Log.i(TAG, "=== Portfolio Management Demo ===");
        
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "No user logged in, cannot demonstrate portfolio");
            return;
        }
        
        // Add portfolio entry
        boolean added = databaseService.addPortfolioTransaction(
            "bitcoin",
            0.5, // 0.5 BTC
            48000.0, // Purchase price per coin
            "BUY"
        );
        Log.i(TAG, "Portfolio entry added: " + added);

        // Get portfolio items
        List<PortfolioItem> portfolioItems = databaseService.getPortfolio();
        Log.i(TAG, "Portfolio items: " + portfolioItems.size());

        // Get portfolio summary
        PortfolioDao.PortfolioSummary summary = databaseService.getPortfolioSummary();
        if (summary != null) {
            Log.i(TAG, "Portfolio Summary:");
            Log.i(TAG, "- Total Investment: $" + summary.getTotalInvestment());
            Log.i(TAG, "- Current Value: $" + summary.getCurrentValue());
            Log.i(TAG, "- Total Profit/Loss: $" + summary.getTotalProfitLoss());
            Log.i(TAG, "- Total Profit/Loss %: " + summary.getTotalProfitLossPercentage() + "%");
        }
        
        // In this simplified service, prices update when coins are cached/updated.
    }
    
    /**
     * Example: Search history management
     */
    public void demonstrateSearchHistory() {
        Log.i(TAG, "=== Search History Demo ===");
        
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "No user logged in, cannot demonstrate search history");
            return;
        }
        
        // Add search queries
        databaseService.addSearchQuery("bitcoin");
        databaseService.addSearchQuery("ethereum");
        databaseService.addSearchQuery("cardano");
        Log.i(TAG, "Search queries added");
        
        // Get search history
        List<com.example.cryptoapplication.models.SearchHistoryItem> searchHistory = databaseService.getRecentSearches(50);
        Log.i(TAG, "Search history size: " + (searchHistory != null ? searchHistory.size() : 0));
        
        // Clear search history
        databaseService.clearSearchHistory();
        Log.i(TAG, "Search history cleared");
    }
    
    /**
     * Example: Data synchronization
     */
    public void demonstrateDataSynchronization() {
        Log.i(TAG, "=== Data Synchronization Demo ===");
        
        // Example sync decision based on last sync time
        long lastSync = databaseService.getLastSyncTime();
        boolean needsSync = databaseService.needsSync(24L * 60 * 60 * 1000); // 24 hours
        Log.i(TAG, "Last sync: " + lastSync + ", needsSync: " + needsSync);
    }
    
    /**
     * Example: Database maintenance
     */
    public void demonstrateDatabaseMaintenance() {
        Log.i(TAG, "=== Database Maintenance Demo ===");
        
        // Get database statistics
        CryptoDatabaseManager.DatabaseStatistics stats = databaseService.getStatistics();
        Log.i(TAG, "Database Statistics:");
        Log.i(TAG, "- Total Users: " + stats.getTotalUsers());
        Log.i(TAG, "- Total Coins: " + stats.getTotalCoins());
        Log.i(TAG, "- Total Portfolio Items: " + stats.getTotalPortfolioItems());
        Log.i(TAG, "- Total Search History: " + stats.getTotalSearchHistory());
        Log.i(TAG, "- Database Size: " + stats.getDatabaseSize() + " bytes");
        
        // Clear old data (older than 30 days)
        databaseService.cleanupOldData(30L * 24 * 60 * 60 * 1000);
        Log.i(TAG, "Old data cleanup invoked for 30 days");
        
        // Optimization (VACUUM) is not exposed in this DatabaseService.
    }
    
    /**
     * Run all demonstrations
     */
    public void runAllDemos() {
        Log.i(TAG, "=== Starting Database Usage Demonstrations ===");
        
        try {
            demonstrateUserManagement();
            demonstrateCoinManagement();
            demonstratePortfolioManagement();
            demonstrateSearchHistory();
            demonstrateDataSynchronization();
            demonstrateDatabaseMaintenance();
            
            Log.i(TAG, "=== All demonstrations completed successfully ===");
        } catch (Exception e) {
            Log.e(TAG, "Error during demonstrations", e);
        }
    }
    
    /**
     * Example: Error handling
     */
    public void demonstrateErrorHandling() {
        Log.i(TAG, "=== Error Handling Demo ===");
        
        try {
            // Try to register with invalid data
            AuthResult invalidReg = authService.register("", "invalid-email", "123");
            Log.i(TAG, "Invalid registration status: " + invalidReg.getStatus() + ", message: " + invalidReg.getMessage());
            
            // Try to login with wrong credentials
            AuthResult invalidLogin = authService.login("nonexistent@example.com", "wrongpassword");
            Log.i(TAG, "Invalid login status: " + invalidLogin.getStatus() + ", message: " + invalidLogin.getMessage());
            
            // Try to add invalid portfolio entry
            User currentUser = authService.getCurrentUser();
            if (currentUser != null) {
                boolean result = databaseService.addPortfolioTransaction(
                    "invalid-coin-id",
                    -1.0, // Negative quantity
                    0.0,  // Zero price
                    "INVALID_TYPE"
                );
                Log.i(TAG, "Invalid portfolio entry result: " + result);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error handled: " + e.getMessage());
        }
    }
}