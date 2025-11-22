package com.example.cryptoapplication.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.cryptoapplication.database.dao.CoinDao;
import com.example.cryptoapplication.database.dao.PortfolioDao;
import com.example.cryptoapplication.database.dao.SearchHistoryDao;
import com.example.cryptoapplication.database.dao.TransactionDao;
import com.example.cryptoapplication.database.dao.UserDao;
import com.example.cryptoapplication.models.CoinModel;
import com.example.cryptoapplication.models.PortfolioItem;
import com.example.cryptoapplication.models.TransactionRecord;
import com.example.cryptoapplication.models.SearchHistoryItem;
import com.example.cryptoapplication.models.User;
import com.example.cryptoapplication.utils.PasswordUtils;

import java.util.List;

/**
 * Service class that provides a high-level API for database operations.
 * This class integrates with the existing AuthService and provides a clean interface for activities.
 */
public class DatabaseService {
    
    private static final String PREFS_NAME = "CryptoDatabasePrefs";
    private static final String KEY_CURRENT_USER_ID = "current_user_id";
    private static final String KEY_LAST_SYNC_TIME = "last_sync_time";
    
    private static DatabaseService instance;
    private final CryptoDatabaseManager dbManager;
    private final SharedPreferences prefs;
    
    private User currentUser;
    
    /**
     * Private constructor for singleton pattern
     * @param context Application context
     */
    private DatabaseService(Context context) {
        this.dbManager = CryptoDatabaseManager.getInstance(context);
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadCurrentUser();
    }
    
