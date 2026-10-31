package com.example.cryptoapplication.ui.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
                adapter.setOnAssetClickListener(asset -> showSellDialog(asset));
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateAssets(assets);
            }

            // Show total assets count
            txtTotalValue.setText(assets.size() + " Assets");
        }
    }

    private void showSellDialog(ConsolidatedAsset asset) {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_sell_asset, null);

        TextView txtTitle   = dialogView.findViewById(R.id.txtSellDialogTitle);
        TextView txtBalance = dialogView.findViewById(R.id.txtAvailableBalance);
        EditText etAmount   = dialogView.findViewById(R.id.etSellAmount);
        Button btn25        = dialogView.findViewById(R.id.btn25);
        Button btn50        = dialogView.findViewById(R.id.btn50);
        Button btn75        = dialogView.findViewById(R.id.btn75);
        Button btn100       = dialogView.findViewById(R.id.btn100);
        Button btnCancel    = dialogView.findViewById(R.id.btnSellCancel);
        Button btnConfirm   = dialogView.findViewById(R.id.btnSellConfirm);

        double totalQty = asset.getTotalQuantity();
        DecimalFormat cryptoFmt = new DecimalFormat("#,##0.########");

        txtTitle.setText("Sell " + asset.getCoinName());
        txtBalance.setText("Available: " + cryptoFmt.format(totalQty)
                + " " + asset.getCoinSymbol().toUpperCase());

        // Quick-select percentage buttons fill the EditText with the corresponding fraction.
        // We store the precise double value in a single-element array so the confirm handler
        // can use it directly, bypassing the DecimalFormat round-trip that causes float errors.
        final double[] preciseAmount = {Double.NaN};

        // settingFromButton guards the TextWatcher so programmatic setText from a button
        // does not immediately wipe the precise value we just stored.
        final boolean[] settingFromButton = {false};

        btn25.setOnClickListener(v -> {
            settingFromButton[0] = true;
            preciseAmount[0] = totalQty * 0.25;
            etAmount.setText(cryptoFmt.format(preciseAmount[0]));
            settingFromButton[0] = false;
        });
        btn50.setOnClickListener(v -> {
            settingFromButton[0] = true;
            preciseAmount[0] = totalQty * 0.50;
            etAmount.setText(cryptoFmt.format(preciseAmount[0]));
            settingFromButton[0] = false;
        });
        btn75.setOnClickListener(v -> {
            settingFromButton[0] = true;
            preciseAmount[0] = totalQty * 0.75;
            etAmount.setText(cryptoFmt.format(preciseAmount[0]));
            settingFromButton[0] = false;
        });
        btn100.setOnClickListener(v -> {
            settingFromButton[0] = true;
            preciseAmount[0] = totalQty; // exact value, no formatting loss
            etAmount.setText(cryptoFmt.format(totalQty));
            settingFromButton[0] = false;
        });
        // Clear the precise cache only when the user manually edits the field,
        // not when a button programmatically sets the text.
        etAmount.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!settingFromButton[0]) preciseAmount[0] = Double.NaN;
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Let the layout's own background handle shape/corners
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            // If a percentage button was tapped, use the exact double value it stored to
            // avoid DecimalFormat round-trip errors (float-precision mismatch in the DB).
            double sellQty;
            if (!Double.isNaN(preciseAmount[0])) {
                sellQty = preciseAmount[0];
            } else {
                String raw = etAmount.getText().toString().trim().replace(",", "");
                if (raw.isEmpty()) {
                    etAmount.setError("Enter a valid number");
                    return;
                }
                try {
                    sellQty = Double.parseDouble(raw);
                } catch (NumberFormatException e) {
                    etAmount.setError("Enter a valid number");
                    return;
                }
            }

            if (sellQty <= 0) {
                etAmount.setError("Amount must be greater than zero");
                return;
            }

            if (sellQty > totalQty) {
                etAmount.setError("Cannot exceed available balance");
                return;
            }

            // sellCryptocurrency requires price > 0; use a negligible floor so the
            // sell succeeds even when no live price is cached (user receives ~$0).
            double price = asset.getCurrentPrice() > 0 ? asset.getCurrentPrice() : 0.00000001;

            SimpleDatabaseService db = SimpleDatabaseService.getInstance(this);
            boolean success = db.sellCryptocurrency(asset.getCoinId(), sellQty, price);

            if (success) {
                dialog.dismiss();
                Toast.makeText(this,
                        "Sold " + cryptoFmt.format(sellQty)
                                + " " + asset.getCoinSymbol().toUpperCase(),
                        Toast.LENGTH_SHORT).show();
                loadAssets(); // refresh list; removes asset row if quantity drops to 0
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Sale failed")
                        .setMessage(db.getLastErrorMessage())
                        .setPositiveButton("OK", (d, w) -> d.dismiss())
                        .show();
            }
        });

        dialog.show();
    }
}