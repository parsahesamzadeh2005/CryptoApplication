package com.example.cryptoapplication.ui.detail;

import android.os.Bundle;
import android.app.AlertDialog;
import android.text.InputType;
import com.google.android.material.button.MaterialButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cryptoapplication.database.DatabaseService;
import com.example.cryptoapplication.R;

public class CoinDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_detail);

        ImageView ivCoinImage = findViewById(R.id.ivCoinImage);
        TextView tvCoinName = findViewById(R.id.tvCoinName);
        TextView tvCoinSymbol = findViewById(R.id.tvCoinSymbol);
        TextView tvCoinPrice = findViewById(R.id.tvCoinPrice);
        TextView tvCoinChange = findViewById(R.id.tvCoinChange);
        MaterialButton btnBuy = findViewById(R.id.btnBuyCoin);

        String id = getIntent().getStringExtra("coin_id");
        String symbol = getIntent().getStringExtra("coin_symbol");
        String name = getIntent().getStringExtra("coin_name");
        double price = getIntent().getDoubleExtra("coin_price", 0.0);
        String image = getIntent().getStringExtra("coin_image");
        double change24h = getIntent().getDoubleExtra("coin_change_24h", 0.0);

        tvCoinName.setText(name != null ? name : "-");
        tvCoinSymbol.setText(symbol != null ? symbol.toUpperCase() : "-");
        tvCoinPrice.setText(String.format("$%.2f", price));
        tvCoinChange.setText(String.format("%.2f%%", change24h));

        if (image != null && !image.isEmpty()) {
            Glide.with(this)
                    .load(image)
                    .placeholder(R.drawable.placeholder_foreground)
                    .error(R.drawable.error_placeholder_foreground)
                    .into(ivCoinImage);
        } else {
            ivCoinImage.setImageResource(R.drawable.placeholder_foreground);
        }

        btnBuy.setOnClickListener(v -> {
            DatabaseService db = DatabaseService.getInstance(this);
            if (!db.isLoggedIn()) {
                Toast.makeText(this, "Please log in to buy.", Toast.LENGTH_SHORT).show();
                return;
            }

            final EditText input = new EditText(this);
            input.setHint("Fiat amount (e.g., 50.00)");
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

            new AlertDialog.Builder(this)
                    .setTitle("Buy " + (name != null ? name : symbol))
                    .setMessage("Enter fiat amount to spend")
                    .setView(input)
                    .setPositiveButton("Buy", (d, w) -> {
                        String t = input.getText().toString().trim();
                        try {
                            double fiat = Double.parseDouble(t);
                            if (fiat <= 0 || price <= 0) throw new NumberFormatException();
                            boolean ok = db.buyCoinFiat(id, fiat, price);
                            if (ok) {
                                Toast.makeText(this, "Purchase completed", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Insufficient balance or error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", (d, w) -> d.dismiss())
                    .show();
        });
    }
}
