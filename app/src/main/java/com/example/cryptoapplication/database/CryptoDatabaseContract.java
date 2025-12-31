package com.example.cryptoapplication.database;

import android.provider.BaseColumns;

/**
 * Contract class for the crypto database.
 * Defines the database schema including tables and columns.
 */
public final class CryptoDatabaseContract {
    
    // Private constructor to prevent instantiation
    private CryptoDatabaseContract() {}
    
    /**
     * User table definition
     */
    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD_HASH = "password_hash";
        public static final String COLUMN_BALANCE = "balance";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_LAST_LOGIN = "last_login";
        public static final String COLUMN_IS_ACTIVE = "is_active";
        public static final String COLUMN_PROFILE_IMAGE = "profile_image";
        public static final String COLUMN_BIO = "bio";
        public static final String COLUMN_PHONE = "phone";
    }
    
    /**
     * Favorite coins table definition
     */
    public static class FavoriteCoinEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorite_coins";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_COIN_ID = "coin_id";
        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_ADDED_AT = "added_at";
    }
    
    /**
     * Coin cache table definition (for offline storage)
     */
    public static class CoinCacheEntry implements BaseColumns {
        public static final String TABLE_NAME = "coin_cache";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_COIN_ID = "coin_id";
        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CURRENT_PRICE = "current_price";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_PRICE_CHANGE_24H = "price_change_24h";
        public static final String COLUMN_MARKET_CAP = "market_cap";
        public static final String COLUMN_VOLUME_24H = "volume_24h";
        public static final String COLUMN_LAST_UPDATED = "last_updated";
        public static final String COLUMN_CURRENCY = "currency";
    }
    
    // Removed unused SearchHistoryEntry, CoinPriceHistoryEntry, and AlertEntry classes
    // These tables have no corresponding DAO classes or services
    
    /**
     * Portfolio table - tracks user's cryptocurrency holdings
     */
    public static class PortfolioEntry implements BaseColumns {
        public static final String TABLE_NAME = "portfolio";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_COIN_ID = "coin_id";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_PURCHASE_PRICE = "purchase_price";
        public static final String COLUMN_PURCHASE_DATE = "purchase_date";
        public static final String COLUMN_CURRENT_PRICE = "current_price";
        public static final String COLUMN_TOTAL_VALUE = "total_value";
        public static final String COLUMN_PROFIT_LOSS = "profit_loss";
        public static final String COLUMN_TRANSACTION_FEE = "transaction_fee";
        public static final String COLUMN_TRANSACTION_TYPE = "transaction_type";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }
    
    // Removed unused CoinPriceHistoryEntry and AlertEntry classes

    /**
     * Transactions table - unified log for BUY and WITHDRAW actions
     */
    public static class TransactionEntry implements BaseColumns {
        public static final String TABLE_NAME = "transactions";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_TYPE = "type"; // BUY, WITHDRAW
        public static final String COLUMN_COIN_ID = "coin_id"; // nullable for withdraw
        public static final String COLUMN_QUANTITY = "quantity"; // nullable for withdraw
        public static final String COLUMN_PRICE_PER_COIN = "price_per_coin"; // nullable for withdraw
        public static final String COLUMN_FIAT_AMOUNT = "fiat_amount"; // fiat spent (BUY) or withdrawn (WITHDRAW)
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
    
    // Aliases used by DAOs without the *Entry suffix
    public static class CoinCache {
        public static final String TABLE_NAME = CoinCacheEntry.TABLE_NAME;
        public static final String COLUMN_COIN_ID = CoinCacheEntry.COLUMN_COIN_ID;
        public static final String COLUMN_SYMBOL = CoinCacheEntry.COLUMN_SYMBOL;
        public static final String COLUMN_NAME = CoinCacheEntry.COLUMN_NAME;
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_CURRENT_PRICE = CoinCacheEntry.COLUMN_CURRENT_PRICE;
        public static final String COLUMN_MARKET_CAP = CoinCacheEntry.COLUMN_MARKET_CAP;
        public static final String COLUMN_MARKET_CAP_RANK = "market_cap_rank";
        public static final String COLUMN_FULLY_DILUTED_VALUATION = "fully_diluted_valuation";
        public static final String COLUMN_TOTAL_VOLUME = "total_volume";
        public static final String COLUMN_HIGH_24H = "high_24h";
        public static final String COLUMN_LOW_24H = "low_24h";
        public static final String COLUMN_PRICE_CHANGE_24H = CoinCacheEntry.COLUMN_PRICE_CHANGE_24H;
        public static final String COLUMN_PRICE_CHANGE_PERCENTAGE_24H = "price_change_percentage_24h";
        public static final String COLUMN_MARKET_CAP_CHANGE_24H = "market_cap_change_24h";
        public static final String COLUMN_MARKET_CAP_CHANGE_PERCENTAGE_24H = "market_cap_change_percentage_24h";
        public static final String COLUMN_CIRCULATING_SUPPLY = "circulating_supply";
        public static final String COLUMN_TOTAL_SUPPLY = "total_supply";
        public static final String COLUMN_MAX_SUPPLY = "max_supply";
        public static final String COLUMN_LAST_UPDATED = CoinCacheEntry.COLUMN_LAST_UPDATED;
        public static final String COLUMN_CACHED_AT = "cached_at";
    }
    
    public static class FavoriteCoins implements BaseColumns {
        public static final String TABLE_NAME = FavoriteCoinEntry.TABLE_NAME;
        public static final String COLUMN_USER_ID = FavoriteCoinEntry.COLUMN_USER_ID;
        public static final String COLUMN_COIN_ID = FavoriteCoinEntry.COLUMN_COIN_ID;
        public static final String COLUMN_CREATED_AT = "created_at";
    }

    /**
     * Alias for Users to match DAO references
     */
    public static class Users implements BaseColumns {
        public static final String TABLE_NAME = UserEntry.TABLE_NAME;
        public static final String COLUMN_USERNAME = UserEntry.COLUMN_USERNAME;
        public static final String COLUMN_EMAIL = UserEntry.COLUMN_EMAIL;
        // Map DAO's COLUMN_PASSWORD to actual schema column name
        public static final String COLUMN_PASSWORD = UserEntry.COLUMN_PASSWORD_HASH;
        public static final String COLUMN_BALANCE = UserEntry.COLUMN_BALANCE;
        public static final String COLUMN_PROFILE_IMAGE = UserEntry.COLUMN_PROFILE_IMAGE;
        public static final String COLUMN_CREATED_AT = UserEntry.COLUMN_CREATED_AT;
        public static final String COLUMN_LAST_LOGIN = UserEntry.COLUMN_LAST_LOGIN;
        public static final String COLUMN_IS_ACTIVE = UserEntry.COLUMN_IS_ACTIVE;
    }
}