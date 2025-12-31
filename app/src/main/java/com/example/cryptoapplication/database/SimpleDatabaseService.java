package com.example.cryptoapplication.database;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.cryptoapplication.models.CoinModel;
import com.example.cryptoapplication.models.ConsolidatedAsset;
import com.example.cryptoapplication.models.PortfolioItem;
import com.example.cryptoapplication.models.User;
import com.example.cryptoapplication.utils.PasswordUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ULTRA-SIMPLE DATABASE SERVICE
 * Uses SharedPreferences for trading data to avoid all database lock issues
 */
public class SimpleDatabaseService {
    
    private static SimpleDatabaseService instance;
    private final CryptoDatabaseManager dbManager;
    private final SharedPreferences userPrefs;
    private final SharedPreferences tradingPrefs;
    private User currentUser;
    private String lastErrorMessage = "";
    
    private static final String USER_PREFS = "user_data";
    private static final String TRADING_PREFS = "trading_data";
    private static final String USER_ID_KEY = "current_user_id";
    private static final String BALANCE_KEY = "user_balance_";
    private static final String HOLDINGS_KEY = "holdings_";
    
    private SimpleDatabaseService(Context context) {
        this.dbManager = CryptoDatabaseManager.getInstance(context);
        this.userPrefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        this.tradingPrefs = context.getSharedPreferences(TRADING_PREFS, Context.MODE_PRIVATE);
        loadCurrentUser();
    }
    
    public static SimpleDatabaseService getInstance(Context context) {
        if (instance == null) {
            instance = new SimpleDatabaseService(context);
        }
        return instance;
    }
    
    // USER MANAGEMENT
    public boolean isUserLoggedIn() {
        return currentUser != null;
    }
    
    public User getCurrentUser() {
        if (currentUser != null) {
            // Get fresh balance from SharedPreferences
            double balance = tradingPrefs.getFloat(BALANCE_KEY + currentUser.getId(), 0.0f);
            currentUser.setBalance(balance);
        }
        return currentUser;
    }
    
