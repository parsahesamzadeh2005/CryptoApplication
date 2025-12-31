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

public class SimpleCoinDao {
    
    private final CryptoDatabaseHelper databaseHelper;
    
    public SimpleCoinDao(Context context) {
        this.databaseHelper = new CryptoDatabaseHelper(context);
    }
    
    public long saveCoin(CoinModel coin) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        
        ContentValues coinData = new ContentValues();
        coinData.put(CryptoDatabaseContract.CoinCache.COLUMN_COIN_ID, coin.getId());
        coinData.put(CryptoDatabaseContract.CoinCache.COLUMN_SYMBOL, coin.getSymbol());
        coinData.put(CryptoDatabaseContract.CoinCache.COLUMN_NAME, coin.getName());
        coinData.put(CryptoDatabaseContract.CoinCache.COLUMN_IMAGE, coin.getImage());
        coinData.put(CryptoDatabaseContract.CoinCache.COLUMN_CURRENT_PRICE, coin.getCurrentPrice());
        coinData.put(CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP, coin.getMarketCap());
        coinData.put(CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP_RANK, coin.getMarketCapRank());
        coinData.put(CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_PERCENTAGE_24H, coin.getPriceChangePercentage24h());
        coinData.put(CryptoDatabaseContract.CoinCache.COLUMN_CACHED_AT, System.currentTimeMillis());
        
