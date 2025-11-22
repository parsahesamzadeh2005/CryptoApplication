package com.example.cryptoapplication.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptoapplication.R;
import com.example.cryptoapplication.database.DatabaseService;
import com.example.cryptoapplication.models.TransactionRecord;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private View emptyState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        recyclerView = findViewById(R.id.transactionRecyclerView);
        emptyState = findViewById(R.id.emptyState);
        ImageButton btnBack = findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseService db = DatabaseService.getInstance(this);
        List<TransactionRecord> items = db.getTransactionsForCurrentUser();

        if (items == null || items.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new TransactionAdapter(items, db);
            recyclerView.setAdapter(adapter);
        }

        btnBack.setOnClickListener(v -> finish());
    }
}