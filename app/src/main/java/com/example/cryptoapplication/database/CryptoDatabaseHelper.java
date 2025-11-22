package com.example.cryptoapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper class for managing the crypto application database.
 * Handles database creation, upgrades, and provides access to the database.
 */
public class CryptoDatabaseHelper extends SQLiteOpenHelper {
    
    // Database information
    private static final String DATABASE_NAME = "crypto_database.db";
    private static final int DATABASE_VERSION = DatabaseMigration.getLatestVersion();
    
    // SQL statements for creating tables
    private static final String SQL_CREATE_USERS_TABLE =
            "CREATE TABLE " + CryptoDatabaseContract.UserEntry.TABLE_NAME + " (" +
            CryptoDatabaseContract.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CryptoDatabaseContract.UserEntry.COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
            CryptoDatabaseContract.UserEntry.COLUMN_USERNAME + " TEXT NOT NULL, " +
            CryptoDatabaseContract.UserEntry.COLUMN_PASSWORD_HASH + " TEXT NOT NULL, " +
            CryptoDatabaseContract.UserEntry.COLUMN_BALANCE + " REAL DEFAULT 0, " +
            CryptoDatabaseContract.UserEntry.COLUMN_CREATED_AT + " INTEGER NOT NULL, " +
            CryptoDatabaseContract.UserEntry.COLUMN_LAST_LOGIN + " INTEGER, " +
            // Ensure columns added by migrations exist on fresh installs
            CryptoDatabaseContract.UserEntry.COLUMN_PROFILE_IMAGE + " TEXT, " +
            CryptoDatabaseContract.UserEntry.COLUMN_BIO + " TEXT, " +
            CryptoDatabaseContract.UserEntry.COLUMN_PHONE + " TEXT, " +
            CryptoDatabaseContract.UserEntry.COLUMN_IS_ACTIVE + " INTEGER DEFAULT 1);";
    
    private static final String SQL_CREATE_FAVORITE_COINS_TABLE =
            "CREATE TABLE " + CryptoDatabaseContract.FavoriteCoinEntry.TABLE_NAME + " (" +
            CryptoDatabaseContract.FavoriteCoinEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CryptoDatabaseContract.FavoriteCoinEntry.COLUMN_USER_ID + " INTEGER NOT NULL, " +
            CryptoDatabaseContract.FavoriteCoinEntry.COLUMN_COIN_ID + " TEXT NOT NULL, " +
            CryptoDatabaseContract.FavoriteCoinEntry.COLUMN_SYMBOL + " TEXT NOT NULL, " +
            CryptoDatabaseContract.FavoriteCoinEntry.COLUMN_NAME + " TEXT NOT NULL, " +
            CryptoDatabaseContract.FavoriteCoinEntry.COLUMN_IMAGE_URL + " TEXT, " +
            CryptoDatabaseContract.FavoriteCoinEntry.COLUMN_ADDED_AT + " INTEGER NOT NULL, " +
            "FOREIGN KEY (" + CryptoDatabaseContract.FavoriteCoinEntry.COLUMN_USER_ID + ") " +
            "REFERENCES " + CryptoDatabaseContract.UserEntry.TABLE_NAME + "(" + 
            CryptoDatabaseContract.UserEntry._ID + ") ON DELETE CASCADE, " +
            "UNIQUE(" + CryptoDatabaseContract.FavoriteCoinEntry.COLUMN_USER_ID + ", " +
            CryptoDatabaseContract.FavoriteCoinEntry.COLUMN_COIN_ID + "));";
    
