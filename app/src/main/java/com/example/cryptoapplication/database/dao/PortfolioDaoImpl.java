package com.example.cryptoapplication.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cryptoapplication.database.CryptoDatabaseContract;
import com.example.cryptoapplication.database.CryptoDatabaseHelper;
import com.example.cryptoapplication.models.PortfolioItem;

import java.util.ArrayList;
import java.util.List;

public class PortfolioDaoImpl implements PortfolioDao {

    private final CryptoDatabaseHelper dbHelper;

    public PortfolioDaoImpl(Context context) {
        this.dbHelper = new CryptoDatabaseHelper(context);
    }

    @Override
    public long insert(PortfolioItem entity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_USER_ID, entity.getUserId());
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_COIN_ID, entity.getCoinId());
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_QUANTITY, entity.getQuantity());
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_PURCHASE_PRICE, entity.getPurchasePrice());
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_PURCHASE_DATE, entity.getPurchaseDate());
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_CURRENT_PRICE, entity.getCurrentPrice());
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_TOTAL_VALUE, entity.getTotalValue());
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_PROFIT_LOSS, entity.getProfitLoss());
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_TRANSACTION_FEE, entity.getTransactionFee());
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_TRANSACTION_TYPE, entity.getTransactionType());
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_CREATED_AT, entity.getCreatedAt());
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_UPDATED_AT, entity.getUpdatedAt());
        return db.insert(CryptoDatabaseContract.PortfolioEntry.TABLE_NAME, null, v);
    }

    @Override
    public int update(PortfolioItem entity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_QUANTITY, entity.getQuantity());
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_PURCHASE_PRICE, entity.getPurchasePrice());
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_PURCHASE_DATE, entity.getPurchaseDate());
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_CURRENT_PRICE, entity.getCurrentPrice());
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_TOTAL_VALUE, entity.getTotalValue());
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_PROFIT_LOSS, entity.getProfitLoss());
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_TRANSACTION_FEE, entity.getTransactionFee());
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_TRANSACTION_TYPE, entity.getTransactionType());
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_UPDATED_AT, entity.getUpdatedAt());
        String where = CryptoDatabaseContract.PortfolioEntry._ID + " = ?";
        String[] args = { String.valueOf(entity.getId()) };
        return db.update(CryptoDatabaseContract.PortfolioEntry.TABLE_NAME, v, where, args);
    }

    @Override
    public int delete(PortfolioItem entity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String where = CryptoDatabaseContract.PortfolioEntry._ID + " = ?";
        String[] args = { String.valueOf(entity.getId()) };
        return db.delete(CryptoDatabaseContract.PortfolioEntry.TABLE_NAME, where, args);
    }

    @Override
    public List<PortfolioItem> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<PortfolioItem> list = new ArrayList<>();
        String[] proj = projection();
        Cursor c = db.query(CryptoDatabaseContract.PortfolioEntry.TABLE_NAME, proj,
                null, null, null, null,
                CryptoDatabaseContract.PortfolioEntry.COLUMN_UPDATED_AT + " DESC");
        while (c.moveToNext()) list.add(cursorToItem(c));
        c.close();
        return list;
    }

    @Override
    public PortfolioItem getById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] proj = projection();
        String where = CryptoDatabaseContract.PortfolioEntry._ID + " = ?";
        Cursor c = db.query(CryptoDatabaseContract.PortfolioEntry.TABLE_NAME, proj, where,
                new String[]{ String.valueOf(id) }, null, null, null);
        PortfolioItem item = null;
        if (c.moveToFirst()) item = cursorToItem(c);
        c.close();
        return item;
    }

    @Override
    public int count() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + CryptoDatabaseContract.PortfolioEntry.TABLE_NAME, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    @Override
    public boolean exists(long id) { return getById(id) != null; }

    @Override
    public int deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(CryptoDatabaseContract.PortfolioEntry.TABLE_NAME, null, null);
    }

    @Override
    public List<PortfolioItem> getByUserId(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<PortfolioItem> list = new ArrayList<>();
        Cursor c = db.query(CryptoDatabaseContract.PortfolioEntry.TABLE_NAME, projection(),
                CryptoDatabaseContract.PortfolioEntry.COLUMN_USER_ID + " = ?",
                new String[]{ String.valueOf(userId) }, null, null,
                CryptoDatabaseContract.PortfolioEntry.COLUMN_UPDATED_AT + " DESC");
        while (c.moveToNext()) list.add(cursorToItem(c));
        c.close();
        return list;
    }

    @Override
    public List<PortfolioItem> getByCoinId(String coinId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<PortfolioItem> list = new ArrayList<>();
        Cursor c = db.query(CryptoDatabaseContract.PortfolioEntry.TABLE_NAME, projection(),
                CryptoDatabaseContract.PortfolioEntry.COLUMN_COIN_ID + " = ?",
                new String[]{ coinId }, null, null,
                CryptoDatabaseContract.PortfolioEntry.COLUMN_UPDATED_AT + " DESC");
        while (c.moveToNext()) list.add(cursorToItem(c));
        c.close();
        return list;
    }

    @Override
    public List<PortfolioItem> getByUserAndCoin(long userId, String coinId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<PortfolioItem> list = new ArrayList<>();
        String where = CryptoDatabaseContract.PortfolioEntry.COLUMN_USER_ID + " = ? AND " +
                CryptoDatabaseContract.PortfolioEntry.COLUMN_COIN_ID + " = ?";
        Cursor c = db.query(CryptoDatabaseContract.PortfolioEntry.TABLE_NAME, projection(), where,
                new String[]{ String.valueOf(userId), coinId }, null, null,
                CryptoDatabaseContract.PortfolioEntry.COLUMN_UPDATED_AT + " DESC");
        while (c.moveToNext()) list.add(cursorToItem(c));
        c.close();
        return list;
    }

    @Override
    public double getTotalQuantity(long userId, String coinId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT SUM(" + CryptoDatabaseContract.PortfolioEntry.COLUMN_QUANTITY + ") FROM " +
                CryptoDatabaseContract.PortfolioEntry.TABLE_NAME + " WHERE " +
                CryptoDatabaseContract.PortfolioEntry.COLUMN_USER_ID + " = ? AND " +
                CryptoDatabaseContract.PortfolioEntry.COLUMN_COIN_ID + " = ?";
        Cursor c = db.rawQuery(sql, new String[]{ String.valueOf(userId), coinId });
        double sum = 0;
        if (c.moveToFirst()) sum = c.getDouble(0);
        c.close();
        return sum;
    }

    @Override
    public double getTotalInvestment(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT SUM(" + CryptoDatabaseContract.PortfolioEntry.COLUMN_PURCHASE_PRICE + " * " +
                CryptoDatabaseContract.PortfolioEntry.COLUMN_QUANTITY + ") FROM " +
                CryptoDatabaseContract.PortfolioEntry.TABLE_NAME + " WHERE " +
                CryptoDatabaseContract.PortfolioEntry.COLUMN_USER_ID + " = ?";
        Cursor c = db.rawQuery(sql, new String[]{ String.valueOf(userId) });
        double sum = 0;
        if (c.moveToFirst()) sum = c.getDouble(0);
        c.close();
        return sum;
    }

    @Override
    public double getTotalValue(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT SUM(" + CryptoDatabaseContract.PortfolioEntry.COLUMN_TOTAL_VALUE + ") FROM " +
                CryptoDatabaseContract.PortfolioEntry.TABLE_NAME + " WHERE " +
                CryptoDatabaseContract.PortfolioEntry.COLUMN_USER_ID + " = ?";
        Cursor c = db.rawQuery(sql, new String[]{ String.valueOf(userId) });
        double sum = 0;
        if (c.moveToFirst()) sum = c.getDouble(0);
        c.close();
        return sum;
    }

    @Override
    public boolean addTransaction(long userId, String coinId, double quantity, double pricePerCoin, String transactionType) {
        PortfolioItem item = new PortfolioItem();
        item.setUserId(userId);
        item.setCoinId(coinId);
        item.setQuantity(quantity);
        item.setPurchasePrice(pricePerCoin);
        item.setPurchaseDate(System.currentTimeMillis());
        item.setTransactionType(transactionType);
        item.setCurrentPrice(pricePerCoin);
        item.setTotalValue(quantity * pricePerCoin);
        item.setProfitLoss(0);
        item.setTransactionFee(0);
        item.setCreatedAt(System.currentTimeMillis());
        item.setUpdatedAt(System.currentTimeMillis());
        return insert(item) != -1;
    }

    @Override
    public int removeAllByUserAndCoin(long userId, String coinId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String where = CryptoDatabaseContract.PortfolioEntry.COLUMN_USER_ID + " = ? AND " +
                CryptoDatabaseContract.PortfolioEntry.COLUMN_COIN_ID + " = ?";
        return db.delete(CryptoDatabaseContract.PortfolioEntry.TABLE_NAME, where,
                new String[]{ String.valueOf(userId), coinId });
    }

    @Override
    public PortfolioSummary getPortfolioSummary(long userId) {
        double investment = getTotalInvestment(userId);
        double value = getTotalValue(userId);
        double profit = value - investment;
        double percentage = investment > 0 ? (profit / investment) * 100.0 : 0.0;

        int coins = getDistinctCoins(userId).size();
        int transactions = countUserTransactions(userId);

        return new PortfolioSummary(investment, value, profit, percentage, coins, transactions);
    }

    @Override
    public List<String> getDistinctCoins(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> coins = new ArrayList<>();
        String sql = "SELECT DISTINCT " + CryptoDatabaseContract.PortfolioEntry.COLUMN_COIN_ID +
                " FROM " + CryptoDatabaseContract.PortfolioEntry.TABLE_NAME +
                " WHERE " + CryptoDatabaseContract.PortfolioEntry.COLUMN_USER_ID + " = ?";
        Cursor c = db.rawQuery(sql, new String[]{ String.valueOf(userId) });
        while (c.moveToNext()) coins.add(c.getString(0));
        c.close();
        return coins;
    }

    @Override
    public boolean hasHoldings(long userId, String coinId) {
        return getTotalQuantity(userId, coinId) > 0;
    }

    @Override
    public int updateCurrentPrice(String coinId, double currentPrice) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(CryptoDatabaseContract.PortfolioEntry.COLUMN_CURRENT_PRICE, currentPrice);
        String where = CryptoDatabaseContract.PortfolioEntry.COLUMN_COIN_ID + " = ?";
        return db.update(CryptoDatabaseContract.PortfolioEntry.TABLE_NAME, v, where, new String[]{ coinId });
    }

    @Override
    public List<PortfolioItemWithProfit> getPortfolioWithProfit(long userId) {
        List<PortfolioItemWithProfit> list = new ArrayList<>();
        for (PortfolioItem item : getByUserId(userId)) {
            double investment = item.getPurchasePrice() * item.getQuantity();
            double value = item.getCurrentPrice() * item.getQuantity();
            double profit = value - investment - item.getTransactionFee();
            double percentage = investment > 0 ? (profit / investment) * 100.0 : 0.0;
            list.add(new PortfolioItemWithProfit(item, value, profit, percentage));
        }
        return list;
    }

    private int countUserTransactions(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + CryptoDatabaseContract.PortfolioEntry.TABLE_NAME +
                " WHERE " + CryptoDatabaseContract.PortfolioEntry.COLUMN_USER_ID + " = ?",
                new String[]{ String.valueOf(userId) });
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    private String[] projection() {
        return new String[]{
                CryptoDatabaseContract.PortfolioEntry._ID,
                CryptoDatabaseContract.PortfolioEntry.COLUMN_USER_ID,
                CryptoDatabaseContract.PortfolioEntry.COLUMN_COIN_ID,
                CryptoDatabaseContract.PortfolioEntry.COLUMN_QUANTITY,
                CryptoDatabaseContract.PortfolioEntry.COLUMN_PURCHASE_PRICE,
                CryptoDatabaseContract.PortfolioEntry.COLUMN_PURCHASE_DATE,
                CryptoDatabaseContract.PortfolioEntry.COLUMN_CURRENT_PRICE,
                CryptoDatabaseContract.PortfolioEntry.COLUMN_TOTAL_VALUE,
                CryptoDatabaseContract.PortfolioEntry.COLUMN_PROFIT_LOSS,
                CryptoDatabaseContract.PortfolioEntry.COLUMN_TRANSACTION_FEE,
                CryptoDatabaseContract.PortfolioEntry.COLUMN_TRANSACTION_TYPE,
                CryptoDatabaseContract.PortfolioEntry.COLUMN_CREATED_AT,
                CryptoDatabaseContract.PortfolioEntry.COLUMN_UPDATED_AT
        };
    }

    private PortfolioItem cursorToItem(Cursor c) {
        PortfolioItem item = new PortfolioItem();
        item.setId(c.getLong(c.getColumnIndexOrThrow(CryptoDatabaseContract.PortfolioEntry._ID)));
        item.setUserId(c.getLong(c.getColumnIndexOrThrow(CryptoDatabaseContract.PortfolioEntry.COLUMN_USER_ID)));
        item.setCoinId(c.getString(c.getColumnIndexOrThrow(CryptoDatabaseContract.PortfolioEntry.COLUMN_COIN_ID)));
        item.setQuantity(c.getDouble(c.getColumnIndexOrThrow(CryptoDatabaseContract.PortfolioEntry.COLUMN_QUANTITY)));
        item.setPurchasePrice(c.getDouble(c.getColumnIndexOrThrow(CryptoDatabaseContract.PortfolioEntry.COLUMN_PURCHASE_PRICE)));
        item.setPurchaseDate(c.getLong(c.getColumnIndexOrThrow(CryptoDatabaseContract.PortfolioEntry.COLUMN_PURCHASE_DATE)));
        item.setCurrentPrice(c.getDouble(c.getColumnIndexOrThrow(CryptoDatabaseContract.PortfolioEntry.COLUMN_CURRENT_PRICE)));
        item.setTotalValue(c.getDouble(c.getColumnIndexOrThrow(CryptoDatabaseContract.PortfolioEntry.COLUMN_TOTAL_VALUE)));
        item.setProfitLoss(c.getDouble(c.getColumnIndexOrThrow(CryptoDatabaseContract.PortfolioEntry.COLUMN_PROFIT_LOSS)));
        item.setTransactionFee(c.getDouble(c.getColumnIndexOrThrow(CryptoDatabaseContract.PortfolioEntry.COLUMN_TRANSACTION_FEE)));
        item.setTransactionType(c.getString(c.getColumnIndexOrThrow(CryptoDatabaseContract.PortfolioEntry.COLUMN_TRANSACTION_TYPE)));
        item.setCreatedAt(c.getLong(c.getColumnIndexOrThrow(CryptoDatabaseContract.PortfolioEntry.COLUMN_CREATED_AT)));
        item.setUpdatedAt(c.getLong(c.getColumnIndexOrThrow(CryptoDatabaseContract.PortfolioEntry.COLUMN_UPDATED_AT)));
        return item;
    }
}