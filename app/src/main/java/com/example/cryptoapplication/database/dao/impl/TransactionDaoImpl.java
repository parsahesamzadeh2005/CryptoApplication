package com.example.cryptoapplication.database.dao.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cryptoapplication.database.CryptoDatabaseContract;
import com.example.cryptoapplication.database.CryptoDatabaseHelper;
import com.example.cryptoapplication.database.dao.TransactionDao;
import com.example.cryptoapplication.models.TransactionRecord;

import java.util.ArrayList;
import java.util.List;

public class TransactionDaoImpl implements TransactionDao {
    private final CryptoDatabaseHelper dbHelper;

    public TransactionDaoImpl(CryptoDatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public long insert(TransactionRecord record) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CryptoDatabaseContract.TransactionEntry.COLUMN_USER_ID, record.getUserId());
        values.put(CryptoDatabaseContract.TransactionEntry.COLUMN_TYPE, record.getType());
        values.put(CryptoDatabaseContract.TransactionEntry.COLUMN_COIN_ID, record.getCoinId());
        values.put(CryptoDatabaseContract.TransactionEntry.COLUMN_QUANTITY, record.getQuantity());
        values.put(CryptoDatabaseContract.TransactionEntry.COLUMN_PRICE_PER_COIN, record.getPricePerCoin());
        values.put(CryptoDatabaseContract.TransactionEntry.COLUMN_FIAT_AMOUNT, record.getFiatAmount());
        values.put(CryptoDatabaseContract.TransactionEntry.COLUMN_TIMESTAMP, record.getTimestamp());
        return db.insert(CryptoDatabaseContract.TransactionEntry.TABLE_NAME, null, values);
    }

    @Override
    public List<TransactionRecord> getByUserId(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = new String[]{
                CryptoDatabaseContract.TransactionEntry._ID,
                CryptoDatabaseContract.TransactionEntry.COLUMN_USER_ID,
                CryptoDatabaseContract.TransactionEntry.COLUMN_TYPE,
                CryptoDatabaseContract.TransactionEntry.COLUMN_COIN_ID,
                CryptoDatabaseContract.TransactionEntry.COLUMN_QUANTITY,
                CryptoDatabaseContract.TransactionEntry.COLUMN_PRICE_PER_COIN,
                CryptoDatabaseContract.TransactionEntry.COLUMN_FIAT_AMOUNT,
                CryptoDatabaseContract.TransactionEntry.COLUMN_TIMESTAMP
        };
        String selection = CryptoDatabaseContract.TransactionEntry.COLUMN_USER_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(userId)};
        String orderBy = CryptoDatabaseContract.TransactionEntry.COLUMN_TIMESTAMP + " DESC";

        Cursor cursor = db.query(CryptoDatabaseContract.TransactionEntry.TABLE_NAME, columns,
                selection, selectionArgs, null, null, orderBy);

        List<TransactionRecord> list = new ArrayList<>();
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    TransactionRecord r = new TransactionRecord(
                            cursor.getLong(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.TransactionEntry._ID)),
                            cursor.getLong(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.TransactionEntry.COLUMN_USER_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.TransactionEntry.COLUMN_TYPE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.TransactionEntry.COLUMN_COIN_ID)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.TransactionEntry.COLUMN_QUANTITY)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.TransactionEntry.COLUMN_PRICE_PER_COIN)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.TransactionEntry.COLUMN_FIAT_AMOUNT)),
                            cursor.getLong(cursor.getColumnIndexOrThrow(CryptoDatabaseContract.TransactionEntry.COLUMN_TIMESTAMP))
                    );
                    list.add(r);
                }
            } finally {
                cursor.close();
            }
        }
        return list;
    }

    @Override
    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(CryptoDatabaseContract.TransactionEntry.TABLE_NAME, null, null);
    }
}