    public User loginUser(String email, String password) {
        try {
            User user = dbManager.getUserDao().findByEmail(email);
            if (user != null && PasswordUtils.verifyPassword(password, user.getPassword())) {
                setCurrentUser(user);
                // Initialize balance in SharedPreferences if not exists
                if (!tradingPrefs.contains(BALANCE_KEY + user.getId())) {
                    tradingPrefs.edit().putFloat(BALANCE_KEY + user.getId(), (float)user.getBalance()).apply();
                }
                return user;
            }
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return null;
    }
    
    public User registerNewUser(String username, String email, String password) {
        try {
            if (dbManager.getUserDao().existsByEmail(email)) {
                return null;
            }
            
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(PasswordUtils.hashPassword(password));
            user.setCreatedAt(System.currentTimeMillis());
            user.setLastLogin(System.currentTimeMillis());
            user.setBalance(0.0);
            
            long id = dbManager.getUserDao().insert(user);
            if (id != -1) {
                user.setId(id);
                setCurrentUser(user);
                // Initialize balance in SharedPreferences
                tradingPrefs.edit().putFloat(BALANCE_KEY + id, 0.0f).apply();
                return user;
            }
        } catch (Exception e) {
            System.out.println("Register error: " + e.getMessage());
        }
        return null;
    }
    
    public void logoutCurrentUser() {
        setCurrentUser(null);
    }
    
    // SIMPLE BALANCE MANAGEMENT
    public boolean addMoneyToBalance(double amount) {
        if (!isUserLoggedIn() || amount <= 0) {
            return false;
        }
        
        String balanceKey = BALANCE_KEY + currentUser.getId();
        float currentBalance = tradingPrefs.getFloat(balanceKey, 0.0f);
        float newBalance = currentBalance + (float)amount;
        
        boolean success = tradingPrefs.edit().putFloat(balanceKey, newBalance).commit();
        if (success) {
            currentUser.setBalance(newBalance);
            System.out.println("Added $" + amount + " to balance. New balance: $" + newBalance);
        }
        return success;
    }
    
    public boolean takeMoneyFromBalance(double amount) {
        if (!isUserLoggedIn() || amount <= 0) {
            return false;
        }
        
        String balanceKey = BALANCE_KEY + currentUser.getId();
        float currentBalance = tradingPrefs.getFloat(balanceKey, 0.0f);
        
        if (currentBalance < amount) {
            return false;
        }
        
        float newBalance = currentBalance - (float)amount;
        boolean success = tradingPrefs.edit().putFloat(balanceKey, newBalance).commit();
        if (success) {
            currentUser.setBalance(newBalance);
            System.out.println("Took $" + amount + " from balance. New balance: $" + newBalance);
        }
        return success;
    }
    
    // ULTRA-SIMPLE CRYPTO TRADING
    public boolean buyCryptocurrency(String coinId, double quantity, double price) {
        lastErrorMessage = "";
        System.out.println("=== SIMPLE BUY START ===");
        System.out.println("Coin: " + coinId + ", Quantity: " + quantity + ", Price: $" + price);
        
        if (!isUserLoggedIn()) {
            lastErrorMessage = "Not logged in";
            return false;
        }
        
        if (quantity <= 0 || price <= 0) {
            lastErrorMessage = "Invalid values";
            return false;
        }
        
        double totalCost = quantity * price;
        String balanceKey = BALANCE_KEY + currentUser.getId();
        float currentBalance = tradingPrefs.getFloat(balanceKey, 0.0f);
        
        System.out.println("Need: $" + totalCost + ", Have: $" + currentBalance);
        
        if (currentBalance < totalCost) {
            lastErrorMessage = "Not enough money. Need $" + totalCost + ", have $" + currentBalance;
            return false;
        }
        
        try {
            // Step 1: Subtract money from balance
            float newBalance = currentBalance - (float)totalCost;
            tradingPrefs.edit().putFloat(balanceKey, newBalance).apply();
            
            // Step 2: Add to crypto holdings
            String holdingsKey = HOLDINGS_KEY + currentUser.getId() + "_" + coinId;
            float currentHoldings = tradingPrefs.getFloat(holdingsKey, 0.0f);
            float newHoldings = currentHoldings + (float)quantity;
            tradingPrefs.edit().putFloat(holdingsKey, newHoldings).apply();
            
            // Step 3: Save transaction record
            String transactionKey = "tx_" + currentUser.getId() + "_" + System.currentTimeMillis();
            String transactionData = coinId + "|" + quantity + "|" + price + "|BUY|" + System.currentTimeMillis();
            tradingPrefs.edit().putString(transactionKey, transactionData).apply();
            
            // Update current user
            currentUser.setBalance(newBalance);
            
            System.out.println("BUY SUCCESS! New balance: $" + newBalance + ", Holdings: " + newHoldings);
            return true;
            
        } catch (Exception e) {
            lastErrorMessage = "Error: " + e.getMessage();
            System.out.println("BUY ERROR: " + e.getMessage());
            return false;
        }
    }
    
    public boolean sellCryptocurrency(String coinId, double quantity, double price) {
        lastErrorMessage = "";
        System.out.println("=== SIMPLE SELL START ===");
        System.out.println("Coin: " + coinId + ", Quantity: " + quantity + ", Price: $" + price);
        
        if (!isUserLoggedIn()) {
            lastErrorMessage = "Not logged in";
            return false;
        }
        
        if (quantity <= 0 || price <= 0) {
            lastErrorMessage = "Invalid values";
            return false;
        }
        
        // Check holdings
        String holdingsKey = HOLDINGS_KEY + currentUser.getId() + "_" + coinId;
        float currentHoldings = tradingPrefs.getFloat(holdingsKey, 0.0f);
        
        System.out.println("Want to sell: " + quantity + ", Have: " + currentHoldings);
        
        if (currentHoldings < quantity) {
            lastErrorMessage = "Not enough crypto. Need " + quantity + ", have " + currentHoldings;
            return false;
        }
        
        try {
            double totalEarned = quantity * price;
            
            // Step 1: Add money to balance
            String balanceKey = BALANCE_KEY + currentUser.getId();
            float currentBalance = tradingPrefs.getFloat(balanceKey, 0.0f);
            float newBalance = currentBalance + (float)totalEarned;
            tradingPrefs.edit().putFloat(balanceKey, newBalance).apply();
            
            // Step 2: Subtract from crypto holdings
            float newHoldings = currentHoldings - (float)quantity;
            tradingPrefs.edit().putFloat(holdingsKey, newHoldings).apply();
            
            // Step 3: Save transaction record
            String transactionKey = "tx_" + currentUser.getId() + "_" + System.currentTimeMillis();
            String transactionData = coinId + "|" + quantity + "|" + price + "|SELL|" + System.currentTimeMillis();
            tradingPrefs.edit().putString(transactionKey, transactionData).apply();
            
            // Update current user
            currentUser.setBalance(newBalance);
            
            System.out.println("SELL SUCCESS! New balance: $" + newBalance + ", Holdings: " + newHoldings);
            return true;
            
        } catch (Exception e) {
            lastErrorMessage = "Error: " + e.getMessage();
            System.out.println("SELL ERROR: " + e.getMessage());
            return false;
        }
    }
    
    // SIMPLE HOLDINGS CALCULATOR
    public double calculateUserHoldings(String coinId) {
        if (!isUserLoggedIn()) {
            return 0.0;
        }
        
        String holdingsKey = HOLDINGS_KEY + currentUser.getId() + "_" + coinId;
        return tradingPrefs.getFloat(holdingsKey, 0.0f);
    }
    
    // CONSOLIDATED ASSETS VIEW
    public List<ConsolidatedAsset> getConsolidatedAssets() {
        List<ConsolidatedAsset> consolidatedAssets = new ArrayList<>();
        
        if (!isUserLoggedIn()) {
            return consolidatedAssets;
        }
        
        try {
            // Get all transactions
            List<PortfolioItem> allTransactions = getUserPortfolio();
            Map<String, ConsolidatedAsset> assetMap = new HashMap<>();
            
            // Group transactions by coin
            for (PortfolioItem transaction : allTransactions) {
                String coinId = transaction.getCoinId();
                
                ConsolidatedAsset asset = assetMap.get(coinId);
                if (asset == null) {
                    // Get coin info
                    CoinModel coin = findCoinById(coinId);
                    String coinName = coin != null ? coin.getName() : coinId;
                    String coinSymbol = coin != null ? coin.getSymbol() : coinId.toUpperCase();
                    
                    asset = new ConsolidatedAsset(coinId, coinName, coinSymbol);
                    assetMap.put(coinId, asset);
                }
                
                // Update consolidated data based on transaction type
                if ("BUY".equals(transaction.getTransactionType())) {
                    // Add to holdings
                    asset.setTotalQuantity(asset.getTotalQuantity() + transaction.getQuantity());
                } else if ("SELL".equals(transaction.getTransactionType())) {
                    // Subtract from holdings
                    asset.setTotalQuantity(asset.getTotalQuantity() - transaction.getQuantity());
                }
                
                asset.setTransactionCount(asset.getTransactionCount() + 1);
                asset.setCurrentPrice(transaction.getPurchasePrice()); // Use latest price as current
            }
            
            // Calculate current values for each asset
            for (ConsolidatedAsset asset : assetMap.values()) {
                if (asset.getTotalQuantity() > 0) { // Only include assets with positive holdings
                    double currentValue = asset.getTotalQuantity() * asset.getCurrentPrice();
                    asset.setTotalValue(currentValue);
                    consolidatedAssets.add(asset);
                }
            }
            
            // Sort by total value (highest first)
            consolidatedAssets.sort((a, b) -> Double.compare(b.getTotalValue(), a.getTotalValue()));
            
        } catch (Exception e) {
            System.out.println("Consolidated assets error: " + e.getMessage());
        }
        
        return consolidatedAssets;
    }
    public List<PortfolioItem> getUserPortfolio() {
        List<PortfolioItem> portfolio = new ArrayList<>();
        
        if (!isUserLoggedIn()) {
            return portfolio;
        }
        
        try {
            Map<String, ?> allPrefs = tradingPrefs.getAll();
            String userPrefix = "tx_" + currentUser.getId() + "_";
            
            for (Map.Entry<String, ?> entry : allPrefs.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith(userPrefix)) {
                    String data = (String) entry.getValue();
                    String[] parts = data.split("\\|");
                    if (parts.length >= 5) {
                        PortfolioItem item = new PortfolioItem();
                        item.setUserId(currentUser.getId());
                        item.setCoinId(parts[0]);
                        item.setQuantity(Double.parseDouble(parts[1]));
                        item.setPurchasePrice(Double.parseDouble(parts[2]));
                        item.setTransactionType(parts[3]);
                        item.setPurchaseDate(Long.parseLong(parts[4]));
                        item.setCurrentPrice(Double.parseDouble(parts[2]));
                        item.setTotalValue(item.getQuantity() * item.getPurchasePrice());
                        portfolio.add(item);
                    }
                }
            }
            
            // Sort by date (newest first)
            portfolio.sort((a, b) -> Long.compare(b.getPurchaseDate(), a.getPurchaseDate()));
            
        } catch (Exception e) {
            System.out.println("Portfolio error: " + e.getMessage());
        }
        
        return portfolio;
    }
    
