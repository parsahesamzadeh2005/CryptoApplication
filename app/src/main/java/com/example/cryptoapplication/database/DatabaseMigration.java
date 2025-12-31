package com.example.cryptoapplication.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Database migration utility for handling schema changes
 * Provides a structured way to migrate between different database versions
 */
public class DatabaseMigration {
    
    private static final String TAG = "DatabaseMigration";
    
    /**
     * Represents a single migration step
     */
    public interface Migration {
        /**
         * Apply this migration step
         */
        void migrate(SQLiteDatabase db);
        
        /**
         * Get the target version for this migration
         */
        int getTargetVersion();
    }
    
    /**
     * Migration from version 1 to 2: Add user profile columns
     */
    public static class Migration1To2 implements Migration {
        @Override
        public void migrate(SQLiteDatabase db) {
            Log.i(TAG, "Migrating database from version 1 to 2");
            
            // Add new columns to users table
            db.execSQL("ALTER TABLE " + CryptoDatabaseContract.UserEntry.TABLE_NAME + 
                      " ADD COLUMN " + CryptoDatabaseContract.UserEntry.COLUMN_PROFILE_IMAGE + " TEXT");
            
            db.execSQL("ALTER TABLE " + CryptoDatabaseContract.UserEntry.TABLE_NAME + 
                      " ADD COLUMN " + CryptoDatabaseContract.UserEntry.COLUMN_BIO + " TEXT");
            
            db.execSQL("ALTER TABLE " + CryptoDatabaseContract.UserEntry.TABLE_NAME + 
                      " ADD COLUMN " + CryptoDatabaseContract.UserEntry.COLUMN_PHONE + " TEXT");
        }
        
        @Override
        public int getTargetVersion() {
            return 2;
        }
    }
    
    // Removed unused Migration2To3 and Migration3To4 classes
    // These migrations created tables (coin_price_history, alerts) that have no corresponding DAO classes or services
    
    /**
     * Migration from version 2 to 5: Add transaction fees to portfolio
     * (Skipped versions 3-4 as they contained unused tables)
     */
    public static class Migration2To5 implements Migration {
        @Override
        public void migrate(SQLiteDatabase db) {
            Log.i(TAG, "Migrating database from version 4 to 5");
            
            // Add transaction fee column to portfolio
            db.execSQL("ALTER TABLE " + CryptoDatabaseContract.PortfolioEntry.TABLE_NAME + 
                      " ADD COLUMN " + CryptoDatabaseContract.PortfolioEntry.COLUMN_TRANSACTION_FEE + " REAL DEFAULT 0");
            
            // Add transaction type column
            db.execSQL("ALTER TABLE " + CryptoDatabaseContract.PortfolioEntry.TABLE_NAME + 
                      " ADD COLUMN " + CryptoDatabaseContract.PortfolioEntry.COLUMN_TRANSACTION_TYPE + " TEXT DEFAULT 'BUY'");
        }
        
        @Override
        public int getTargetVersion() {
            return 5;
        }
    }

    /**
     * Migration from version 5 to 6: Expand coin_cache and portfolio schemas
     */
    public static class Migration5To6 implements Migration {
        @Override
        public void migrate(SQLiteDatabase db) {
            Log.i(TAG, "Migrating database from version 5 to 6");

            // Add missing columns in coin_cache used by DAO
            addColumnIfMissing(db, CryptoDatabaseContract.CoinCacheEntry.TABLE_NAME, "image", "TEXT");
            addColumnIfMissing(db, CryptoDatabaseContract.CoinCacheEntry.TABLE_NAME, "market_cap_rank", "INTEGER");
            addColumnIfMissing(db, CryptoDatabaseContract.CoinCacheEntry.TABLE_NAME, "fully_diluted_valuation", "REAL");
            addColumnIfMissing(db, CryptoDatabaseContract.CoinCacheEntry.TABLE_NAME, "total_volume", "REAL");
            addColumnIfMissing(db, CryptoDatabaseContract.CoinCacheEntry.TABLE_NAME, "high_24h", "REAL");
            addColumnIfMissing(db, CryptoDatabaseContract.CoinCacheEntry.TABLE_NAME, "low_24h", "REAL");
            addColumnIfMissing(db, CryptoDatabaseContract.CoinCacheEntry.TABLE_NAME, "price_change_percentage_24h", "REAL");
            addColumnIfMissing(db, CryptoDatabaseContract.CoinCacheEntry.TABLE_NAME, "market_cap_change_24h", "REAL");
            addColumnIfMissing(db, CryptoDatabaseContract.CoinCacheEntry.TABLE_NAME, "market_cap_change_percentage_24h", "REAL");
            addColumnIfMissing(db, CryptoDatabaseContract.CoinCacheEntry.TABLE_NAME, "circulating_supply", "REAL");
            addColumnIfMissing(db, CryptoDatabaseContract.CoinCacheEntry.TABLE_NAME, "total_supply", "REAL");
            addColumnIfMissing(db, CryptoDatabaseContract.CoinCacheEntry.TABLE_NAME, "max_supply", "REAL");
            addColumnIfMissing(db, CryptoDatabaseContract.CoinCacheEntry.TABLE_NAME, "cached_at", "INTEGER");

            // Add missing portfolio columns used by DAO
            addColumnIfMissing(db, CryptoDatabaseContract.PortfolioEntry.TABLE_NAME, CryptoDatabaseContract.PortfolioEntry.COLUMN_QUANTITY, "REAL");
            addColumnIfMissing(db, CryptoDatabaseContract.PortfolioEntry.TABLE_NAME, CryptoDatabaseContract.PortfolioEntry.COLUMN_CURRENT_PRICE, "REAL");
            addColumnIfMissing(db, CryptoDatabaseContract.PortfolioEntry.TABLE_NAME, CryptoDatabaseContract.PortfolioEntry.COLUMN_TOTAL_VALUE, "REAL");
            addColumnIfMissing(db, CryptoDatabaseContract.PortfolioEntry.TABLE_NAME, CryptoDatabaseContract.PortfolioEntry.COLUMN_PROFIT_LOSS, "REAL DEFAULT 0");
            addColumnIfMissing(db, CryptoDatabaseContract.PortfolioEntry.TABLE_NAME, CryptoDatabaseContract.PortfolioEntry.COLUMN_TRANSACTION_FEE, "REAL DEFAULT 0");
            addColumnIfMissing(db, CryptoDatabaseContract.PortfolioEntry.TABLE_NAME, CryptoDatabaseContract.PortfolioEntry.COLUMN_TRANSACTION_TYPE, "TEXT DEFAULT 'BUY'");
            addColumnIfMissing(db, CryptoDatabaseContract.PortfolioEntry.TABLE_NAME, CryptoDatabaseContract.PortfolioEntry.COLUMN_CREATED_AT, "INTEGER");
            addColumnIfMissing(db, CryptoDatabaseContract.PortfolioEntry.TABLE_NAME, CryptoDatabaseContract.PortfolioEntry.COLUMN_UPDATED_AT, "INTEGER");
        }

