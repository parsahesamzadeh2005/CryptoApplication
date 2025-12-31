package com.example.cryptoapplication.database;

import android.content.Context;

import com.example.cryptoapplication.database.dao.CoinDao;
import com.example.cryptoapplication.database.dao.CoinDaoImpl;
import com.example.cryptoapplication.database.dao.PortfolioDao;
import com.example.cryptoapplication.database.dao.PortfolioDaoImpl;
import com.example.cryptoapplication.database.dao.TransactionDao;
import com.example.cryptoapplication.database.dao.impl.TransactionDaoImpl;
import com.example.cryptoapplication.database.dao.SearchHistoryDao;
import com.example.cryptoapplication.database.dao.SearchHistoryDaoImpl;
import com.example.cryptoapplication.database.dao.UserDao;
import com.example.cryptoapplication.database.dao.UserDaoImpl;

/**
 * Database manager that provides access to all DAOs and manages database operations.
 * This class serves as the main entry point for all database operations.
 */
public class CryptoDatabaseManager {
    
    private static CryptoDatabaseManager instance;
    private final Context context;
    private final CryptoDatabaseHelper dbHelper;
    
    // DAO instances
    private final UserDao userDao;
    private final CoinDao coinDao;
    private final SearchHistoryDao searchHistoryDao;
    private final PortfolioDao portfolioDao;
    private final TransactionDao transactionDao;
    
    /**
     * Private constructor for singleton pattern
     * @param context Application context
     */
    private CryptoDatabaseManager(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = new CryptoDatabaseHelper(this.context);
        
        // Initialize DAOs
        this.userDao = new UserDaoImpl(this.context);
        this.coinDao = new CoinDaoImpl(this.context);
        this.searchHistoryDao = new SearchHistoryDaoImpl(this.context);
        this.portfolioDao = new PortfolioDaoImpl(this.context);
        this.transactionDao = new TransactionDaoImpl(this.dbHelper);
    }
    
    /**
     * Get the singleton instance of CryptoDatabaseManager
     * @param context Application context
     * @return The singleton instance
     */
    public static synchronized CryptoDatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new CryptoDatabaseManager(context);
        }
        return instance;
    }
    
    /**
     * Get the User DAO
     * @return UserDao instance
     */
    public UserDao getUserDao() {
        return userDao;
    }
    
    /**
     * Get the Coin DAO
     * @return CoinDao instance
     */
    public CoinDao getCoinDao() {
        return coinDao;
    }
    
    /**
     * Get the Search History DAO
     * @return SearchHistoryDao instance
     */
    public SearchHistoryDao getSearchHistoryDao() {
        return searchHistoryDao;
    }
    
    /**
     * Get the Portfolio DAO
     * @return PortfolioDao instance
     */
    public PortfolioDao getPortfolioDao() {
        return portfolioDao;
    }
    
    /**
     * Get the Transaction DAO
     * @return TransactionDao instance
     */
    public TransactionDao getTransactionDao() {
        return transactionDao;
    }
    
    /**
     * Get the database helper (for direct database access if needed)
     * @return CryptoDatabaseHelper instance
     */
    public CryptoDatabaseHelper getDbHelper() {
        return dbHelper;
    }
    
    /**
     * Close the database connection
     */
    public void close() {
        dbHelper.close();
    }
    
    /**
     * Get writable database instance
     */
    public android.database.sqlite.SQLiteDatabase getWritableDatabase() {
        return dbHelper.getWritableDatabase();
    }
    
    /**
     * Begin a database transaction
     */
    public void beginTransaction() {
        dbHelper.getWritableDatabase().beginTransaction();
    }
    
    /**
     * Set the transaction as successful
     */
    public void setTransactionSuccessful() {
        dbHelper.getWritableDatabase().setTransactionSuccessful();
    }
    
    /**
     * End the current transaction
     */
    public void endTransaction() {
        dbHelper.getWritableDatabase().endTransaction();
    }
    
    /**
     * Check if currently in a transaction
     * @return true if in transaction, false otherwise
     */
    public boolean inTransaction() {
        return dbHelper.getWritableDatabase().inTransaction();
    }
    
    /**
     * Execute a database operation within a transaction
     * @param operation The operation to execute
     * @return true if successful, false otherwise
     */
    public boolean executeInTransaction(TransactionOperation operation) {
        boolean success = false;
        try {
            beginTransaction();
            operation.execute();
            setTransactionSuccessful();
            success = true;
        } catch (Exception e) {
            // Transaction will be rolled back automatically
            e.printStackTrace();
        } finally {
            endTransaction();
        }
        return success;
    }
    
    /**
     * Clear all data from all tables (use with caution!)
     */
    public void clearAllData() {
        executeInTransaction(new TransactionOperation() {
            @Override
            public void execute() {
                userDao.deleteAll();
                coinDao.deleteAll();
                searchHistoryDao.deleteAll();
                portfolioDao.deleteAll();
                transactionDao.deleteAll();
            }
        });
    }
    
    /**
     * Get database statistics
     * @return DatabaseStatistics object containing counts for each table
     */
    public DatabaseStatistics getStatistics() {
        return new DatabaseStatistics(
            userDao.count(),
            coinDao.count(),
            searchHistoryDao.count(),
            portfolioDao.count()
        );
    }
    
    /**
     * Interface for transaction operations
     */
    public interface TransactionOperation {
        void execute();
    }
    
    /**
     * Database statistics data class
     */
    public static class DatabaseStatistics {
        public final int userCount;
        public final int coinCount;
        public final int searchHistoryCount;
        public final int portfolioCount;
        
        public DatabaseStatistics(int userCount, int coinCount, int searchHistoryCount, int portfolioCount) {
            this.userCount = userCount;
            this.coinCount = coinCount;
            this.searchHistoryCount = searchHistoryCount;
            this.portfolioCount = portfolioCount;
        }
        
        // Convenience getters to support usage examples
        public int getTotalUsers() { return userCount; }
        public int getTotalCoins() { return coinCount; }
        public int getTotalSearchHistory() { return searchHistoryCount; }
        public int getTotalPortfolioItems() { return portfolioCount; }
        public long getDatabaseSize() { return 0L; }

        @Override
        public String toString() {
            return String.format("Database Statistics:\nUsers: %d\nCoins: %d\nSearch History: %d\nPortfolio Items: %d",
                userCount, coinCount, searchHistoryCount, portfolioCount);
        }
    }
}