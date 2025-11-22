package com.example.cryptoapplication.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cryptoapplication.database.CryptoDatabaseContract;
import com.example.cryptoapplication.database.CryptoDatabaseHelper;
import com.example.cryptoapplication.models.CoinModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of CoinDao interface.
 * Handles all coin-related database operations.
 */
public class CoinDaoImpl implements CoinDao {
    
    private final CryptoDatabaseHelper dbHelper;
    
    public CoinDaoImpl(Context context) {
        this.dbHelper = new CryptoDatabaseHelper(context);
    }
    
    @Override
    public long insert(CoinModel coin) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_COIN_ID, coin.getId());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_SYMBOL, coin.getSymbol());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_NAME, coin.getName());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_IMAGE, coin.getImage());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_CURRENT_PRICE, coin.getCurrentPrice());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP, coin.getMarketCap());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP_RANK, coin.getMarketCapRank());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_FULLY_DILUTED_VALUATION, coin.getFullyDilutedValuation());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_TOTAL_VOLUME, coin.getTotalVolume());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_HIGH_24H, coin.getHigh24h());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_LOW_24H, coin.getLow24h());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_24H, coin.getPriceChange24h());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_PERCENTAGE_24H, coin.getPriceChangePercentage24h());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP_CHANGE_24H, coin.getMarketCapChange24h());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP_CHANGE_PERCENTAGE_24H, coin.getMarketCapChangePercentage24h());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_CIRCULATING_SUPPLY, coin.getCirculatingSupply());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_TOTAL_SUPPLY, coin.getTotalSupply());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_MAX_SUPPLY, coin.getMaxSupply());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_LAST_UPDATED, coin.getLastUpdated());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_CACHED_AT, System.currentTimeMillis());
        
        return db.insert(CryptoDatabaseContract.CoinCache.TABLE_NAME, null, values);
    }

    @Override
    public int update(CoinModel coin) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_SYMBOL, coin.getSymbol());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_NAME, coin.getName());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_IMAGE, coin.getImage());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_CURRENT_PRICE, coin.getCurrentPrice());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP, coin.getMarketCap());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP_RANK, coin.getMarketCapRank());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_FULLY_DILUTED_VALUATION, coin.getFullyDilutedValuation());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_TOTAL_VOLUME, coin.getTotalVolume());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_HIGH_24H, coin.getHigh24h());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_LOW_24H, coin.getLow24h());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_24H, coin.getPriceChange24h());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_PERCENTAGE_24H, coin.getPriceChangePercentage24h());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP_CHANGE_24H, coin.getMarketCapChange24h());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP_CHANGE_PERCENTAGE_24H, coin.getMarketCapChangePercentage24h());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_CIRCULATING_SUPPLY, coin.getCirculatingSupply());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_TOTAL_SUPPLY, coin.getTotalSupply());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_MAX_SUPPLY, coin.getMaxSupply());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_LAST_UPDATED, coin.getLastUpdated());
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_CACHED_AT, System.currentTimeMillis());
        
        String selection = CryptoDatabaseContract.CoinCache.COLUMN_COIN_ID + " = ?";
        String[] selectionArgs = {coin.getId()};
        
        return db.update(CryptoDatabaseContract.CoinCache.TABLE_NAME, values, selection, selectionArgs);
    }
    
    @Override
    public int delete(CoinModel coin) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        String selection = CryptoDatabaseContract.CoinCache.COLUMN_COIN_ID + " = ?";
        String[] selectionArgs = {coin.getId()};
        
        return db.delete(CryptoDatabaseContract.CoinCache.TABLE_NAME, selection, selectionArgs);
    }
    
    @Override
    public List<CoinModel> getAll() {
        List<CoinModel> coins = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String[] projection = getCoinProjection();
        
        Cursor cursor = db.query(
            CryptoDatabaseContract.CoinCache.TABLE_NAME,
            projection,
            null, null, null, null,
            CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP_RANK + " ASC"
        );
        
        while (cursor.moveToNext()) {
            coins.add(cursorToCoin(cursor));
        }
        cursor.close();
        
        return coins;
    }
    
    @Override
    public CoinModel getById(long id) {
        // Since we're using coin_id as the primary identifier, not the SQLite _id
        return null; // Not used in this implementation
    }
    
    @Override
    public int count() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String countQuery = "SELECT COUNT(*) FROM " + CryptoDatabaseContract.CoinCache.TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);
        
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        
        return count;
    }
    
    @Override
    public boolean exists(long id) {
        // Since we're using coin_id as the primary identifier, not the SQLite _id
        return false; // Not used in this implementation
    }
    
    @Override
    public int deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(CryptoDatabaseContract.CoinCache.TABLE_NAME, null, null);
    }
    
    @Override
    public CoinModel findByCoinId(String coinId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String[] projection = getCoinProjection();
        String selection = CryptoDatabaseContract.CoinCache.COLUMN_COIN_ID + " = ?";
        String[] selectionArgs = {coinId};
        
        Cursor cursor = db.query(
            CryptoDatabaseContract.CoinCache.TABLE_NAME,
            projection,
            selection, selectionArgs, null, null, null
        );
        
        CoinModel coin = null;
        if (cursor.moveToFirst()) {
            coin = cursorToCoin(cursor);
        }
        cursor.close();
        
        return coin;
    }
    
    @Override
    public List<CoinModel> findBySymbol(String symbol) {
        List<CoinModel> coins = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String[] projection = getCoinProjection();
        String selection = CryptoDatabaseContract.CoinCache.COLUMN_SYMBOL + " = ?";
        String[] selectionArgs = {symbol.toUpperCase()};
        
        Cursor cursor = db.query(
            CryptoDatabaseContract.CoinCache.TABLE_NAME,
            projection,
            selection, selectionArgs, null, null, null
        );
        
        while (cursor.moveToNext()) {
            coins.add(cursorToCoin(cursor));
        }
        cursor.close();
        
        return coins;
    }
    
    @Override
    public List<CoinModel> searchByName(String query) {
        List<CoinModel> coins = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String[] projection = getCoinProjection();
        String selection = CryptoDatabaseContract.CoinCache.COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = {"%" + query + "%"};
        
        Cursor cursor = db.query(
            CryptoDatabaseContract.CoinCache.TABLE_NAME,
            projection,
            selection, selectionArgs, null, null,
            CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP + " DESC"
        );
        
        while (cursor.moveToNext()) {
            coins.add(cursorToCoin(cursor));
        }
        cursor.close();
        
        return coins;
    }
    
    @Override
    public List<CoinModel> getTopByMarketCap(int limit) {
        List<CoinModel> coins = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String[] projection = getCoinProjection();
        
        Cursor cursor = db.query(
            CryptoDatabaseContract.CoinCache.TABLE_NAME,
            projection,
            null, null, null, null,
            CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP + " DESC",
            String.valueOf(limit)
        );
        
        while (cursor.moveToNext()) {
            coins.add(cursorToCoin(cursor));
        }
        cursor.close();
        
        return coins;
    }
    
    @Override
    public List<CoinModel> getTopGainers(int limit) {
        List<CoinModel> coins = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String[] projection = getCoinProjection();
        String selection = CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_PERCENTAGE_24H + " > 0";
        
        Cursor cursor = db.query(
            CryptoDatabaseContract.CoinCache.TABLE_NAME,
            projection,
            selection, null, null, null,
            CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_PERCENTAGE_24H + " DESC",
            String.valueOf(limit)
        );
        
        while (cursor.moveToNext()) {
            coins.add(cursorToCoin(cursor));
        }
        cursor.close();
        
        return coins;
    }
    
    @Override
    public List<CoinModel> getTopLosers(int limit) {
        List<CoinModel> coins = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String[] projection = getCoinProjection();
        String selection = CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_PERCENTAGE_24H + " < 0";
        
        Cursor cursor = db.query(
            CryptoDatabaseContract.CoinCache.TABLE_NAME,
            projection,
            selection, null, null, null,
            CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_PERCENTAGE_24H + " ASC",
            String.valueOf(limit)
        );
        
        while (cursor.moveToNext()) {
            coins.add(cursorToCoin(cursor));
        }
        cursor.close();
        
        return coins;
    }
    
    @Override
    public List<CoinModel> getFavoritesByUser(long userId) {
        List<CoinModel> coins = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String query = "SELECT c.* FROM " + CryptoDatabaseContract.CoinCache.TABLE_NAME + " c " +
                      "INNER JOIN " + CryptoDatabaseContract.FavoriteCoins.TABLE_NAME + " f " +
                      "ON c." + CryptoDatabaseContract.CoinCache.COLUMN_COIN_ID + " = f." + 
                      CryptoDatabaseContract.FavoriteCoins.COLUMN_COIN_ID + " " +
                      "WHERE f." + CryptoDatabaseContract.FavoriteCoins.COLUMN_USER_ID + " = ? " +
                      "ORDER BY f." + CryptoDatabaseContract.FavoriteCoins.COLUMN_CREATED_AT + " DESC";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        
        while (cursor.moveToNext()) {
            coins.add(cursorToCoin(cursor));
        }
        cursor.close();
        
        return coins;
    }
    
    @Override
    public boolean addToFavorites(long userId, String coinId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(CryptoDatabaseContract.FavoriteCoins.COLUMN_USER_ID, userId);
        values.put(CryptoDatabaseContract.FavoriteCoins.COLUMN_COIN_ID, coinId);
        values.put(CryptoDatabaseContract.FavoriteCoins.COLUMN_CREATED_AT, System.currentTimeMillis());
        
        long result = db.insert(CryptoDatabaseContract.FavoriteCoins.TABLE_NAME, null, values);
        return result != -1;
    }
    
    @Override
    public boolean removeFromFavorites(long userId, String coinId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        String selection = CryptoDatabaseContract.FavoriteCoins.COLUMN_USER_ID + " = ? AND " +
                          CryptoDatabaseContract.FavoriteCoins.COLUMN_COIN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId), coinId};
        
        int result = db.delete(CryptoDatabaseContract.FavoriteCoins.TABLE_NAME, selection, selectionArgs);
        return result > 0;
    }
    
    @Override
    public boolean isFavorite(long userId, String coinId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String[] projection = {CryptoDatabaseContract.FavoriteCoins._ID};
        String selection = CryptoDatabaseContract.FavoriteCoins.COLUMN_USER_ID + " = ? AND " +
                          CryptoDatabaseContract.FavoriteCoins.COLUMN_COIN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId), coinId};
        
        Cursor cursor = db.query(
            CryptoDatabaseContract.FavoriteCoins.TABLE_NAME,
            projection,
            selection, selectionArgs, null, null, null
        );
        
        boolean exists = cursor.moveToFirst();
        cursor.close();
        
        return exists;
    }
    
    @Override
    public int updatePriceData(String coinId, double currentPrice, double priceChange24h, 
                              double priceChangePercentage24h, long marketCap, long lastUpdated) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_CURRENT_PRICE, currentPrice);
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_24H, priceChange24h);
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_PERCENTAGE_24H, priceChangePercentage24h);
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP, marketCap);
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_LAST_UPDATED, lastUpdated);
        values.put(CryptoDatabaseContract.CoinCache.COLUMN_CACHED_AT, System.currentTimeMillis());
        
        String selection = CryptoDatabaseContract.CoinCache.COLUMN_COIN_ID + " = ?";
        String[] selectionArgs = {coinId};
        
        return db.update(CryptoDatabaseContract.CoinCache.TABLE_NAME, values, selection, selectionArgs);
    }
    
    @Override
    public List<CoinModel> getCoinsNeedingUpdate(long maxAgeMillis) {
        List<CoinModel> coins = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        long cutoffTime = System.currentTimeMillis() - maxAgeMillis;
        String[] projection = getCoinProjection();
        String selection = CryptoDatabaseContract.CoinCache.COLUMN_CACHED_AT + " < ?";
        String[] selectionArgs = {String.valueOf(cutoffTime)};
        
        Cursor cursor = db.query(
            CryptoDatabaseContract.CoinCache.TABLE_NAME,
            projection,
            selection, selectionArgs, null, null,
            CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP + " DESC"
        );
        
        while (cursor.moveToNext()) {
            coins.add(cursorToCoin(cursor));
        }
        cursor.close();
        
        return coins;
    }
    
    @Override
    public int deleteOldCoins(long maxAgeMillis) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        long cutoffTime = System.currentTimeMillis() - maxAgeMillis;
        String selection = CryptoDatabaseContract.CoinCache.COLUMN_CACHED_AT + " < ?";
        String[] selectionArgs = {String.valueOf(cutoffTime)};
        
        return db.delete(CryptoDatabaseContract.CoinCache.TABLE_NAME, selection, selectionArgs);
    }
    
    private String[] getCoinProjection() {
        return new String[]{
            CryptoDatabaseContract.CoinCache.COLUMN_COIN_ID,
            CryptoDatabaseContract.CoinCache.COLUMN_SYMBOL,
            CryptoDatabaseContract.CoinCache.COLUMN_NAME,
            CryptoDatabaseContract.CoinCache.COLUMN_IMAGE,
            CryptoDatabaseContract.CoinCache.COLUMN_CURRENT_PRICE,
            CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP,
            CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP_RANK,
            CryptoDatabaseContract.CoinCache.COLUMN_FULLY_DILUTED_VALUATION,
            CryptoDatabaseContract.CoinCache.COLUMN_TOTAL_VOLUME,
            CryptoDatabaseContract.CoinCache.COLUMN_HIGH_24H,
            CryptoDatabaseContract.CoinCache.COLUMN_LOW_24H,
            CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_24H,
            CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_PERCENTAGE_24H,
            CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP_CHANGE_24H,
            CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP_CHANGE_PERCENTAGE_24H,
            CryptoDatabaseContract.CoinCache.COLUMN_CIRCULATING_SUPPLY,
            CryptoDatabaseContract.CoinCache.COLUMN_TOTAL_SUPPLY,
            CryptoDatabaseContract.CoinCache.COLUMN_MAX_SUPPLY,
            CryptoDatabaseContract.CoinCache.COLUMN_LAST_UPDATED,
            CryptoDatabaseContract.CoinCache.COLUMN_CACHED_AT
        };
    }
    
    private CoinModel cursorToCoin(Cursor cursor) {
        CoinModel coin = new CoinModel();
        coin.setId(cursor.getString(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_COIN_ID)));
        coin.setSymbol(cursor.getString(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_SYMBOL)));
        coin.setName(cursor.getString(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_NAME)));
        coin.setImage(cursor.getString(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_IMAGE)));
        coin.setCurrentPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_CURRENT_PRICE)));
        coin.setMarketCap(cursor.getLong(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP)));
        coin.setMarketCapRank(cursor.getInt(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP_RANK)));
        coin.setFullyDilutedValuation(cursor.getLong(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_FULLY_DILUTED_VALUATION)));
        coin.setTotalVolume(cursor.getLong(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_TOTAL_VOLUME)));
        coin.setHigh24h(cursor.getDouble(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_HIGH_24H)));
        coin.setLow24h(cursor.getDouble(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_LOW_24H)));
        coin.setPriceChange24h(cursor.getDouble(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_24H)));
        coin.setPriceChangePercentage24h(cursor.getDouble(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_PERCENTAGE_24H)));
        coin.setMarketCapChange24h(cursor.getLong(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP_CHANGE_24H)));
        coin.setMarketCapChangePercentage24h(cursor.getDouble(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP_CHANGE_PERCENTAGE_24H)));
        coin.setCirculatingSupply(cursor.getDouble(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_CIRCULATING_SUPPLY)));
        coin.setTotalSupply(cursor.getDouble(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_TOTAL_SUPPLY)));
        coin.setMaxSupply(cursor.getDouble(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_MAX_SUPPLY)));
        coin.setLastUpdated(cursor.getLong(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_LAST_UPDATED)));
        return coin;
    }
}