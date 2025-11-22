package com.example.cryptoapplication.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cryptoapplication.database.CryptoDatabaseContract;
import com.example.cryptoapplication.database.CryptoDatabaseHelper;
import com.example.cryptoapplication.models.SearchHistoryItem;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryDaoImpl implements SearchHistoryDao {

    private final CryptoDatabaseHelper dbHelper;

    public SearchHistoryDaoImpl(Context context) {
        this.dbHelper = new CryptoDatabaseHelper(context);
    }

    @Override
    public long insert(SearchHistoryItem entity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(CryptoDatabaseContract.SearchHistoryEntry.COLUMN_USER_ID, entity.getUserId());
        v.put(CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCH_QUERY, entity.getSearchQuery());
        v.put(CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCHED_AT, entity.getSearchedAt());
        return db.insert(CryptoDatabaseContract.SearchHistoryEntry.TABLE_NAME, null, v);
    }

    @Override
    public int update(SearchHistoryItem entity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(CryptoDatabaseContract.SearchHistoryEntry.COLUMN_USER_ID, entity.getUserId());
        v.put(CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCH_QUERY, entity.getSearchQuery());
        v.put(CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCHED_AT, entity.getSearchedAt());
        String where = CryptoDatabaseContract.SearchHistoryEntry._ID + " = ?";
        String[] args = { String.valueOf(entity.getId()) };
        return db.update(CryptoDatabaseContract.SearchHistoryEntry.TABLE_NAME, v, where, args);
    }

    @Override
    public int delete(SearchHistoryItem entity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String where = CryptoDatabaseContract.SearchHistoryEntry._ID + " = ?";
        String[] args = { String.valueOf(entity.getId()) };
        return db.delete(CryptoDatabaseContract.SearchHistoryEntry.TABLE_NAME, where, args);
    }

    @Override
    public List<SearchHistoryItem> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<SearchHistoryItem> list = new ArrayList<>();
        String[] projection = {
                CryptoDatabaseContract.SearchHistoryEntry._ID,
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_USER_ID,
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCH_QUERY,
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCHED_AT
        };
        Cursor c = db.query(CryptoDatabaseContract.SearchHistoryEntry.TABLE_NAME, projection,
                null, null, null, null,
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCHED_AT + " DESC");
        while (c.moveToNext()) list.add(cursorToItem(c));
        c.close();
        return list;
    }

    @Override
    public SearchHistoryItem getById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                CryptoDatabaseContract.SearchHistoryEntry._ID,
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_USER_ID,
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCH_QUERY,
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCHED_AT
        };
        String sel = CryptoDatabaseContract.SearchHistoryEntry._ID + " = ?";
        String[] args = { String.valueOf(id) };
        Cursor c = db.query(CryptoDatabaseContract.SearchHistoryEntry.TABLE_NAME, projection, sel, args, null, null, null);
        SearchHistoryItem item = null;
        if (c.moveToFirst()) item = cursorToItem(c);
        c.close();
        return item;
    }

    @Override
    public int count() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + CryptoDatabaseContract.SearchHistoryEntry.TABLE_NAME, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    @Override
    public boolean exists(long id) {
        return getById(id) != null;
    }

    @Override
    public int deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(CryptoDatabaseContract.SearchHistoryEntry.TABLE_NAME, null, null);
    }

    @Override
    public List<SearchHistoryItem> getByUserId(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<SearchHistoryItem> list = new ArrayList<>();
        String[] projection = {
                CryptoDatabaseContract.SearchHistoryEntry._ID,
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_USER_ID,
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCH_QUERY,
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCHED_AT
        };
        String sel = CryptoDatabaseContract.SearchHistoryEntry.COLUMN_USER_ID + " = ?";
        String[] args = { String.valueOf(userId) };
        Cursor c = db.query(CryptoDatabaseContract.SearchHistoryEntry.TABLE_NAME, projection, sel, args, null, null,
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCHED_AT + " DESC");
        while (c.moveToNext()) list.add(cursorToItem(c));
        c.close();
        return list;
    }

    @Override
    public List<SearchHistoryItem> getRecentSearches(long userId, int limit) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<SearchHistoryItem> list = new ArrayList<>();
        String sql = "SELECT " + CryptoDatabaseContract.SearchHistoryEntry._ID + ", " +
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_USER_ID + ", " +
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCH_QUERY + ", " +
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCHED_AT +
                " FROM " + CryptoDatabaseContract.SearchHistoryEntry.TABLE_NAME +
                " WHERE " + CryptoDatabaseContract.SearchHistoryEntry.COLUMN_USER_ID + " = ?" +
                " ORDER BY " + CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCHED_AT + " DESC" +
                " LIMIT " + limit;
        Cursor c = db.rawQuery(sql, new String[]{ String.valueOf(userId) });
        while (c.moveToNext()) list.add(cursorToItem(c));
        c.close();
        return list;
    }

    @Override
    public List<SearchHistoryItem> searchInHistory(long userId, String query) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<SearchHistoryItem> list = new ArrayList<>();
        String[] projection = {
                CryptoDatabaseContract.SearchHistoryEntry._ID,
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_USER_ID,
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCH_QUERY,
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCHED_AT
        };
        String sel = CryptoDatabaseContract.SearchHistoryEntry.COLUMN_USER_ID + " = ? AND " +
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCH_QUERY + " LIKE ?";
        String[] args = { String.valueOf(userId), "%" + query + "%" };
        Cursor c = db.query(CryptoDatabaseContract.SearchHistoryEntry.TABLE_NAME, projection, sel, args, null, null,
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCHED_AT + " DESC");
        while (c.moveToNext()) list.add(cursorToItem(c));
        c.close();
        return list;
    }

    @Override
    public boolean addSearchQuery(long userId, String query) {
        SearchHistoryItem item = new SearchHistoryItem();
        item.setUserId(userId);
        item.setSearchQuery(query);
        item.setSearchedAt(System.currentTimeMillis());
        return insert(item) != -1;
    }

    @Override
    public int clearUserSearchHistory(long userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String where = CryptoDatabaseContract.SearchHistoryEntry.COLUMN_USER_ID + " = ?";
        String[] args = { String.valueOf(userId) };
        return db.delete(CryptoDatabaseContract.SearchHistoryEntry.TABLE_NAME, where, args);
    }

    @Override
    public int deleteOldEntries(int daysToKeep) {
        long cutoff = System.currentTimeMillis() - (long)daysToKeep * 24L * 60L * 60L * 1000L;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String where = CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCHED_AT + " < ?";
        String[] args = { String.valueOf(cutoff) };
        return db.delete(CryptoDatabaseContract.SearchHistoryEntry.TABLE_NAME, where, args);
    }

    @Override
    public List<String> getMostSearchedQueries(int limit) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> queries = new ArrayList<>();
        String sql = "SELECT " + CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCH_QUERY + ", COUNT(*) AS cnt" +
                " FROM " + CryptoDatabaseContract.SearchHistoryEntry.TABLE_NAME +
                " GROUP BY " + CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCH_QUERY +
                " ORDER BY cnt DESC LIMIT " + limit;
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) queries.add(c.getString(0));
        c.close();
        return queries;
    }

    @Override
    public boolean searchQueryExists(long userId, String query) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT 1 FROM " + CryptoDatabaseContract.SearchHistoryEntry.TABLE_NAME +
                " WHERE " + CryptoDatabaseContract.SearchHistoryEntry.COLUMN_USER_ID + " = ? AND " +
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCH_QUERY + " = ? LIMIT 1";
        Cursor c = db.rawQuery(sql, new String[]{ String.valueOf(userId), query });
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    @Override
    public boolean updateSearchTimestamp(long userId, String query) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCHED_AT, System.currentTimeMillis());
        String where = CryptoDatabaseContract.SearchHistoryEntry.COLUMN_USER_ID + " = ? AND " +
                CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCH_QUERY + " = ?";
        String[] args = { String.valueOf(userId), query };
        return db.update(CryptoDatabaseContract.SearchHistoryEntry.TABLE_NAME, v, where, args) > 0;
    }

    @Override
    public List<String> getUniqueUserQueries(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT " + CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCH_QUERY +
                " FROM " + CryptoDatabaseContract.SearchHistoryEntry.TABLE_NAME +
                " WHERE " + CryptoDatabaseContract.SearchHistoryEntry.COLUMN_USER_ID + " = ?";
        Cursor c = db.rawQuery(sql, new String[]{ String.valueOf(userId) });
        while (c.moveToNext()) list.add(c.getString(0));
        c.close();
        return list;
    }

    private SearchHistoryItem cursorToItem(Cursor c) {
        SearchHistoryItem item = new SearchHistoryItem();
        item.setId(c.getLong(c.getColumnIndexOrThrow(CryptoDatabaseContract.SearchHistoryEntry._ID)));
        item.setUserId(c.getLong(c.getColumnIndexOrThrow(CryptoDatabaseContract.SearchHistoryEntry.COLUMN_USER_ID)));
        item.setSearchQuery(c.getString(c.getColumnIndexOrThrow(CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCH_QUERY)));
        item.setSearchedAt(c.getLong(c.getColumnIndexOrThrow(CryptoDatabaseContract.SearchHistoryEntry.COLUMN_SEARCHED_AT)));
        return item;
    }
}