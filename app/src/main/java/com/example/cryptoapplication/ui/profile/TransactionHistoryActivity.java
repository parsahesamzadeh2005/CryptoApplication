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
import com.example.cryptoapplication.database.SimpleDatabaseService;
import com.example.cryptoapplication.models.PortfolioItem;

import java.util.List;

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

        // Use the new SimpleDatabaseService
        SimpleDatabaseService db = SimpleDatabaseService.getInstance(this);
        List<PortfolioItem> transactions = db.getUserPortfolio();

        if (transactions == null || transactions.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new TransactionAdapter(transactions, db);
            recyclerView.setAdapter(adapter);
        }

        btnBack.setOnClickListener(v -> finish());
    }
}