    private static final String SQL_CREATE_COIN_CACHE_TABLE =
            "CREATE TABLE " + CryptoDatabaseContract.CoinCacheEntry.TABLE_NAME + " (" +
            CryptoDatabaseContract.CoinCacheEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CryptoDatabaseContract.CoinCacheEntry.COLUMN_COIN_ID + " TEXT UNIQUE NOT NULL, " +
            CryptoDatabaseContract.CoinCacheEntry.COLUMN_SYMBOL + " TEXT NOT NULL, " +
            CryptoDatabaseContract.CoinCacheEntry.COLUMN_NAME + " TEXT NOT NULL, " +
            CryptoDatabaseContract.CoinCacheEntry.COLUMN_CURRENT_PRICE + " REAL NOT NULL, " +
            CryptoDatabaseContract.CoinCacheEntry.COLUMN_IMAGE_URL + " TEXT, " +
            CryptoDatabaseContract.CoinCacheEntry.COLUMN_PRICE_CHANGE_24H + " REAL, " +
            CryptoDatabaseContract.CoinCacheEntry.COLUMN_MARKET_CAP + " REAL, " +
            CryptoDatabaseContract.CoinCacheEntry.COLUMN_LAST_UPDATED + " INTEGER NOT NULL, " +
            CryptoDatabaseContract.CoinCacheEntry.COLUMN_CURRENCY + " TEXT NOT NULL, " +
            // Columns added in Migration 5->6, ensure they exist for new installs
            "image TEXT, " +
            "market_cap_rank INTEGER, " +
            "fully_diluted_valuation REAL, " +
            "total_volume REAL, " +
            "high_24h REAL, " +
            "low_24h REAL, " +
            "price_change_percentage_24h REAL, " +
            "market_cap_change_24h REAL, " +
            "market_cap_change_percentage_24h REAL, " +
            "circulating_supply REAL, " +
            "total_supply REAL, " +
            "max_supply REAL, " +
            "cached_at INTEGER);";
    
    private static final String SQL_CREATE_SEARCH_HISTORY_TABLE =
            "CREATE TABLE " + CryptoDatabaseContract.SearchHistoryEntry.TABLE_NAME + " (" +
            CryptoDatabaseContract.SearchHistoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CryptoDatabaseContract.SearchHistoryEntry.COLUMN_USER_ID + " INTEGER NOT NULL, " +
            CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCH_QUERY + " TEXT NOT NULL, " +
            CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCHED_AT + " INTEGER NOT NULL, " +
            "FOREIGN KEY (" + CryptoDatabaseContract.SearchHistoryEntry.COLUMN_USER_ID + ") " +
            "REFERENCES " + CryptoDatabaseContract.UserEntry.TABLE_NAME + "(" + 
            CryptoDatabaseContract.UserEntry._ID + ") ON DELETE CASCADE);";
    
    private static final String SQL_CREATE_PORTFOLIO_TABLE =
            "CREATE TABLE " + CryptoDatabaseContract.PortfolioEntry.TABLE_NAME + " (" +
            CryptoDatabaseContract.PortfolioEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CryptoDatabaseContract.PortfolioEntry.COLUMN_USER_ID + " INTEGER NOT NULL, " +
            CryptoDatabaseContract.PortfolioEntry.COLUMN_COIN_ID + " TEXT NOT NULL, " +
            CryptoDatabaseContract.PortfolioEntry.COLUMN_AMOUNT + " REAL, " +
            CryptoDatabaseContract.PortfolioEntry.COLUMN_QUANTITY + " REAL NOT NULL, " +
            CryptoDatabaseContract.PortfolioEntry.COLUMN_PURCHASE_PRICE + " REAL NOT NULL, " +
            CryptoDatabaseContract.PortfolioEntry.COLUMN_PURCHASE_DATE + " INTEGER NOT NULL, " +
            CryptoDatabaseContract.PortfolioEntry.COLUMN_CURRENT_PRICE + " REAL NOT NULL, " +
            CryptoDatabaseContract.PortfolioEntry.COLUMN_TOTAL_VALUE + " REAL NOT NULL, " +
            CryptoDatabaseContract.PortfolioEntry.COLUMN_PROFIT_LOSS + " REAL DEFAULT 0, " +
            CryptoDatabaseContract.PortfolioEntry.COLUMN_TRANSACTION_FEE + " REAL DEFAULT 0, " +
            CryptoDatabaseContract.PortfolioEntry.COLUMN_TRANSACTION_TYPE + " TEXT DEFAULT 'BUY', " +
            CryptoDatabaseContract.PortfolioEntry.COLUMN_CREATED_AT + " INTEGER, " +
            CryptoDatabaseContract.PortfolioEntry.COLUMN_UPDATED_AT + " INTEGER, " +
            // Columns added in Migration 5->6, ensure they exist for new installs
            "last_updated INTEGER, " +
            "FOREIGN KEY (" + CryptoDatabaseContract.PortfolioEntry.COLUMN_USER_ID + ") " +
            "REFERENCES " + CryptoDatabaseContract.UserEntry.TABLE_NAME + "(" + 
            CryptoDatabaseContract.UserEntry._ID + ") ON DELETE CASCADE, " +
            "UNIQUE(" + CryptoDatabaseContract.PortfolioEntry.COLUMN_USER_ID + ", " +
            CryptoDatabaseContract.PortfolioEntry.COLUMN_COIN_ID + "));";