        return database.insert(CryptoDatabaseContract.CoinCache.TABLE_NAME, null, coinData);
    }
    
    public int updateCoin(CoinModel coin) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        
        ContentValues updatedData = new ContentValues();
        updatedData.put(CryptoDatabaseContract.CoinCache.COLUMN_SYMBOL, coin.getSymbol());
        updatedData.put(CryptoDatabaseContract.CoinCache.COLUMN_NAME, coin.getName());
        updatedData.put(CryptoDatabaseContract.CoinCache.COLUMN_IMAGE, coin.getImage());
        updatedData.put(CryptoDatabaseContract.CoinCache.COLUMN_CURRENT_PRICE, coin.getCurrentPrice());
        updatedData.put(CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP, coin.getMarketCap());
        updatedData.put(CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_PERCENTAGE_24H, coin.getPriceChangePercentage24h());
        updatedData.put(CryptoDatabaseContract.CoinCache.COLUMN_CACHED_AT, System.currentTimeMillis());
        
        String whereClause = CryptoDatabaseContract.CoinCache.COLUMN_COIN_ID + " = ?";
        String[] whereArgs = {coin.getId()};
        
        return database.update(CryptoDatabaseContract.CoinCache.TABLE_NAME, updatedData, whereClause, whereArgs);
    }
    
    public int deleteCoin(CoinModel coin) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        
        String whereClause = CryptoDatabaseContract.CoinCache.COLUMN_COIN_ID + " = ?";
        String[] whereArgs = {coin.getId()};
        
        return database.delete(CryptoDatabaseContract.CoinCache.TABLE_NAME, whereClause, whereArgs);
    }
    
    public List<CoinModel> getAllCoins() {
        List<CoinModel> coinList = new ArrayList<>();
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        
        String[] columnsToGet = getCoinColumns();
        
        Cursor cursor = database.query(
            CryptoDatabaseContract.CoinCache.TABLE_NAME,
            columnsToGet,
            null, null, null, null,
            CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP_RANK + " ASC"
        );
        
        while (cursor.moveToNext()) {
            CoinModel coin = createCoinFromCursor(cursor);
            coinList.add(coin);
        }
        
        cursor.close();
        return coinList;
    }
    
    public CoinModel findCoinById(String coinId) {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        
        String[] columnsToGet = getCoinColumns();
        String whereClause = CryptoDatabaseContract.CoinCache.COLUMN_COIN_ID + " = ?";
        String[] whereArgs = {coinId};
        
        Cursor cursor = database.query(
            CryptoDatabaseContract.CoinCache.TABLE_NAME,
            columnsToGet,
            whereClause, whereArgs, null, null, null
        );
        
        CoinModel foundCoin = null;
        if (cursor.moveToFirst()) {
            foundCoin = createCoinFromCursor(cursor);
        }
        
        cursor.close();
        return foundCoin;
    }
    
    public List<CoinModel> searchCoinsByName(String searchText) {
        List<CoinModel> matchingCoins = new ArrayList<>();
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        
        String[] columnsToGet = getCoinColumns();
        String whereClause = CryptoDatabaseContract.CoinCache.COLUMN_NAME + " LIKE ?";
        String[] whereArgs = {"%" + searchText + "%"};
        
        Cursor cursor = database.query(
            CryptoDatabaseContract.CoinCache.TABLE_NAME,
            columnsToGet,
            whereClause, whereArgs, null, null,
            CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP + " DESC"
        );
        
        while (cursor.moveToNext()) {
            CoinModel coin = createCoinFromCursor(cursor);
            matchingCoins.add(coin);
        }
        
        cursor.close();
        return matchingCoins;
    }
    
    public List<CoinModel> getTopValueCoins(int howMany) {
        List<CoinModel> topCoins = new ArrayList<>();
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        
        String[] columnsToGet = getCoinColumns();
        
        Cursor cursor = database.query(
            CryptoDatabaseContract.CoinCache.TABLE_NAME,
            columnsToGet,
            null, null, null, null,
            CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP + " DESC",
            String.valueOf(howMany)
        );
        
        while (cursor.moveToNext()) {
            CoinModel coin = createCoinFromCursor(cursor);
            topCoins.add(coin);
        }
        
        cursor.close();
        return topCoins;
    }
    
    public List<CoinModel> getWinningCoins(int howMany) {
        List<CoinModel> winners = new ArrayList<>();
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        
        String[] columnsToGet = getCoinColumns();
        String whereClause = CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_PERCENTAGE_24H + " > 0";
        
        Cursor cursor = database.query(
            CryptoDatabaseContract.CoinCache.TABLE_NAME,
            columnsToGet,
            whereClause, null, null, null,
            CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_PERCENTAGE_24H + " DESC",
            String.valueOf(howMany)
        );
        
        while (cursor.moveToNext()) {
            CoinModel coin = createCoinFromCursor(cursor);
            winners.add(coin);
        }
        
        cursor.close();
        return winners;
    }
    
    public List<CoinModel> getLosingCoins(int howMany) {
        List<CoinModel> losers = new ArrayList<>();
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        
        String[] columnsToGet = getCoinColumns();
        String whereClause = CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_PERCENTAGE_24H + " < 0";
        
        Cursor cursor = database.query(
            CryptoDatabaseContract.CoinCache.TABLE_NAME,
            columnsToGet,
            whereClause, null, null, null,
            CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_PERCENTAGE_24H + " ASC",
            String.valueOf(howMany)
        );
        
        while (cursor.moveToNext()) {
            CoinModel coin = createCoinFromCursor(cursor);
            losers.add(coin);
        }
        
        cursor.close();
        return losers;
    }
    
    public boolean addToFavorites(long userId, String coinId) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        
        ContentValues favoriteData = new ContentValues();
        favoriteData.put(CryptoDatabaseContract.FavoriteCoins.COLUMN_USER_ID, userId);
        favoriteData.put(CryptoDatabaseContract.FavoriteCoins.COLUMN_COIN_ID, coinId);
        favoriteData.put(CryptoDatabaseContract.FavoriteCoins.COLUMN_CREATED_AT, System.currentTimeMillis());
        
        long result = database.insert(CryptoDatabaseContract.FavoriteCoins.TABLE_NAME, null, favoriteData);
        return result != -1;
    }
    
    public boolean removeFromFavorites(long userId, String coinId) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        
        String whereClause = CryptoDatabaseContract.FavoriteCoins.COLUMN_USER_ID + " = ? AND " +
                           CryptoDatabaseContract.FavoriteCoins.COLUMN_COIN_ID + " = ?";
        String[] whereArgs = {String.valueOf(userId), coinId};
        
        int deletedRows = database.delete(CryptoDatabaseContract.FavoriteCoins.TABLE_NAME, whereClause, whereArgs);
        return deletedRows > 0;
    }
    
    public boolean isCoinFavorite(long userId, String coinId) {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        
        String[] columnsToGet = {CryptoDatabaseContract.FavoriteCoins._ID};
        String whereClause = CryptoDatabaseContract.FavoriteCoins.COLUMN_USER_ID + " = ? AND " +
                           CryptoDatabaseContract.FavoriteCoins.COLUMN_COIN_ID + " = ?";
        String[] whereArgs = {String.valueOf(userId), coinId};
        
        Cursor cursor = database.query(
            CryptoDatabaseContract.FavoriteCoins.TABLE_NAME,
            columnsToGet,
            whereClause, whereArgs, null, null, null
        );
        
        boolean isFavorite = cursor.moveToFirst();
        cursor.close();
        
        return isFavorite;
    }
    
    public List<CoinModel> getUserFavoriteCoins(long userId) {
        List<CoinModel> favoriteCoins = new ArrayList<>();
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        
        String query = "SELECT c.* FROM " + CryptoDatabaseContract.CoinCache.TABLE_NAME + " c " +
                      "INNER JOIN " + CryptoDatabaseContract.FavoriteCoins.TABLE_NAME + " f " +
                      "ON c." + CryptoDatabaseContract.CoinCache.COLUMN_COIN_ID + " = f." + 
                      CryptoDatabaseContract.FavoriteCoins.COLUMN_COIN_ID + " " +
                      "WHERE f." + CryptoDatabaseContract.FavoriteCoins.COLUMN_USER_ID + " = ? " +
                      "ORDER BY f." + CryptoDatabaseContract.FavoriteCoins.COLUMN_CREATED_AT + " DESC";
        
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(userId)});
        
        while (cursor.moveToNext()) {
            CoinModel coin = createCoinFromCursor(cursor);
            favoriteCoins.add(coin);
        }
        
        cursor.close();
        return favoriteCoins;
    }
    
    private String[] getCoinColumns() {
        return new String[]{
            CryptoDatabaseContract.CoinCache.COLUMN_COIN_ID,
            CryptoDatabaseContract.CoinCache.COLUMN_SYMBOL,
            CryptoDatabaseContract.CoinCache.COLUMN_NAME,
            CryptoDatabaseContract.CoinCache.COLUMN_IMAGE,
            CryptoDatabaseContract.CoinCache.COLUMN_CURRENT_PRICE,
            CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP,
            CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP_RANK,
            CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_PERCENTAGE_24H,
            CryptoDatabaseContract.CoinCache.COLUMN_CACHED_AT
        };
    }
    
    private CoinModel createCoinFromCursor(Cursor cursor) {
        CoinModel coin = new CoinModel();
        
        coin.setId(cursor.getString(
            cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_COIN_ID)));
        
        coin.setSymbol(cursor.getString(
            cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_SYMBOL)));
        
        coin.setName(cursor.getString(
            cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_NAME)));
        
        coin.setImage(cursor.getString(
            cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_IMAGE)));
        
        coin.setCurrentPrice(cursor.getDouble(
            cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_CURRENT_PRICE)));
        
        coin.setMarketCap(cursor.getLong(
            cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP)));
        
        coin.setMarketCapRank(cursor.getInt(
            cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_MARKET_CAP_RANK)));
        
        coin.setPriceChangePercentage24h(cursor.getDouble(
            cursor.getColumnIndexOrThrow(CryptoDatabaseContract.CoinCache.COLUMN_PRICE_CHANGE_PERCENTAGE_24H)));
        
        return coin;
    }
}