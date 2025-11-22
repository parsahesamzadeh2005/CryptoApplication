package com.example.cryptoapplication.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cryptoapplication.database.CryptoDatabaseContract;
import com.example.cryptoapplication.database.CryptoDatabaseHelper;
import com.example.cryptoapplication.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of UserDao interface.
 * Handles all user-related database operations.
 */
public class UserDaoImpl implements UserDao {
    
    private final CryptoDatabaseHelper dbHelper;
    
    public UserDaoImpl(Context context) {
        this.dbHelper = new CryptoDatabaseHelper(context);
    }
    
    @Override
    public long insert(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(CryptoDatabaseContract.Users.COLUMN_USERNAME, user.getUsername());
        values.put(CryptoDatabaseContract.Users.COLUMN_EMAIL, user.getEmail());
        values.put(CryptoDatabaseContract.Users.COLUMN_PASSWORD, user.getPassword());
        values.put(CryptoDatabaseContract.Users.COLUMN_PROFILE_IMAGE, user.getProfileImage());
        values.put(CryptoDatabaseContract.Users.COLUMN_CREATED_AT, user.getCreatedAt());
        values.put(CryptoDatabaseContract.Users.COLUMN_LAST_LOGIN, user.getLastLogin());
        values.put(CryptoDatabaseContract.Users.COLUMN_BALANCE, user.getBalance());
        
        return db.insert(CryptoDatabaseContract.Users.TABLE_NAME, null, values);
    }

    @Override
    public int update(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(CryptoDatabaseContract.Users.COLUMN_USERNAME, user.getUsername());
        values.put(CryptoDatabaseContract.Users.COLUMN_EMAIL, user.getEmail());
        values.put(CryptoDatabaseContract.Users.COLUMN_PASSWORD, user.getPassword());
        values.put(CryptoDatabaseContract.Users.COLUMN_PROFILE_IMAGE, user.getProfileImage());
        values.put(CryptoDatabaseContract.Users.COLUMN_LAST_LOGIN, user.getLastLogin());
        values.put(CryptoDatabaseContract.Users.COLUMN_BALANCE, user.getBalance());
        
        String selection = CryptoDatabaseContract.Users._ID + " = ?";
        String[] selectionArgs = {String.valueOf(user.getId())};
        
        return db.update(CryptoDatabaseContract.Users.TABLE_NAME, values, selection, selectionArgs);
    }
    
    @Override
    public int delete(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        String selection = CryptoDatabaseContract.Users._ID + " = ?";
        String[] selectionArgs = {String.valueOf(user.getId())};
        
        return db.delete(CryptoDatabaseContract.Users.TABLE_NAME, selection, selectionArgs);
    }
    
    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String[] projection = {
            CryptoDatabaseContract.Users._ID,
            CryptoDatabaseContract.Users.COLUMN_USERNAME,
            CryptoDatabaseContract.Users.COLUMN_EMAIL,
            CryptoDatabaseContract.Users.COLUMN_PASSWORD,
            CryptoDatabaseContract.Users.COLUMN_PROFILE_IMAGE,
            CryptoDatabaseContract.Users.COLUMN_CREATED_AT,
            CryptoDatabaseContract.Users.COLUMN_LAST_LOGIN,
            CryptoDatabaseContract.Users.COLUMN_BALANCE
        };
        
        Cursor cursor = db.query(
            CryptoDatabaseContract.Users.TABLE_NAME,
            projection,
            null, null, null, null,
            CryptoDatabaseContract.Users.COLUMN_CREATED_AT + " DESC"
        );
        
        while (cursor.moveToNext()) {
            users.add(cursorToUser(cursor));
        }
        cursor.close();
        