    private static final String SQL_CREATE_TRANSACTIONS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + CryptoDatabaseContract.TransactionEntry.TABLE_NAME + " (" +
            CryptoDatabaseContract.TransactionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CryptoDatabaseContract.TransactionEntry.COLUMN_USER_ID + " INTEGER NOT NULL, " +
            CryptoDatabaseContract.TransactionEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
            CryptoDatabaseContract.TransactionEntry.COLUMN_COIN_ID + " TEXT, " +
            CryptoDatabaseContract.TransactionEntry.COLUMN_QUANTITY + " REAL, " +
            CryptoDatabaseContract.TransactionEntry.COLUMN_PRICE_PER_COIN + " REAL, " +
            CryptoDatabaseContract.TransactionEntry.COLUMN_FIAT_AMOUNT + " REAL NOT NULL, " +
            CryptoDatabaseContract.TransactionEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL, " +
            "FOREIGN KEY(" + CryptoDatabaseContract.TransactionEntry.COLUMN_USER_ID + ") REFERENCES " +
            CryptoDatabaseContract.UserEntry.TABLE_NAME + "(" + CryptoDatabaseContract.UserEntry._ID + ") ON DELETE CASCADE" +
            ");";
    
    // SQL statements for dropping tables
    private static final String SQL_DELETE_USERS_TABLE =
            "DROP TABLE IF EXISTS " + CryptoDatabaseContract.UserEntry.TABLE_NAME;
    
    private static final String SQL_DELETE_FAVORITE_COINS_TABLE =
            "DROP TABLE IF EXISTS " + CryptoDatabaseContract.FavoriteCoinEntry.TABLE_NAME;
    
    private static final String SQL_DELETE_COIN_CACHE_TABLE =
            "DROP TABLE IF EXISTS " + CryptoDatabaseContract.CoinCacheEntry.TABLE_NAME;
    
    private static final String SQL_DELETE_SEARCH_HISTORY_TABLE =
            "DROP TABLE IF EXISTS " + CryptoDatabaseContract.SearchHistoryEntry.TABLE_NAME;
    
    private static final String SQL_DELETE_PORTFOLIO_TABLE =
            "DROP TABLE IF EXISTS " + CryptoDatabaseContract.PortfolioEntry.TABLE_NAME;

    private static final String SQL_DELETE_ALERTS_TABLE =
            "DROP TABLE IF EXISTS " + CryptoDatabaseContract.AlertEntry.TABLE_NAME;

    private static final String SQL_DELETE_COIN_PRICE_HISTORY_TABLE =
            "DROP TABLE IF EXISTS " + CryptoDatabaseContract.CoinPriceHistoryEntry.TABLE_NAME;

    private static final String SQL_DELETE_TRANSACTIONS_TABLE =
            "DROP TABLE IF EXISTS " + CryptoDatabaseContract.TransactionEntry.TABLE_NAME;
    