    /**
     * Get the singleton instance of DatabaseService
     * @param context Application context
     * @return The singleton instance
     */
    public static synchronized DatabaseService getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseService(context);
        }
        return instance;
    }
    
    /**
     * Load the current user from SharedPreferences
     */
    private void loadCurrentUser() {
        long userId = prefs.getLong(KEY_CURRENT_USER_ID, -1);
        if (userId != -1) {
            currentUser = dbManager.getUserDao().getById(userId);
            if (currentUser == null) {
                // User no longer exists, clear the preference
                prefs.edit().remove(KEY_CURRENT_USER_ID).apply();
            }
        }
    }
    
    /**
     * Get the current user
     * @return Current user or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Get a user by email
     * @param email Email address
     * @return User if found, null otherwise
     */
    public User getUserByEmail(String email) {
        return dbManager.getUserDao().findByEmail(email);
    }
    
    /**
     * Set the current user
     * @param user The user to set as current
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            prefs.edit().putLong(KEY_CURRENT_USER_ID, user.getId()).apply();
        } else {
            prefs.edit().remove(KEY_CURRENT_USER_ID).apply();
        }
    }
    
    /**
     * Check if a user is currently logged in
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * User authentication (login)
     * @param email User email
     * @param password User password
     * @return User if authentication successful, null otherwise
     */
    public User login(String email, String password) {
        User user = dbManager.getUserDao().findByEmail(email);
        if (user != null && PasswordUtils.verifyPassword(password, user.getPassword())) {
            setCurrentUser(user);
            return user;
        }
        return null;
    }
    
    /**
     * User registration
     * @param username Username
     * @param email Email
     * @param password Password
     * @return User if registration successful, null otherwise
     */
    public User register(String username, String email, String password) {
        UserDao userDao = dbManager.getUserDao();
        
        // Check if user already exists
        if (userDao.existsByEmail(email)) {
            return null;
        }
        
        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(PasswordUtils.hashPassword(password));
        user.setCreatedAt(System.currentTimeMillis());
        user.setLastLogin(System.currentTimeMillis());
        
        long userId = userDao.insert(user);
        if (userId != -1) {
            user.setId(userId);
            setCurrentUser(user);
            return user;
        }
        
        return null;
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        setCurrentUser(null);
    }
    
    /**
     * Update user profile
     * @param user Updated user data
     * @return true if update successful, false otherwise
     */
    public boolean updateUserProfile(User user) {
        int result = dbManager.getUserDao().update(user);
        if (result > 0 && currentUser != null && currentUser.getId() == user.getId()) {
            setCurrentUser(user); // Update current user reference
        }
        return result > 0;
    }
    
    /**
     * Get all coins from cache
     * @return List of coins
     */
    public List<CoinModel> getAllCoins() {
        return dbManager.getCoinDao().getAll();
    }
    
    /**
     * Search coins by name
     * @param query Search query
     * @return List of matching coins
     */
    public List<CoinModel> searchCoins(String query) {
        return dbManager.getCoinDao().searchByName(query);
    }

    /**
     * Get a coin by ID from cache
     */
    public CoinModel getCoinById(String coinId) {
        return dbManager.getCoinDao().findByCoinId(coinId);
    }
    
    /**
     * Get top coins by market cap
     * @param limit Maximum number of coins
     * @return List of top coins
     */
    public List<CoinModel> getTopCoins(int limit) {
        return dbManager.getCoinDao().getTopByMarketCap(limit);
    }
    
    /**
     * Get top gainers
     * @param limit Maximum number of coins
     * @return List of top gainers
     */
    public List<CoinModel> getTopGainers(int limit) {
        return dbManager.getCoinDao().getTopGainers(limit);
    }
    
    /**
     * Get top losers
     * @param limit Maximum number of coins
     * @return List of top losers
     */
    public List<CoinModel> getTopLosers(int limit) {
        return dbManager.getCoinDao().getTopLosers(limit);
    }
    
    /**
     * Add coin to user's favorites
     * @param coinId Coin ID
     * @return true if successful, false otherwise
     */
    public boolean addToFavorites(String coinId) {
        if (!isLoggedIn()) return false;
        return dbManager.getCoinDao().addToFavorites(currentUser.getId(), coinId);
    }
    
    /**
     * Remove coin from user's favorites
     * @param coinId Coin ID
     * @return true if successful, false otherwise
     */
    public boolean removeFromFavorites(String coinId) {
        if (!isLoggedIn()) return false;
        return dbManager.getCoinDao().removeFromFavorites(currentUser.getId(), coinId);
    }
    
    /**
     * Check if coin is in user's favorites
     * @param coinId Coin ID
     * @return true if favorite, false otherwise
     */
    public boolean isFavorite(String coinId) {
        if (!isLoggedIn()) return false;
        return dbManager.getCoinDao().isFavorite(currentUser.getId(), coinId);
    }
    
    /**
     * Get user's favorite coins
     * @return List of favorite coins
     */
    public List<CoinModel> getFavoriteCoins() {
        if (!isLoggedIn()) return null;
        return dbManager.getCoinDao().getFavoritesByUser(currentUser.getId());
    }
    
    /**
     * Add search query to history
     * @param query Search query
     * @return true if successful, false otherwise
     */
    public boolean addSearchQuery(String query) {
        if (!isLoggedIn()) return false;
        return dbManager.getSearchHistoryDao().addSearchQuery(currentUser.getId(), query);
    }
    
    /**
     * Get recent search queries
     * @param limit Maximum number of queries
     * @return List of recent search queries
     */
    public List<SearchHistoryItem> getRecentSearches(int limit) {
        if (!isLoggedIn()) return null;
        return dbManager.getSearchHistoryDao().getRecentSearches(currentUser.getId(), limit);
    }
    
    /**
     * Clear user's search history
     * @return Number of items cleared
     */
    public int clearSearchHistory() {
        if (!isLoggedIn()) return 0;
        return dbManager.getSearchHistoryDao().clearUserSearchHistory(currentUser.getId());
    }
    
    /**
     * Add portfolio transaction
     * @param coinId Coin ID
     * @param quantity Quantity (positive for buy, negative for sell)
     * @param pricePerCoin Price per coin
     * @param transactionType Transaction type (BUY/SELL)
     * @return true if successful, false otherwise
     */
    public boolean addPortfolioTransaction(String coinId, double quantity, double pricePerCoin, String transactionType) {
        if (!isLoggedIn()) return false;
        return dbManager.getPortfolioDao().addTransaction(currentUser.getId(), coinId, quantity, pricePerCoin, transactionType);
    }
    
    /**
     * Get user's portfolio
     * @return List of portfolio items
     */
    public List<PortfolioItem> getPortfolio() {
        if (!isLoggedIn()) return null;
        return dbManager.getPortfolioDao().getByUserId(currentUser.getId());
    }
    
    /**
     * Get portfolio summary
     * @return Portfolio summary data
     */
    public PortfolioDao.PortfolioSummary getPortfolioSummary() {
        if (!isLoggedIn()) return null;
        return dbManager.getPortfolioDao().getPortfolioSummary(currentUser.getId());
    }

    /**
     * Add fiat balance to current user
     * @param amount Amount to add (must be positive)
     * @return true if updated
     */
    public boolean addUserBalance(double amount) {
        if (!isLoggedIn() || amount <= 0) return false;
        currentUser.setBalance(currentUser.getBalance() + amount);
        int res = dbManager.getUserDao().update(currentUser);
        if (res > 0) {
            setCurrentUser(currentUser);
            // Log deposit transaction to transactions table (best-effort)
            try {
                TransactionRecord r = new TransactionRecord();
                r.setUserId(currentUser.getId());
                r.setType("DEPOSIT");
                r.setCoinId(null);
                r.setQuantity(0);
                r.setPricePerCoin(0);
                r.setFiatAmount(amount);
                r.setTimestamp(System.currentTimeMillis());
                dbManager.getTransactionDao().insert(r);
            } catch (Exception ignored) {}
            return true;
        }
        return false;
    }

    /**
     * Buy coin using fiat amount from user's balance.
     * Deducts fiat, adds a BUY transaction entry.
     */
    public boolean buyCoinFiat(String coinId, double fiatAmount, double pricePerCoin) {
        if (!isLoggedIn()) return false;
        if (fiatAmount <= 0 || pricePerCoin <= 0) return false;

        double balance = currentUser.getBalance();
        if (balance < fiatAmount) return false;

        double quantity = fiatAmount / pricePerCoin;
        boolean txOk = dbManager.getPortfolioDao().addTransaction(currentUser.getId(), coinId, quantity, pricePerCoin, "BUY");
        if (!txOk) return false;

        currentUser.setBalance(balance - fiatAmount);
        int res = dbManager.getUserDao().update(currentUser);
        if (res > 0) {
            setCurrentUser(currentUser);
            // Log BUY transaction in transactions table (best-effort)
            try {
                TransactionRecord r = new TransactionRecord();
                r.setUserId(currentUser.getId());
                r.setType("BUY");
                r.setCoinId(coinId);
                r.setQuantity(quantity);
                r.setPricePerCoin(pricePerCoin);
                r.setFiatAmount(fiatAmount);
                r.setTimestamp(System.currentTimeMillis());
                dbManager.getTransactionDao().insert(r);
            } catch (Exception ignored) {}
            return true;
        }
        return false;
    }
    
    /**
     * Cache coins in database
     * @param coins List of coins to cache
     */
    public void cacheCoins(List<CoinModel> coins) {
        dbManager.beginTransaction();
        try {
            for (CoinModel coin : coins) {
                // Check if coin already exists
                CoinModel existingCoin = dbManager.getCoinDao().findByCoinId(coin.getId());
                if (existingCoin != null) {
                    dbManager.getCoinDao().update(coin);
                } else {
                    dbManager.getCoinDao().insert(coin);
                }
            }
            dbManager.setTransactionSuccessful();
            updateLastSyncTime();
        } finally {
            dbManager.endTransaction();
        }
    }

    /**
     * Withdraw fiat from user's balance and log a transaction.
     */
    public boolean withdrawFiat(double amount) {
        if (!isLoggedIn() || amount <= 0) return false;
        double balance = currentUser.getBalance();
        if (balance < amount) return false;

        currentUser.setBalance(balance - amount);
        int res = dbManager.getUserDao().update(currentUser);
        if (res > 0) {
            setCurrentUser(currentUser);
            // Log WITHDRAW transaction (best-effort)
            try {
                TransactionRecord r = new TransactionRecord();
                r.setUserId(currentUser.getId());
                r.setType("WITHDRAW");
                r.setCoinId(null);
                r.setQuantity(0);
                r.setPricePerCoin(0);
                r.setFiatAmount(amount);
                r.setTimestamp(System.currentTimeMillis());
                dbManager.getTransactionDao().insert(r);
            } catch (Exception ignored) {}
            return true;
        }
        return false;
    }

    /**
     * Get transactions for current user.
     */
    public List<TransactionRecord> getTransactionsForCurrentUser() {
        if (!isLoggedIn()) return null;
        try {
            return dbManager.getTransactionDao().getByUserId(currentUser.getId());
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Get coins that need updating (older than specified time)
     * @param maxAgeMillis Maximum age in milliseconds
     * @return List of coins needing update
     */
    public List<CoinModel> getCoinsNeedingUpdate(long maxAgeMillis) {
        return dbManager.getCoinDao().getCoinsNeedingUpdate(maxAgeMillis);
    }
    
    /**
     * Clean up old cached data
     * @param maxAgeMillis Maximum age in milliseconds
     */
    public void cleanupOldData(long maxAgeMillis) {
        dbManager.getCoinDao().deleteOldCoins(maxAgeMillis);
        dbManager.getSearchHistoryDao().deleteOldEntries(30); // Delete search history older than 30 days
    }
    
    /**
     * Update last sync time
     */
    private void updateLastSyncTime() {
        prefs.edit().putLong(KEY_LAST_SYNC_TIME, System.currentTimeMillis()).apply();
    }
    
    /**
     * Get last sync time
     * @return Last sync timestamp or -1 if never synced
     */
    public long getLastSyncTime() {
        return prefs.getLong(KEY_LAST_SYNC_TIME, -1);
    }
    
    /**
     * Check if data needs syncing (older than specified time)
     * @param maxAgeMillis Maximum age in milliseconds
     * @return true if data needs syncing, false otherwise
     */
    public boolean needsSync(long maxAgeMillis) {
        long lastSync = getLastSyncTime();
        if (lastSync == -1) return true;
        return (System.currentTimeMillis() - lastSync) > maxAgeMillis;
    }
    
    /**
     * Get database statistics
     * @return Database statistics
     */
    public CryptoDatabaseManager.DatabaseStatistics getStatistics() {
        return dbManager.getStatistics();
    }
    
    /**
     * Clear all data (use with caution!)
     */
    public void clearAllData() {
        dbManager.clearAllData();
        setCurrentUser(null);
        prefs.edit().clear().apply();
    }
    
    /**
     * Close the database connection
     */
    public void close() {
        dbManager.close();
    }
}