package com.example.cryptoapplication.database;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.cryptoapplication.models.CoinModel;
import com.example.cryptoapplication.models.PortfolioItem;
import com.example.cryptoapplication.models.User;
import com.example.cryptoapplication.utils.PasswordUtils;
import java.util.List;

public class SimpleDatabaseService {
    
    private static SimpleDatabaseService ourInstance;
    private final CryptoDatabaseManager databaseManager;
    private final SharedPreferences userPreferences;
    private User currentLoggedInUser;
    
    private static final String PREFERENCES_NAME = "CryptoDatabasePrefs";
    private static final String CURRENT_USER_KEY = "current_user_id";
    
    
    private SimpleDatabaseService(Context context) {
        this.databaseManager = CryptoDatabaseManager.getInstance(context);
        this.userPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        loadCurrentUser();
    }
    
    public static SimpleDatabaseService getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new SimpleDatabaseService(context);
        }
        return ourInstance;
    }
    
    public boolean isUserLoggedIn() {
        return currentLoggedInUser != null;
    }
    
    public User getCurrentUser() {
        return currentLoggedInUser;
    }
    
    public User loginUser(String email, String password) {
        User foundUser = databaseManager.getUserDao().findByEmail(email);
        
        if (foundUser != null && PasswordUtils.verifyPassword(password, foundUser.getPassword())) {
            setCurrentUser(foundUser);
            return foundUser;
        }
        
        return null;
    }
    
    public User registerNewUser(String username, String email, String password) {
        if (databaseManager.getUserDao().existsByEmail(email)) {
            return null;
        }
        
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(PasswordUtils.hashPassword(password));
        newUser.setCreatedAt(System.currentTimeMillis());
        newUser.setLastLogin(System.currentTimeMillis());
        newUser.setBalance(0.0);
        
        long userId = databaseManager.getUserDao().insert(newUser);
        if (userId != -1) {
            newUser.setId(userId);
            setCurrentUser(newUser);
            return newUser;
        }
        
        return null;
    }
    
    public void logoutCurrentUser() {
        setCurrentUser(null);
    }
    
    public boolean addMoneyToBalance(double amount) {
        if (!isUserLoggedIn() || amount <= 0) {
            return false;
        }
        
        double newBalance = currentLoggedInUser.getBalance() + amount;
        currentLoggedInUser.setBalance(newBalance);
        
        int updateResult = databaseManager.getUserDao().update(currentLoggedInUser);
        if (updateResult > 0) {
            setCurrentUser(currentLoggedInUser);
            return true;
        }
        
        return false;
    }
    
    public boolean takeMoneyFromBalance(double amount) {
        if (!isUserLoggedIn() || amount <= 0) {
            return false;
        }
        
        double currentBalance = currentLoggedInUser.getBalance();
        if (currentBalance < amount) {
            return false;
        }
        
        double newBalance = currentBalance - amount;
        currentLoggedInUser.setBalance(newBalance);
        
        int updateResult = databaseManager.getUserDao().update(currentLoggedInUser);
        if (updateResult > 0) {
            setCurrentUser(currentLoggedInUser);
            return true;
        }
        
        return false;
    }
    
    public List<CoinModel> getAllCoins() {
        return databaseManager.getCoinDao().getAll();
    }
    
    public List<CoinModel> searchForCoins(String searchText) {
        return databaseManager.getCoinDao().searchByName(searchText);
    }
    
    public CoinModel findCoinById(String coinId) {
        return databaseManager.getCoinDao().findByCoinId(coinId);
    }
    
    public List<CoinModel> getTopValueCoins(int howMany) {
        return databaseManager.getCoinDao().getTopByMarketCap(howMany);
    }
    
    public List<CoinModel> getWinningCoins(int howMany) {
        return databaseManager.getCoinDao().getTopGainers(howMany);
    }
    
    public List<CoinModel> getLosingCoins(int howMany) {
        return databaseManager.getCoinDao().getTopLosers(howMany);
    }
    
    public void saveCoinsToCache(List<CoinModel> coins) {
        databaseManager.beginTransaction();
        try {
            for (CoinModel coin : coins) {
                CoinModel existingCoin = databaseManager.getCoinDao().findByCoinId(coin.getId());
                if (existingCoin != null) {
                    databaseManager.getCoinDao().update(coin);
                } else {
                    databaseManager.getCoinDao().insert(coin);
                }
            }
            databaseManager.setTransactionSuccessful();
        } finally {
            databaseManager.endTransaction();
        }
    }
    
    public boolean addCoinToFavorites(String coinId) {
        if (!isUserLoggedIn()) {
            return false;
        }
        return databaseManager.getCoinDao().addToFavorites(currentLoggedInUser.getId(), coinId);
    }
    
    public boolean removeCoinFromFavorites(String coinId) {
        if (!isUserLoggedIn()) {
            return false;
        }
        return databaseManager.getCoinDao().removeFromFavorites(currentLoggedInUser.getId(), coinId);
    }
    
    public boolean isCoinInFavorites(String coinId) {
        if (!isUserLoggedIn()) {
            return false;
        }
        return databaseManager.getCoinDao().isFavorite(currentLoggedInUser.getId(), coinId);
    }
    
    public List<CoinModel> getFavoriteCoins() {
        if (!isUserLoggedIn()) {
            return null;
        }
        return databaseManager.getCoinDao().getFavoritesByUser(currentLoggedInUser.getId());
    }
    
    // Fixed: Now accepts coin quantity directly instead of money amount
    public boolean buyCryptocurrency(String coinId, double coinQuantity, double pricePerCoin) {
        if (!isUserLoggedIn() || coinQuantity <= 0 || pricePerCoin <= 0) {
            return false;
        }
        
        // Calculate total cost from coin quantity (not the other way around)
        double totalCost = coinQuantity * pricePerCoin;
        
        if (currentLoggedInUser.getBalance() < totalCost) {
            return false;
        }
        
        boolean portfolioUpdated = databaseManager.getPortfolioDao().addTransaction(
            currentLoggedInUser.getId(), 
            coinId, 
            coinQuantity, 
            pricePerCoin, 
            "BUY"
        );
        
        if (!portfolioUpdated) {
            return false;
        }
        
        return takeMoneyFromBalance(totalCost);
    }
    
    // Added: Method to handle selling specific coin quantities
    public boolean sellCryptocurrency(String coinId, double coinQuantity, double pricePerCoin) {
        if (!isUserLoggedIn() || coinQuantity <= 0 || pricePerCoin <= 0) {
            return false;
        }
        
        // Calculate total proceeds from coin quantity
        double totalProceeds = coinQuantity * pricePerCoin;
        
        // Add sell transaction to portfolio (negative quantity for sell)
        boolean portfolioUpdated = databaseManager.getPortfolioDao().addTransaction(
            currentLoggedInUser.getId(), 
            coinId, 
            -coinQuantity, // Negative quantity indicates sell
            pricePerCoin, 
            "SELL"
        );
        
        if (!portfolioUpdated) {
            return false;
        }
        
        return addMoneyToBalance(totalProceeds);
    }
    
    public List<PortfolioItem> getUserPortfolio() {
        if (!isUserLoggedIn()) {
            return null;
        }
        return databaseManager.getPortfolioDao().getByUserId(currentLoggedInUser.getId());
    }
    
    private void loadCurrentUser() {
        long savedUserId = userPreferences.getLong(CURRENT_USER_KEY, -1);
        
        if (savedUserId != -1) {
            User savedUser = databaseManager.getUserDao().getById(savedUserId);
            if (savedUser != null) {
                currentLoggedInUser = savedUser;
            } else {
                userPreferences.edit().remove(CURRENT_USER_KEY).apply();
            }
        }
    }
    
    private void setCurrentUser(User user) {
        this.currentLoggedInUser = user;
        
        if (user != null) {
            userPreferences.edit().putLong(CURRENT_USER_KEY, user.getId()).apply();
        } else {
            userPreferences.edit().remove(CURRENT_USER_KEY).apply();
        }
    }
    
    public void closeDatabase() {
        databaseManager.close();
    }
}