    public CryptoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create all tables
        db.execSQL(SQL_CREATE_USERS_TABLE);
        db.execSQL(SQL_CREATE_FAVORITE_COINS_TABLE);
        db.execSQL(SQL_CREATE_COIN_CACHE_TABLE);
        db.execSQL(SQL_CREATE_SEARCH_HISTORY_TABLE);
        db.execSQL(SQL_CREATE_PORTFOLIO_TABLE);
        db.execSQL(SQL_CREATE_TRANSACTIONS_TABLE);
        // Ensure tables introduced by migrations exist on fresh installs
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CryptoDatabaseContract.AlertEntry.TABLE_NAME + " (" +
                CryptoDatabaseContract.AlertEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CryptoDatabaseContract.AlertEntry.COLUMN_USER_ID + " INTEGER NOT NULL, " +
                CryptoDatabaseContract.AlertEntry.COLUMN_COIN_ID + " TEXT NOT NULL, " +
                CryptoDatabaseContract.AlertEntry.COLUMN_ALERT_TYPE + " TEXT NOT NULL, " +
                CryptoDatabaseContract.AlertEntry.COLUMN_TARGET_PRICE + " REAL NOT NULL, " +
                CryptoDatabaseContract.AlertEntry.COLUMN_IS_ACTIVE + " INTEGER DEFAULT 1, " +
                CryptoDatabaseContract.AlertEntry.COLUMN_CREATED_AT + " INTEGER NOT NULL, " +
                CryptoDatabaseContract.AlertEntry.COLUMN_TRIGGERED_AT + " INTEGER, " +
                "FOREIGN KEY (" + CryptoDatabaseContract.AlertEntry.COLUMN_USER_ID + ") REFERENCES " +
                CryptoDatabaseContract.UserEntry.TABLE_NAME + "(" + CryptoDatabaseContract.UserEntry._ID + ") ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + CryptoDatabaseContract.CoinPriceHistoryEntry.TABLE_NAME + " (" +
                CryptoDatabaseContract.CoinPriceHistoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CryptoDatabaseContract.CoinPriceHistoryEntry.COLUMN_COIN_ID + " TEXT NOT NULL, " +
                CryptoDatabaseContract.CoinPriceHistoryEntry.COLUMN_PRICE + " REAL NOT NULL, " +
                CryptoDatabaseContract.CoinPriceHistoryEntry.COLUMN_MARKET_CAP + " REAL, " +
                CryptoDatabaseContract.CoinPriceHistoryEntry.COLUMN_VOLUME_24H + " REAL, " +
                CryptoDatabaseContract.CoinPriceHistoryEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL)");
        
        // Enable foreign key constraints
        db.execSQL("PRAGMA foreign_keys=ON");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            DatabaseMigration.migrate(db, oldVersion, newVersion);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            android.util.Log.e("CryptoDBHelper", "Database migration failed", e);
        } finally {
            db.endTransaction();
        }
    }
    
    private void dropAllTables(SQLiteDatabase db) {
        // Drop child tables first to satisfy foreign key constraints
        db.execSQL(SQL_DELETE_FAVORITE_COINS_TABLE);
        db.execSQL(SQL_DELETE_PORTFOLIO_TABLE);
        db.execSQL(SQL_DELETE_TRANSACTIONS_TABLE);
        db.execSQL(SQL_DELETE_ALERTS_TABLE);
        db.execSQL(SQL_DELETE_SEARCH_HISTORY_TABLE);
        db.execSQL(SQL_DELETE_COIN_PRICE_HISTORY_TABLE);
        db.execSQL(SQL_DELETE_USERS_TABLE);
        db.execSQL(SQL_DELETE_COIN_CACHE_TABLE);
    }
    
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Enable foreign key constraints
        db.setForeignKeyConstraintsEnabled(true);
    }
    
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Ensure foreign keys are enabled when database is opened
        db.execSQL("PRAGMA foreign_keys=ON");
    }
}