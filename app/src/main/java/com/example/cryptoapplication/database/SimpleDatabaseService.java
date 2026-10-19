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
 * Uses SharedPreferences for trading data to avoid all database lock issues.
 *
 * All numeric values (balance, holdings) are stored as String representations
 * of double so no float precision is lost at the persistence layer.
 * Legacy float keys are migrated to String on first read.
 */
public class SimpleDatabaseService {

    private static SimpleDatabaseService instance;
    private final CryptoDatabaseManager dbManager;
    private final SharedPreferences userPrefs;
    private final SharedPreferences tradingPrefs;
    private User currentUser;
    private String lastErrorMessage = "";

    private static final String USER_PREFS    = "user_data";
    private static final String TRADING_PREFS = "trading_data";
    private static final String USER_ID_KEY   = "current_user_id";
    private static final String BALANCE_KEY   = "user_balance_";
    private static final String HOLDINGS_KEY  = "holdings_";

    // -------------------------------------------------------------------------
    // Double <-> SharedPreferences helpers (String-backed, no float loss)
    // -------------------------------------------------------------------------

    /**
     * Read a double that was stored as a String.
     * If the key holds a legacy float value it is migrated in-place and the
     * corrected double is returned.
     */
    private double readDouble(String key, double defaultValue) {
        Map<String, ?> all = tradingPrefs.getAll();
        Object raw = all.get(key);
        if (raw == null) return defaultValue;

        if (raw instanceof String) {
            try {
                return Double.parseDouble((String) raw);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }

        // Legacy float — migrate to String now so future reads are precise
        if (raw instanceof Float) {
            // Re-parse via string to get the closest double representation of
            // the float's decimal value (e.g. 325.0f → "325.0" → 325.0 exactly)
            double migrated = Double.parseDouble(Float.toString((Float) raw));
            tradingPrefs.edit().remove(key).putString(key, Double.toString(migrated)).apply();
            return migrated;
        }

        return defaultValue;
    }

    /** Persist a double as a String (lossless). */
    private void writeDouble(String key, double value) {
        tradingPrefs.edit().putString(key, Double.toString(value)).apply();
    }

    /** Persist a double as a String synchronously (commit). */
    private boolean writeDoubleSync(String key, double value) {
        return tradingPrefs.edit().putString(key, Double.toString(value)).commit();
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    private SimpleDatabaseService(Context context) {
        this.dbManager   = CryptoDatabaseManager.getInstance(context);
        this.userPrefs   = context.getSharedPreferences(USER_PREFS,    Context.MODE_PRIVATE);
        this.tradingPrefs = context.getSharedPreferences(TRADING_PREFS, Context.MODE_PRIVATE);
        loadCurrentUser();
    }

    public static SimpleDatabaseService getInstance(Context context) {
        if (instance == null) {
            instance = new SimpleDatabaseService(context);
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // User management
    // -------------------------------------------------------------------------

    public boolean isUserLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        if (currentUser != null) {
            double balance = readDouble(BALANCE_KEY + currentUser.getId(), 0.0);
            currentUser.setBalance(balance);
        }
        return currentUser;
    }

    public User loginUser(String email, String password) {
        try {
            User user = dbManager.getUserDao().findByEmail(email);
            if (user != null && PasswordUtils.verifyPassword(password, user.getPassword())) {
                setCurrentUser(user);
                String key = BALANCE_KEY + user.getId();
                if (tradingPrefs.getAll().get(key) == null) {
                    writeDouble(key, user.getBalance());
                } else {
                    // Trigger migration if stored as float
                    readDouble(key, user.getBalance());
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
                writeDouble(BALANCE_KEY + id, 0.0);
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

    // -------------------------------------------------------------------------
    // Balance management
    // -------------------------------------------------------------------------

    public boolean addMoneyToBalance(double amount) {
        if (!isUserLoggedIn() || amount <= 0) return false;

        String key = BALANCE_KEY + currentUser.getId();
        double current = readDouble(key, 0.0);
        double updated = current + amount;

        boolean success = writeDoubleSync(key, updated);
        if (success) {
            currentUser.setBalance(updated);
            System.out.println("Added $" + amount + " to balance. New balance: $" + updated);
        }
        return success;
    }

    public boolean takeMoneyFromBalance(double amount) {
        if (!isUserLoggedIn() || amount <= 0) return false;

        String key = BALANCE_KEY + currentUser.getId();
        double current = readDouble(key, 0.0);

        if (amount > current + Math.max(1e-6, current * 1e-6)) {
            lastErrorMessage = "Insufficient balance";
            return false;
        }

        double updated = Math.max(0.0, current - amount);
        boolean success = writeDoubleSync(key, updated);
        if (success) {
            currentUser.setBalance(updated);
            System.out.println("Took $" + amount + " from balance. New balance: $" + updated);
        }
        return success;
    }

    // -------------------------------------------------------------------------
    // Trading
    // -------------------------------------------------------------------------

    public boolean buyCryptocurrency(String coinId, double quantity, double price) {
        lastErrorMessage = "";
        System.out.println("=== BUY START === Coin: " + coinId +
                ", Qty: " + quantity + ", Price: $" + price);

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
        double currentBalance = readDouble(balanceKey, 0.0);

        System.out.println("Need: $" + totalCost + ", Have: $" + currentBalance);

        double epsilon = Math.max(1e-6, currentBalance * 1e-6);
        if (totalCost > currentBalance + epsilon) {
            lastErrorMessage = "Not enough money. Need $" + totalCost +
                    ", have $" + currentBalance;
            return false;
        }
        // Clamp to avoid tiny negative balances from floating-point arithmetic
        double cost = Math.min(totalCost, currentBalance);

        try {
            double newBalance = Math.max(0.0, currentBalance - cost);
            writeDouble(balanceKey, newBalance);

            String holdingsKey = HOLDINGS_KEY + currentUser.getId() + "_" + coinId;
            double currentHoldings = readDouble(holdingsKey, 0.0);
            writeDouble(holdingsKey, currentHoldings + quantity);

            String txKey  = "tx_" + currentUser.getId() + "_" + System.currentTimeMillis();
            String txData = coinId + "|" + quantity + "|" + price + "|BUY|" +
                    System.currentTimeMillis();
            tradingPrefs.edit().putString(txKey, txData).apply();

            currentUser.setBalance(newBalance);
            System.out.println("BUY SUCCESS! New balance: $" + newBalance);
            return true;

        } catch (Exception e) {
            lastErrorMessage = "Error: " + e.getMessage();
            System.out.println("BUY ERROR: " + e.getMessage());
            return false;
        }
    }

    public boolean sellCryptocurrency(String coinId, double quantity, double price) {
        lastErrorMessage = "";
        System.out.println("=== SELL START === Coin: " + coinId +
                ", Qty: " + quantity + ", Price: $" + price);

        if (!isUserLoggedIn()) {
            lastErrorMessage = "Not logged in";
            return false;
        }
        if (quantity <= 0 || price <= 0) {
            lastErrorMessage = "Invalid values";
            return false;
        }

        String holdingsKey = HOLDINGS_KEY + currentUser.getId() + "_" + coinId;
        double currentHoldings = readDouble(holdingsKey, 0.0);

        System.out.println("Want to sell: " + quantity + ", Have: " + currentHoldings);

        double epsilon = Math.max(1e-9, currentHoldings * 1e-6);
        if (quantity > currentHoldings + epsilon) {
            lastErrorMessage = "Not enough crypto. Need " + quantity +
                    ", have " + currentHoldings;
            return false;
        }
        // Clamp to avoid tiny negative holdings
        double qty = Math.min(quantity, currentHoldings);

        try {
            double totalEarned = qty * price;

            String balanceKey = BALANCE_KEY + currentUser.getId();
            double currentBalance = readDouble(balanceKey, 0.0);
            double newBalance = currentBalance + totalEarned;
            writeDouble(balanceKey, newBalance);

            double newHoldings = Math.max(0.0, currentHoldings - qty);
            writeDouble(holdingsKey, newHoldings);

            String txKey  = "tx_" + currentUser.getId() + "_" + System.currentTimeMillis();
            String txData = coinId + "|" + qty + "|" + price + "|SELL|" +
                    System.currentTimeMillis();
            tradingPrefs.edit().putString(txKey, txData).apply();

            currentUser.setBalance(newBalance);
            System.out.println("SELL SUCCESS! New balance: $" + newBalance +
                    ", Holdings: " + newHoldings);
            return true;

        } catch (Exception e) {
            lastErrorMessage = "Error: " + e.getMessage();
            System.out.println("SELL ERROR: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Holdings / portfolio queries
    // -------------------------------------------------------------------------

    public double calculateUserHoldings(String coinId) {
        if (!isUserLoggedIn()) return 0.0;
        return readDouble(HOLDINGS_KEY + currentUser.getId() + "_" + coinId, 0.0);
    }

    public List<ConsolidatedAsset> getConsolidatedAssets() {
        List<ConsolidatedAsset> consolidatedAssets = new ArrayList<>();
        if (!isUserLoggedIn()) return consolidatedAssets;

        try {
            List<PortfolioItem> allTransactions = getUserPortfolio();
            Map<String, ConsolidatedAsset> assetMap = new HashMap<>();

            for (PortfolioItem tx : allTransactions) {
                String coinId = tx.getCoinId();
                ConsolidatedAsset asset = assetMap.get(coinId);
                if (asset == null) {
                    CoinModel coin = findCoinById(coinId);
                    String name   = coin != null ? coin.getName()   : coinId;
                    String symbol = coin != null ? coin.getSymbol() : coinId.toUpperCase();
                    asset = new ConsolidatedAsset(coinId, name, symbol);
                    assetMap.put(coinId, asset);
                }

                if ("BUY".equals(tx.getTransactionType())) {
                    asset.setTotalQuantity(asset.getTotalQuantity() + tx.getQuantity());
                } else if ("SELL".equals(tx.getTransactionType())) {
                    asset.setTotalQuantity(asset.getTotalQuantity() - tx.getQuantity());
                }

                asset.setTransactionCount(asset.getTransactionCount() + 1);
                asset.setCurrentPrice(tx.getPurchasePrice());
            }

            for (ConsolidatedAsset asset : assetMap.values()) {
                if (asset.getTotalQuantity() > 0) {
                    asset.setTotalValue(asset.getTotalQuantity() * asset.getCurrentPrice());
                    consolidatedAssets.add(asset);
                }
            }

            consolidatedAssets.sort((a, b) ->
                    Double.compare(b.getTotalValue(), a.getTotalValue()));

        } catch (Exception e) {
            System.out.println("Consolidated assets error: " + e.getMessage());
        }

        return consolidatedAssets;
    }

    public List<PortfolioItem> getUserPortfolio() {
        List<PortfolioItem> portfolio = new ArrayList<>();
        if (!isUserLoggedIn()) return portfolio;

        try {
            Map<String, ?> allPrefs = tradingPrefs.getAll();
            String prefix = "tx_" + currentUser.getId() + "_";

            for (Map.Entry<String, ?> entry : allPrefs.entrySet()) {
                if (!entry.getKey().startsWith(prefix)) continue;
                Object val = entry.getValue();
                if (!(val instanceof String)) continue;

                String[] parts = ((String) val).split("\\|");
                if (parts.length < 5) continue;

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

            portfolio.sort((a, b) -> Long.compare(b.getPurchaseDate(), a.getPurchaseDate()));

        } catch (Exception e) {
            System.out.println("Portfolio error: " + e.getMessage());
        }

        return portfolio;
    }

    // -------------------------------------------------------------------------
    // Coin data (delegate to SQLite)
    // -------------------------------------------------------------------------

    public List<CoinModel> getAllCoins() {
        try { return dbManager.getCoinDao().getAll(); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    public List<CoinModel> searchForCoins(String searchText) {
        try { return dbManager.getCoinDao().searchByName(searchText); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    public CoinModel findCoinById(String coinId) {
        try { return dbManager.getCoinDao().findByCoinId(coinId); }
        catch (Exception e) { return null; }
    }

    public List<CoinModel> getTopValueCoins(int count) {
        try { return dbManager.getCoinDao().getTopByMarketCap(count); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    public List<CoinModel> getWinningCoins(int count) {
        try { return dbManager.getCoinDao().getTopGainers(count); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    public List<CoinModel> getLosingCoins(int count) {
        try { return dbManager.getCoinDao().getTopLosers(count); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    public void saveCoinsToCache(List<CoinModel> coins) {
        try {
            dbManager.beginTransaction();
            for (CoinModel coin : coins) {
                CoinModel existing = dbManager.getCoinDao().findByCoinId(coin.getId());
                if (existing != null) dbManager.getCoinDao().update(coin);
                else                  dbManager.getCoinDao().insert(coin);
            }
            dbManager.setTransactionSuccessful();
        } catch (Exception e) {
            System.out.println("Cache error: " + e.getMessage());
        } finally {
            try { dbManager.endTransaction(); } catch (Exception ignored) {}
        }
    }

    // -------------------------------------------------------------------------
    // Favourites (delegate to SQLite)
    // -------------------------------------------------------------------------

    public boolean addCoinToFavorites(String coinId) {
        if (!isUserLoggedIn()) return false;
        try { return dbManager.getCoinDao().addToFavorites(currentUser.getId(), coinId); }
        catch (Exception e) { return false; }
    }

    public boolean removeCoinFromFavorites(String coinId) {
        if (!isUserLoggedIn()) return false;
        try { return dbManager.getCoinDao().removeFromFavorites(currentUser.getId(), coinId); }
        catch (Exception e) { return false; }
    }

    public boolean isCoinInFavorites(String coinId) {
        if (!isUserLoggedIn()) return false;
        try { return dbManager.getCoinDao().isFavorite(currentUser.getId(), coinId); }
        catch (Exception e) { return false; }
    }

    public List<CoinModel> getFavoriteCoins() {
        if (!isUserLoggedIn()) return new ArrayList<>();
        try { return dbManager.getCoinDao().getFavoritesByUser(currentUser.getId()); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    // -------------------------------------------------------------------------
    // Error handling
    // -------------------------------------------------------------------------

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void loadCurrentUser() {
        long userId = userPrefs.getLong(USER_ID_KEY, -1);
        if (userId == -1) return;
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

    private void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            userPrefs.edit().putLong(USER_ID_KEY, user.getId()).apply();
        } else {
            userPrefs.edit().remove(USER_ID_KEY).apply();
        }
    }

    public void closeDatabase() {
        try { dbManager.close(); } catch (Exception ignored) {}
    }
}