    // COIN DATA METHODS (delegate to database)
    public List<CoinModel> getAllCoins() {
        try {
            return dbManager.getCoinDao().getAll();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    public List<CoinModel> searchForCoins(String searchText) {
        try {
            return dbManager.getCoinDao().searchByName(searchText);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    public CoinModel findCoinById(String coinId) {
        try {
            return dbManager.getCoinDao().findByCoinId(coinId);
        } catch (Exception e) {
            return null;
        }
    }
    
    public List<CoinModel> getTopValueCoins(int count) {
        try {
            return dbManager.getCoinDao().getTopByMarketCap(count);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    public List<CoinModel> getWinningCoins(int count) {
        try {
            return dbManager.getCoinDao().getTopGainers(count);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    public List<CoinModel> getLosingCoins(int count) {
        try {
            return dbManager.getCoinDao().getTopLosers(count);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    public void saveCoinsToCache(List<CoinModel> coins) {
        try {
            dbManager.beginTransaction();
            for (CoinModel coin : coins) {
                CoinModel existing = dbManager.getCoinDao().findByCoinId(coin.getId());
                if (existing != null) {
                    dbManager.getCoinDao().update(coin);
                } else {
                    dbManager.getCoinDao().insert(coin);
                }
            }
            dbManager.setTransactionSuccessful();
        } catch (Exception e) {
            System.out.println("Cache error: " + e.getMessage());
        } finally {
            try {
                dbManager.endTransaction();
            } catch (Exception ignored) {}
        }
    }
    
    // FAVORITES (delegate to database)
    public boolean addCoinToFavorites(String coinId) {
        if (!isUserLoggedIn()) return false;
        try {
            return dbManager.getCoinDao().addToFavorites(currentUser.getId(), coinId);
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean removeCoinFromFavorites(String coinId) {
        if (!isUserLoggedIn()) return false;
        try {
            return dbManager.getCoinDao().removeFromFavorites(currentUser.getId(), coinId);
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isCoinInFavorites(String coinId) {
        if (!isUserLoggedIn()) return false;
        try {
            return dbManager.getCoinDao().isFavorite(currentUser.getId(), coinId);
        } catch (Exception e) {
            return false;
        }
    }
    
    public List<CoinModel> getFavoriteCoins() {
        if (!isUserLoggedIn()) return new ArrayList<>();
        try {
            return dbManager.getCoinDao().getFavoritesByUser(currentUser.getId());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    // ERROR HANDLING
    public String getLastErrorMessage() {
        return lastErrorMessage;
    }
    
    // PRIVATE HELPERS
    private void loadCurrentUser() {
        long userId = userPrefs.getLong(USER_ID_KEY, -1);
        if (userId != -1) {
            try {
                User user = dbManager.getUserDao().getById(userId);
                if (user != null) {
                    currentUser = user;
                } else {
                    userPrefs.edit().remove(USER_ID_KEY).apply();
                }
            } catch (Exception e) {
                System.out.println("Load user error: " + e.getMessage());
            }
        }
    }
    
    private void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            userPrefs.edit().putLong(USER_ID_KEY, user.getId()).apply();
        } else {
            userPrefs.edit().remove(USER_ID_KEY).apply();
        }
    }
    
    public void closeDatabase() {
        try {
            dbManager.close();
        } catch (Exception ignored) {}
    }
}