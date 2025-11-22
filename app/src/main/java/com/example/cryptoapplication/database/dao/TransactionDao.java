package com.example.cryptoapplication.database.dao;

import com.example.cryptoapplication.models.TransactionRecord;
import java.util.List;

public interface TransactionDao {
    long insert(TransactionRecord record);
    List<TransactionRecord> getByUserId(long userId);
    void deleteAll();
}