        private void addColumnIfMissing(SQLiteDatabase db, String tableName, String columnName, String columnType) {
            try {
                if (!columnExists(db, tableName, columnName)) {
                    db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType);
                }
            } catch (Exception e) {
                Log.w(TAG, "Skipping add column " + columnName + " on table " + tableName + ": " + e.getMessage());
            }
        }

        private boolean columnExists(SQLiteDatabase db, String tableName, String columnName) {
            Cursor cursor = null;
            try {
                cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
                int nameIndex = cursor.getColumnIndex("name");
                while (cursor.moveToNext()) {
                    String existing = cursor.getString(nameIndex);
                    if (columnName.equalsIgnoreCase(existing)) {
                        return true;
                    }
                }
                return false;
            } finally {
                if (cursor != null) cursor.close();
            }
        }

        @Override
        public int getTargetVersion() {
            return 6;
        }
    }

    /**
     * Migration from version 6 to 7: Add user balance column
     */
    public static class Migration6To7 implements Migration {
        @Override
        public void migrate(SQLiteDatabase db) {
            Log.i(TAG, "Migrating database from version 6 to 7");
            try {
                db.execSQL("ALTER TABLE " + CryptoDatabaseContract.UserEntry.TABLE_NAME +
                        " ADD COLUMN " + CryptoDatabaseContract.UserEntry.COLUMN_BALANCE + " REAL DEFAULT 0");
            } catch (Exception e) {
                Log.w(TAG, "Balance column migration skipped: " + e.getMessage());
            }
        }

        @Override
        public int getTargetVersion() {
            return 7;
        }
    }
    
    /**
     * Get all available migrations
     */
    public static List<Migration> getAllMigrations() {
        List<Migration> migrations = new ArrayList<>();
        migrations.add(new Migration1To2());
        migrations.add(new Migration2To5()); // Renamed from Migration4To5
        migrations.add(new Migration5To6());
        migrations.add(new Migration6To7());
        return migrations;
    }
    
    /**
     * Apply migrations from oldVersion to newVersion
     */
    public static void migrate(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Migrating database from version " + oldVersion + " to " + newVersion);
        
        List<Migration> migrations = getAllMigrations();
        
        for (Migration migration : migrations) {
            if (migration.getTargetVersion() > oldVersion && migration.getTargetVersion() <= newVersion) {
                try {
                    migration.migrate(db);
                    Log.i(TAG, "Successfully applied migration to version " + migration.getTargetVersion());
                } catch (Exception e) {
                    Log.e(TAG, "Failed to apply migration to version " + migration.getTargetVersion(), e);
                    throw new RuntimeException("Database migration failed", e);
                }
            }
        }
        
        Log.i(TAG, "Database migration completed successfully");
    }
    
    /**
     * Check if migration is needed
     */
    public static boolean isMigrationNeeded(int oldVersion, int newVersion) {
        return oldVersion < newVersion;
    }
    
    /**
     * Get the latest database version
     */
    public static int getLatestVersion() {
        return 7; // Updated to reflect actual migrations
    }
}