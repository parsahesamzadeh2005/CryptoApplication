package com.example.cryptoapplication.ui.home;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptoapplication.R;
import com.example.cryptoapplication.database.SimpleDatabaseService;
import com.example.cryptoapplication.models.CoinModel;
import com.example.cryptoapplication.models.User;
import com.example.cryptoapplication.models.PortfolioItem;
import com.example.cryptoapplication.repository.CoinRepositoryRetrofit;
import com.example.cryptoapplication.ui.home.adapter.CoinAdapter;
import com.example.cryptoapplication.ui.profile.ProfileActivity;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {

    // UI Components
    private RecyclerView coinRecyclerView;
    private CircularProgressIndicator loadingProgressBar;
    private TextView errorTextView;
    private SearchView searchView;
    private TextView btnAll, btnGainers, btnLosers;
    private View tabSelector;
    private ImageView btnProfile;
    private TextView txtTotalBalance;
    private TextView txtBalanceChange;

    // Data
    private CoinAdapter coinAdapter;
    private CoinRepositoryRetrofit coinRepository;
    private SimpleDatabaseService databaseService;
    private ExecutorService executorService;
    private Handler mainHandler;
    
    private List<CoinModel> allCoinsCache = new ArrayList<>();
    
    private enum TabType { ALL, GAINERS, LOSERS }
    private TabType currentTab = TabType.ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        // Initialize services first
        coinRepository = new CoinRepositoryRetrofit();
        databaseService = SimpleDatabaseService.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        
        // Initialize views
        initViews();
        
        // Setup components
        setupRecyclerView();
        setupTabs();
        setupSearch();
        setupProfileButton();
        
        // Load initial data
        loadUserBalance();
        loadAllCoins();
    }

    private void initViews() {
        coinRecyclerView = findViewById(R.id.coinListRecyclerView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        errorTextView = findViewById(R.id.errorTextView);
        searchView = findViewById(R.id.searchView);
        btnAll = findViewById(R.id.btnAllCoins);
        btnGainers = findViewById(R.id.btnTopGainers);
        btnLosers = findViewById(R.id.btnTopLosers);
        tabSelector = findViewById(R.id.tabSelector);
        btnProfile = findViewById(R.id.btnProfile);
        txtTotalBalance = findViewById(R.id.txtTotalBalance);
        txtBalanceChange = findViewById(R.id.txtBalanceChange);
        
        // Set initial selector position and tab colors
        updateTabSelection(TabType.ALL);
    }

    private void setupRecyclerView() {
        coinAdapter = new CoinAdapter(new ArrayList<>());
        coinRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        coinRecyclerView.setAdapter(coinAdapter);
        
        coinAdapter.setOnCoinClickListener(coin -> {
            try {
                Intent intent = new Intent(HomeActivity.this, 
                    com.example.cryptoapplication.ui.detail.CoinDetailActivity.class);
                intent.putExtra("coin_id", coin.getId());
                intent.putExtra("coin_symbol", coin.getSymbol());
                intent.putExtra("coin_name", coin.getName());
                intent.putExtra("coin_price", coin.getCurrentPrice());
                intent.putExtra("coin_image", coin.getImage());
                intent.putExtra("coin_change_24h", coin.getPriceChangePercentage24h());
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setupTabs() {
        View.OnClickListener tabListener = view -> {
            if (view.getId() == R.id.btnAllCoins) {
                updateTabSelection(TabType.ALL);
                loadAllCoins();
            } else if (view.getId() == R.id.btnTopGainers) {
                updateTabSelection(TabType.GAINERS);
                loadGainers();
            } else if (view.getId() == R.id.btnTopLosers) {
                updateTabSelection(TabType.LOSERS);
                loadLosers();
            }
        };

        if (btnAll != null) btnAll.setOnClickListener(tabListener);
        if (btnGainers != null) btnGainers.setOnClickListener(tabListener);
        if (btnLosers != null) btnLosers.setOnClickListener(tabListener);
    }

    private void setupSearch() {
        if (searchView == null) return;
        
        searchView.setIconifiedByDefault(false);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterCoins(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCoins(newText);
                return true;
            }
        });
    }

    private void setupProfileButton() {
        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        }
    }
    
    private void updateTabSelection(TabType selectedTab) {
        currentTab = selectedTab;
        
        // Update text colors and styles
        if (selectedTab == TabType.ALL) {
            btnAll.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            btnAll.setTypeface(btnAll.getTypeface(), android.graphics.Typeface.BOLD);
            btnGainers.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            btnGainers.setTypeface(btnGainers.getTypeface(), android.graphics.Typeface.NORMAL);
            btnLosers.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            btnLosers.setTypeface(btnLosers.getTypeface(), android.graphics.Typeface.NORMAL);
        } else if (selectedTab == TabType.GAINERS) {
            btnAll.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            btnAll.setTypeface(btnAll.getTypeface(), android.graphics.Typeface.NORMAL);
            btnGainers.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            btnGainers.setTypeface(btnGainers.getTypeface(), android.graphics.Typeface.BOLD);
            btnLosers.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            btnLosers.setTypeface(btnLosers.getTypeface(), android.graphics.Typeface.NORMAL);
        } else if (selectedTab == TabType.LOSERS) {
            btnAll.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            btnAll.setTypeface(btnAll.getTypeface(), android.graphics.Typeface.NORMAL);
            btnGainers.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            btnGainers.setTypeface(btnGainers.getTypeface(), android.graphics.Typeface.NORMAL);
            btnLosers.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            btnLosers.setTypeface(btnLosers.getTypeface(), android.graphics.Typeface.BOLD);
        }
        
        // Animate selector position
        animateTabSelector(selectedTab);
    }
    
    private void animateTabSelector(TabType selectedTab) {
        if (tabSelector == null) return;
        
        // Post to ensure layout is complete
        tabSelector.post(() -> {
            // Get the parent container width
            View parent = (View) tabSelector.getParent();
            if (parent == null) return;
            
            int containerWidth = parent.getWidth() - 8; // Account for padding
            int tabWidth = containerWidth / 3; // 3 tabs
            int targetX = 2; // Start with margin
            
            switch (selectedTab) {
                case ALL:
                    targetX = 2;
                    break;
                case GAINERS:
                    targetX = tabWidth + 2;
                    break;
                case LOSERS:
                    targetX = (tabWidth * 2) + 2;
                    break;
            }
            
            // Set selector width to match tab width
            tabSelector.getLayoutParams().width = tabWidth - 4;
            tabSelector.requestLayout();
            
            // Animate to target position
            ObjectAnimator animator = ObjectAnimator.ofFloat(tabSelector, "translationX", targetX);
            animator.setDuration(250);
            animator.start();
        });
    }

    private void loadUserBalance() {
        executorService.execute(() -> {
            try {
                if (databaseService.isUserLoggedIn()) {
                    User currentUser = databaseService.getCurrentUser();
                    double cashBalance = currentUser.getBalance();
                    
                    // Calculate portfolio value
                    double portfolioValue = calculatePortfolioValue();
                    double totalBalance = cashBalance + portfolioValue;
                    
                    // Calculate 24h change (simplified - using a mock calculation)
                    double changeAmount = totalBalance * 0.024; // Mock 2.4% change
                    double changePercent = 2.4;
                    
                    mainHandler.post(() -> updateBalanceUI(totalBalance, changeAmount, changePercent));
                } else {
                    mainHandler.post(() -> updateBalanceUI(0.0, 0.0, 0.0));
                }
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> updateBalanceUI(0.0, 0.0, 0.0));
            }
        });
    }
    
    private double calculatePortfolioValue() {
        try {
            if (!databaseService.isUserLoggedIn()) {
                return 0.0;
            }
            
            List<PortfolioItem> portfolio = databaseService.getUserPortfolio();
            if (portfolio == null || portfolio.isEmpty()) {
                return 0.0;
            }
            
            double totalValue = 0.0;
            List<CoinModel> currentCoins = coinRepository.getCoins();
            
            if (currentCoins != null) {
                for (PortfolioItem item : portfolio) {
                    // Find current price for this coin
                    for (CoinModel coin : currentCoins) {
                        if (coin.getId().equals(item.getCoinId())) {
                            totalValue += item.getQuantity() * coin.getCurrentPrice();
                            break;
                        }
                    }
                }
            }
            
            return totalValue;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
    
    private void updateBalanceUI(double totalBalance, double changeAmount, double changePercent) {
        if (txtTotalBalance != null) {
            txtTotalBalance.setText(String.format("$%.2f", totalBalance));
        }
        
        if (txtBalanceChange != null) {
            String changeText;
            int textColor;
            
            if (changeAmount > 0) {
                changeText = String.format("+$%.2f (+%.2f%%)", changeAmount, changePercent);
                textColor = ContextCompat.getColor(this, R.color.green_profit);
            } else if (changeAmount < 0) {
                changeText = String.format("$%.2f (%.2f%%)", changeAmount, changePercent);
                textColor = ContextCompat.getColor(this, R.color.red_loss);
            } else {
                changeText = "$0.00 (0.00%)";
                textColor = ContextCompat.getColor(this, R.color.text_secondary);
            }
            
            txtBalanceChange.setText(changeText);
            txtBalanceChange.setTextColor(textColor);
        }
    }

    private void loadAllCoins() {
        currentTab = TabType.ALL;
        fetchCoins(TabType.ALL);
    }

    private void loadGainers() {
        currentTab = TabType.GAINERS;
        fetchCoins(TabType.GAINERS);
    }

    private void loadLosers() {
        currentTab = TabType.LOSERS;
        fetchCoins(TabType.LOSERS);
    }

    private void fetchCoins(TabType type) {
        showLoading();
        
        executorService.execute(() -> {
            try {
                List<CoinModel> allCoins = coinRepository.getCoins();
                
                if (allCoins == null || allCoins.isEmpty()) {
                    mainHandler.post(() -> {
                        showError("No data available. Check your connection.");
                        coinAdapter.updateCoins(new ArrayList<>());
                    });
                    return;
                }
                
                List<CoinModel> result;
                switch (type) {
                    case GAINERS:
                        result = coinRepository.getTopGainersTab(allCoins);
                        break;
                    case LOSERS:
                        result = coinRepository.getTopLosersTab(allCoins);
                        break;
                    default:
                        result = coinRepository.getAllCoinsTab(allCoins);
                        break;
                }
                
                mainHandler.post(() -> {
                    allCoinsCache.clear();
                    allCoinsCache.addAll(allCoins);
                    coinAdapter.updateCoins(result);
                    showContent();
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> {
                    showError("Error loading data: " + e.getMessage());
                    coinAdapter.updateCoins(new ArrayList<>());
                });
            }
        });
    }

    private void filterCoins(String query) {
        if (allCoinsCache.isEmpty()) return;
        
        String searchText = query == null ? "" : query.trim().toLowerCase();
        
        List<CoinModel> baseList;
        switch (currentTab) {
            case GAINERS:
                baseList = coinRepository.getTopGainersTab(allCoinsCache);
                break;
            case LOSERS:
                baseList = coinRepository.getTopLosersTab(allCoinsCache);
                break;
            default:
                baseList = coinRepository.getAllCoinsTab(allCoinsCache);
                break;
        }
        
        List<CoinModel> filtered = new ArrayList<>();
        for (CoinModel coin : baseList) {
            String name = coin.getName() != null ? coin.getName().toLowerCase() : "";
            String symbol = coin.getSymbol() != null ? coin.getSymbol().toLowerCase() : "";
            
            if (name.contains(searchText) || symbol.contains(searchText)) {
                filtered.add(coin);
            }
        }
        
        coinAdapter.updateCoins(filtered);
        
        if (filtered.isEmpty()) {
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setText("No coins match your search.");
        } else {
            errorTextView.setVisibility(View.GONE);
        }
    }

    private void showLoading() {
        mainHandler.post(() -> {
            if (loadingProgressBar != null) loadingProgressBar.setVisibility(View.VISIBLE);
            if (errorTextView != null) errorTextView.setVisibility(View.GONE);
            if (coinRecyclerView != null) coinRecyclerView.setVisibility(View.GONE);
        });
    }

    private void showError(String message) {
        mainHandler.post(() -> {
            if (loadingProgressBar != null) loadingProgressBar.setVisibility(View.GONE);
            if (errorTextView != null) {
                errorTextView.setVisibility(View.VISIBLE);
                errorTextView.setText(message);
            }
            if (coinRecyclerView != null) coinRecyclerView.setVisibility(View.GONE);
        });
    }

    private void showContent() {
        mainHandler.post(() -> {
            if (loadingProgressBar != null) loadingProgressBar.setVisibility(View.GONE);
            if (errorTextView != null) errorTextView.setVisibility(View.GONE);
            if (coinRecyclerView != null) coinRecyclerView.setVisibility(View.VISIBLE);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh balance when returning to the activity
        loadUserBalance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
