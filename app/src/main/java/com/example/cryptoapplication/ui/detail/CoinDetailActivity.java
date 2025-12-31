package com.example.cryptoapplication.ui.detail;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cryptoapplication.R;
import com.example.cryptoapplication.database.SimpleDatabaseService;
import com.example.cryptoapplication.models.PortfolioItem;
import com.example.cryptoapplication.models.User;
import com.example.cryptoapplication.ui.detail.adapter.PriceHistoryAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoinDetailActivity extends AppCompatActivity {

    // UI Components
    private ImageView btnBack, btnFavorite, ivCoinImage;
    private TextView tvCoinName, tvCoinSymbol, tvCoinPrice, tvCoinPriceUSD, tvCoinChange;
    private MaterialButton btnBuyTab, btnSellTab, btnExecuteTrade;
    private MaterialButton btn25Percent, btn50Percent, btn75Percent, btn100Percent;
    private TextInputEditText etAmount;
    private TextView tvUSDAmount, tvUserQuantity, tvUserValue;
    private RecyclerView rvPriceHistory;
    private View cardUserHoldings;

    // Data
    private SimpleDatabaseService database;
    private ExecutorService executor;
    private Handler mainHandler;
    private PriceHistoryAdapter priceHistoryAdapter;

    // Coin Data
    private String coinId, coinSymbol, coinName, coinImage;
    private double coinPrice, coinChange24h;
    private boolean isBuyMode = true;
    private boolean isFavorite = false;
    private double userHoldings = 0.0;

    private final DecimalFormat priceFormat = new DecimalFormat("#,##0.00");
    private final DecimalFormat cryptoFormat = new DecimalFormat("#,##0.########");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_detail);

        // Initialize
        database = SimpleDatabaseService.getInstance(this);
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        // Get coin data from intent
        getCoinDataFromIntent();
        initViews();
        setupUI();
        setupTradingSection();
        setupPriceHistory();
        loadUserHoldings();
    }

    private void getCoinDataFromIntent() {
        coinId = getIntent().getStringExtra("coin_id");
        coinSymbol = getIntent().getStringExtra("coin_symbol");
        coinName = getIntent().getStringExtra("coin_name");
        coinPrice = getIntent().getDoubleExtra("coin_price", 0.0);
        coinImage = getIntent().getStringExtra("coin_image");
        coinChange24h = getIntent().getDoubleExtra("coin_change_24h", 0.0);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnFavorite = findViewById(R.id.btnFavorite);
        ivCoinImage = findViewById(R.id.ivCoinImage);
        tvCoinName = findViewById(R.id.tvCoinName);
        tvCoinSymbol = findViewById(R.id.tvCoinSymbol);
        tvCoinPrice = findViewById(R.id.tvCoinPrice);
        tvCoinPriceUSD = findViewById(R.id.tvCoinPriceUSD);
        tvCoinChange = findViewById(R.id.tvCoinChange);
        btnBuyTab = findViewById(R.id.btnBuyTab);
        btnSellTab = findViewById(R.id.btnSellTab);
        btnExecuteTrade = findViewById(R.id.btnExecuteTrade);
        btn25Percent = findViewById(R.id.btn25Percent);
        btn50Percent = findViewById(R.id.btn50Percent);
        btn75Percent = findViewById(R.id.btn75Percent);
        btn100Percent = findViewById(R.id.btn100Percent);
        etAmount = findViewById(R.id.etAmount);
        tvUSDAmount = findViewById(R.id.tvUSDAmount);
        tvUserQuantity = findViewById(R.id.tvUserQuantity);
        tvUserValue = findViewById(R.id.tvUserValue);
        rvPriceHistory = findViewById(R.id.rvPriceHistory);
        cardUserHoldings = findViewById(R.id.cardUserHoldings);
    }

    private void setupUI() {
        // Set coin information
        tvCoinName.setText(coinName != null ? coinName : "Unknown");
        tvCoinSymbol.setText(coinSymbol != null ? coinSymbol.toUpperCase() : "");
        tvCoinPrice.setText("$" + priceFormat.format(coinPrice));
        tvCoinPriceUSD.setText("$" + priceFormat.format(coinPrice));

        // Set price change with color
        String changeText = (coinChange24h >= 0 ? "+" : "") + String.format("%.2f%%", coinChange24h);
        tvCoinChange.setText(changeText);
        int changeColor = coinChange24h >= 0 ? 
            ContextCompat.getColor(this, R.color.green_profit) : 
            ContextCompat.getColor(this, R.color.red_loss);
        tvCoinChange.setTextColor(changeColor);

        // Load coin image
        if (coinImage != null && !coinImage.isEmpty()) {
            Glide.with(this)
                    .load(coinImage)
                    .placeholder(R.drawable.placeholder_foreground)
                    .error(R.drawable.error_placeholder_foreground)
                    .into(ivCoinImage);
        }

        // Setup click listeners
        btnBack.setOnClickListener(v -> finish());
        btnFavorite.setOnClickListener(v -> toggleFavorite());
        checkFavoriteStatus();
    }

    private void setupTradingSection() {
        // Buy/Sell tab listeners
        btnBuyTab.setOnClickListener(v -> switchToBuyMode());
        btnSellTab.setOnClickListener(v -> switchToSellMode());

        // Percentage buttons
        btn25Percent.setOnClickListener(v -> setPercentageAmount(0.25));
        btn50Percent.setOnClickListener(v -> setPercentageAmount(0.50));
        btn75Percent.setOnClickListener(v -> setPercentageAmount(0.75));
        btn100Percent.setOnClickListener(v -> setPercentageAmount(1.0));

        // Amount input listener
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateUSDAmount();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Execute trade button
        btnExecuteTrade.setOnClickListener(v -> executeTrade());

        // Set initial mode
        switchToBuyMode();
    }

    private void setupPriceHistory() {
        priceHistoryAdapter = new PriceHistoryAdapter(generateMockPriceHistory());
        rvPriceHistory.setLayoutManager(new LinearLayoutManager(this));
        rvPriceHistory.setAdapter(priceHistoryAdapter);
    }

    // SIMPLE HOLDINGS CALCULATION
    private void loadUserHoldings() {
        executor.execute(() -> {
            userHoldings = 0.0;
            
            if (database.isUserLoggedIn()) {
                userHoldings = database.calculateUserHoldings(coinId);
            }

            mainHandler.post(() -> updateUserHoldingsUI());
        });
    }

    private void updateUserHoldingsUI() {
        if (userHoldings > 0) {
            cardUserHoldings.setVisibility(View.VISIBLE);
            tvUserQuantity.setText(cryptoFormat.format(userHoldings) + " " + coinSymbol);
            double value = userHoldings * coinPrice;
            tvUserValue.setText("$" + priceFormat.format(value));
        } else {
            cardUserHoldings.setVisibility(View.GONE);
        }
    }

    private void switchToBuyMode() {
        isBuyMode = true;
        btnBuyTab.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.purple_primary));
        btnBuyTab.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        btnSellTab.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.transparent));
        btnSellTab.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        btnExecuteTrade.setText("Buy Now");
        btnExecuteTrade.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.purple_primary));
        etAmount.setHint("Amount to buy");
    }

    private void switchToSellMode() {
        isBuyMode = false;
        btnSellTab.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red_loss));
        btnSellTab.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        btnBuyTab.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.transparent));
        btnBuyTab.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        btnExecuteTrade.setText("Sell Now");
        btnExecuteTrade.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red_loss));
        etAmount.setHint("Amount to sell");
    }

    // SIMPLE PERCENTAGE CALCULATION
    private void setPercentageAmount(double percentage) {
        if (!database.isUserLoggedIn()) {
            Toast.makeText(this, "Please log in", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        
        if (isBuyMode) {
            // Buy mode: Use percentage of user's money (get fresh balance)
            User user = database.getCurrentUser();
            double moneyToSpend = user.getBalance() * percentage;
            amount = moneyToSpend / coinPrice;
            System.out.println("BUY %: " + (percentage*100) + "% of $" + user.getBalance() + " = $" + moneyToSpend + " = " + amount + " coins");
        } else {
            // Sell mode: Use percentage of user's crypto (get fresh holdings)
            double freshHoldings = database.calculateUserHoldings(coinId);
            amount = freshHoldings * percentage;
            System.out.println("SELL %: " + (percentage*100) + "% of " + freshHoldings + " = " + amount + " coins");
        }

        etAmount.setText(cryptoFormat.format(amount));
    }

    private void updateUSDAmount() {
        try {
            String amountText = etAmount.getText().toString().trim();
            if (!amountText.isEmpty()) {
                double amount = Double.parseDouble(amountText);
                double usdValue = amount * coinPrice;
                tvUSDAmount.setText("$" + priceFormat.format(usdValue));
            } else {
                tvUSDAmount.setText("$0.00");
            }
        } catch (NumberFormatException e) {
            tvUSDAmount.setText("$0.00");
        }
    }

    // SIMPLE TRADE EXECUTION
    private void executeTrade() {
        if (!database.isUserLoggedIn()) {
            Toast.makeText(this, "Please log in", Toast.LENGTH_SHORT).show();
            return;
        }

        String amountText = etAmount.getText().toString().trim();
        if (amountText.isEmpty()) {
            Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                Toast.makeText(this, "Amount must be > 0", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isBuyMode) {
                buyNow(amount);
            } else {
                sellNow(amount);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
        }
    }

    // SIMPLE BUY LOGIC
    private void buyNow(double cryptoAmount) {
        double totalCost = cryptoAmount * coinPrice;
        
        System.out.println("UI BUY: " + cryptoAmount + " " + coinSymbol + " for $" + totalCost);
        
        boolean success = database.buyCryptocurrency(coinId, cryptoAmount, coinPrice);
        
        if (success) {
            Toast.makeText(this, "✅ Bought " + cryptoFormat.format(cryptoAmount) + " " + coinSymbol, Toast.LENGTH_SHORT).show();
            etAmount.setText("");
            loadUserHoldings(); // Refresh holdings display
        } else {
            String errorMsg = database.getLastErrorMessage();
            if (errorMsg.isEmpty()) {
                User user = database.getCurrentUser();
                errorMsg = "Buy failed. Need $" + priceFormat.format(totalCost) + ", have $" + priceFormat.format(user.getBalance());
            }
            Toast.makeText(this, "❌ " + errorMsg, Toast.LENGTH_LONG).show();
        }
    }

    // SIMPLE SELL LOGIC
    private void sellNow(double cryptoAmount) {
        System.out.println("UI SELL: " + cryptoAmount + " " + coinSymbol);
        
        boolean success = database.sellCryptocurrency(coinId, cryptoAmount, coinPrice);
        
        if (success) {
            double totalEarned = cryptoAmount * coinPrice;
            Toast.makeText(this, "✅ Sold " + cryptoFormat.format(cryptoAmount) + " " + coinSymbol + " for $" + priceFormat.format(totalEarned), Toast.LENGTH_SHORT).show();
            etAmount.setText("");
            loadUserHoldings(); // Refresh holdings display
        } else {
            String errorMsg = database.getLastErrorMessage();
            if (errorMsg.isEmpty()) {
                double holdings = database.calculateUserHoldings(coinId);
                errorMsg = "Sell failed. Need " + cryptoFormat.format(cryptoAmount) + ", have " + cryptoFormat.format(holdings);
            }
            Toast.makeText(this, "❌ " + errorMsg, Toast.LENGTH_LONG).show();
        }
    }

    private void checkFavoriteStatus() {
        executor.execute(() -> {
            boolean favorite = database.isCoinInFavorites(coinId);
            mainHandler.post(() -> {
                isFavorite = favorite;
                updateFavoriteIcon();
            });
        });
    }

    private void toggleFavorite() {
        if (!database.isUserLoggedIn()) {
            Toast.makeText(this, "Please log in", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            boolean success;
            if (isFavorite) {
                success = database.removeCoinFromFavorites(coinId);
            } else {
                success = database.addCoinToFavorites(coinId);
            }

            if (success) {
                isFavorite = !isFavorite;
                mainHandler.post(() -> {
                    updateFavoriteIcon();
                    String message = isFavorite ? "Added to favorites" : "Removed from favorites";
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void updateFavoriteIcon() {
        int iconRes = isFavorite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off;
        btnFavorite.setImageResource(iconRes);
    }

    private List<PriceHistoryAdapter.PriceEntry> generateMockPriceHistory() {
        List<PriceHistoryAdapter.PriceEntry> history = new ArrayList<>();
        
        for (int i = 0; i < 10; i++) {
            double variation = (Math.random() - 0.5) * 0.02;
            double price = coinPrice * (1 + variation);
            double quantity = Math.random() * 0.01;
            boolean isPositive = variation >= 0;
            
            history.add(new PriceHistoryAdapter.PriceEntry(price, quantity, isPositive));
        }
        
        return history;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}