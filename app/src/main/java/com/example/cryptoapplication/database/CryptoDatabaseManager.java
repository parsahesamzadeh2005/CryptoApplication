package com.example.cryptoapplication.database;

import android.content.Context;

import com.example.cryptoapplication.database.dao.CoinDao;
import com.example.cryptoapplication.database.dao.CoinDaoImpl;
import com.example.cryptoapplication.database.dao.PortfolioDao;
import com.example.cryptoapplication.database.dao.PortfolioDaoImpl;
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
    private final PortfolioDao portfolioDao;
    
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
        this.portfolioDao = new PortfolioDaoImpl(this.context);
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
     * Get the Portfolio DAO
     * @return PortfolioDao instance
     */
    public PortfolioDao getPortfolioDao() {
        return portfolioDao;
    }
    
    /**
     * Close the database connection
     */
    public void close() {
        dbHelper.close();
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
}