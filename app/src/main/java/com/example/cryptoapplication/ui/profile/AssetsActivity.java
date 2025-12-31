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
import com.example.cryptoapplication.models.ConsolidatedAsset;
import com.example.cryptoapplication.ui.home.adapter.ConsolidatedAssetsAdapter;

import java.text.DecimalFormat;
import java.util.List;

public class AssetsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ConsolidatedAssetsAdapter adapter;
    private View emptyState;
    private TextView txtTotalValue;
    private final DecimalFormat priceFormat = new DecimalFormat("#,##0.00");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assets);

        recyclerView = findViewById(R.id.assetsRecyclerView);
        emptyState = findViewById(R.id.emptyState);
        txtTotalValue = findViewById(R.id.txtTotalValue);
        ImageButton btnBack = findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load assets
        loadAssets();

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAssets(); // Refresh when returning to screen
    }

    private void loadAssets() {
        SimpleDatabaseService db = SimpleDatabaseService.getInstance(this);
        List<ConsolidatedAsset> assets = db.getConsolidatedAssets();

        if (assets == null || assets.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            txtTotalValue.setText("$0.00");
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            
            if (adapter == null) {
                adapter = new ConsolidatedAssetsAdapter(assets);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateAssets(assets);
            }

            // Show total assets count
            txtTotalValue.setText(assets.size() + " Assets");
        }
    }
}