        return users;
    }
    
    @Override
    public User getById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String[] projection = {
            CryptoDatabaseContract.Users._ID,
            CryptoDatabaseContract.Users.COLUMN_USERNAME,
            CryptoDatabaseContract.Users.COLUMN_EMAIL,
            CryptoDatabaseContract.Users.COLUMN_PASSWORD,
            CryptoDatabaseContract.Users.COLUMN_PROFILE_IMAGE,
            CryptoDatabaseContract.Users.COLUMN_CREATED_AT,
            CryptoDatabaseContract.Users.COLUMN_LAST_LOGIN,
            CryptoDatabaseContract.Users.COLUMN_BALANCE
        };
        
        String selection = CryptoDatabaseContract.Users._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        
        Cursor cursor = db.query(
            CryptoDatabaseContract.Users.TABLE_NAME,
            projection,
            selection, selectionArgs, null, null, null
        );
        
        User user = null;
        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
        }
        cursor.close();
        
        return user;
    }
    
    @Override
    public int count() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String countQuery = "SELECT COUNT(*) FROM " + CryptoDatabaseContract.Users.TABLE_NAME;
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
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String selection = CryptoDatabaseContract.Users._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        
        Cursor cursor = db.query(
            CryptoDatabaseContract.Users.TABLE_NAME,
            new String[]{CryptoDatabaseContract.Users._ID},
            selection, selectionArgs, null, null, null
        );
        
        boolean exists = cursor.moveToFirst();
        cursor.close();
        
        return exists;
    }
    
    @Override
    public int deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(CryptoDatabaseContract.Users.TABLE_NAME, null, null);
    }
    
    @Override
    public User findByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String[] projection = {
            CryptoDatabaseContract.Users._ID,
            CryptoDatabaseContract.Users.COLUMN_USERNAME,
            CryptoDatabaseContract.Users.COLUMN_EMAIL,
            CryptoDatabaseContract.Users.COLUMN_PASSWORD,
            CryptoDatabaseContract.Users.COLUMN_PROFILE_IMAGE,
            CryptoDatabaseContract.Users.COLUMN_CREATED_AT,
            CryptoDatabaseContract.Users.COLUMN_LAST_LOGIN,
            CryptoDatabaseContract.Users.COLUMN_BALANCE
        };
        
        String selection = CryptoDatabaseContract.Users.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};
        
        Cursor cursor = db.query(
            CryptoDatabaseContract.Users.TABLE_NAME,
            projection,
            selection, selectionArgs, null, null, null
        );
        
        User user = null;
        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
        }
        cursor.close();
        
        return user;
    }
    
    @Override
    public User findByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String[] projection = {
            CryptoDatabaseContract.Users._ID,
            CryptoDatabaseContract.Users.COLUMN_USERNAME,
            CryptoDatabaseContract.Users.COLUMN_EMAIL,
            CryptoDatabaseContract.Users.COLUMN_PASSWORD,
            CryptoDatabaseContract.Users.COLUMN_PROFILE_IMAGE,
            CryptoDatabaseContract.Users.COLUMN_CREATED_AT,
            CryptoDatabaseContract.Users.COLUMN_LAST_LOGIN,
            CryptoDatabaseContract.Users.COLUMN_BALANCE
        };
        
        String selection = CryptoDatabaseContract.Users.COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        
        Cursor cursor = db.query(
            CryptoDatabaseContract.Users.TABLE_NAME,
            projection,
            selection, selectionArgs, null, null, null
        );
        
        User user = null;
        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
        }
        cursor.close();
        
        return user;
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email) != null;
    }
    
    @Override
    public int updateLastLogin(long userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(CryptoDatabaseContract.Users.COLUMN_LAST_LOGIN, System.currentTimeMillis());
        
        String selection = CryptoDatabaseContract.Users._ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        
        return db.update(CryptoDatabaseContract.Users.TABLE_NAME, values, selection, selectionArgs);
    }
    
    @Override
    public User getCurrentUser() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String[] projection = {
            CryptoDatabaseContract.Users._ID,
            CryptoDatabaseContract.Users.COLUMN_USERNAME,
            CryptoDatabaseContract.Users.COLUMN_EMAIL,
            CryptoDatabaseContract.Users.COLUMN_PASSWORD,
            CryptoDatabaseContract.Users.COLUMN_PROFILE_IMAGE,
            CryptoDatabaseContract.Users.COLUMN_CREATED_AT,
            CryptoDatabaseContract.Users.COLUMN_LAST_LOGIN
        };
        
        // For now, we'll get the most recently logged in user
        // In a real app, you might store the current user ID in SharedPreferences
        Cursor cursor = db.query(
            CryptoDatabaseContract.Users.TABLE_NAME,
            projection,
            null, null, null, null,
            CryptoDatabaseContract.Users.COLUMN_LAST_LOGIN + " DESC",
            "1"
        );
        
        User user = null;
        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
        }
        cursor.close();
        
        return user;
    }
    
    @Override
    public boolean setCurrentUser(long userId) {
        // In a real implementation, you might store this in SharedPreferences
        // For now, we'll just update the last login to mark as current
        return updateLastLogin(userId) > 0;
    }
    
    @Override
    public boolean clearCurrentUser() {
        // In a real implementation, you might clear this from SharedPreferences
        // For now, we'll return true as there's no specific current user tracking
        return true;
    }
    
    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.Users._ID))); 
        user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.Users.COLUMN_USERNAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.Users.COLUMN_EMAIL)));
        user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.Users.COLUMN_PASSWORD)));
        user.setProfileImage(cursor.getString(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.Users.COLUMN_PROFILE_IMAGE)));
        user.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.Users.COLUMN_CREATED_AT)));
        user.setLastLogin(cursor.getLong(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.Users.COLUMN_LAST_LOGIN)));
        int balanceIndex = cursor.getColumnIndex(CryptoDatabaseContract.Users.COLUMN_BALANCE);
        if (balanceIndex != -1) {
            user.setBalance(cursor.getDouble(balanceIndex));
        }
        return user;
    }
}