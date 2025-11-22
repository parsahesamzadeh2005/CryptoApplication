package com.example.cryptoapplication.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cryptoapplication.R;
import com.example.cryptoapplication.model.home.CoinModel;
import com.example.cryptoapplication.repository.CoinRepositoryRetrofit;
import com.example.cryptoapplication.ui.home.adapter.CoinAdapter;
import com.example.cryptoapplication.ui.home.adapter.ImageSliderAdapter;
import com.example.cryptoapplication.ui.profile.ProfileActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {

    // UI Components
    private ViewPager2 imageSlider;
    private LinearLayout indicatorLayout;
    private RecyclerView coinRecyclerView;
    private CircularProgressIndicator loadingProgressBar;
    private TextView errorTextView;
    private SearchView searchView;
    private MaterialButton btnAll, btnGainers, btnLosers, btnProfile;

    // Data
    private CoinAdapter coinAdapter;
    private CoinRepositoryRetrofit coinRepository;
    private ExecutorService executorService;
    private Handler mainHandler;
    
    private List<CoinModel> allCoinsCache = new ArrayList<>();
    private List<Integer> imageList = new ArrayList<>();
    private List<ImageView> indicatorDots = new ArrayList<>();
    
    private enum TabType { ALL, GAINERS, LOSERS }
    private TabType currentTab = TabType.ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        // Initialize services first
        coinRepository = new CoinRepositoryRetrofit();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        
        // Initialize views
        initViews();
        
        // Setup components
        setupRecyclerView();
        setupImageSlider();
        setupTabs();
        setupSearch();
        setupProfileButton();
        
        // Load initial data
        loadAllCoins();
    }

    private void initViews() {
        imageSlider = findViewById(R.id.imageSlider);
        indicatorLayout = findViewById(R.id.indicatorLayout);
        coinRecyclerView = findViewById(R.id.coinListRecyclerView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        errorTextView = findViewById(R.id.errorTextView);
        searchView = findViewById(R.id.searchView);
        btnAll = findViewById(R.id.btnAllCoins);
        btnGainers = findViewById(R.id.btnTopGainers);
        btnLosers = findViewById(R.id.btnTopLosers);
        btnProfile = findViewById(R.id.btnProfile);
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

    private void setupImageSlider() {
        try {
            imageList.add(R.drawable.slider1);
            imageList.add(R.drawable.slider2);
            imageList.add(R.drawable.slider3);

            ImageSliderAdapter adapter = new ImageSliderAdapter(imageList);
            imageSlider.setAdapter(adapter);

            setupIndicators(imageList.size());
            setCurrentIndicator(0);

            imageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    setCurrentIndicator(position);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupIndicators(int count) {
        indicatorDots.clear();
        indicatorLayout.removeAllViews();

        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageResource(R.drawable.indicator_inactive);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            indicatorLayout.addView(dot);
            indicatorDots.add(dot);
        }
    }

    private void setCurrentIndicator(int index) {
        for (int i = 0; i < indicatorDots.size(); i++) {
            indicatorDots.get(i).setImageResource(
                i == index ? R.drawable.indicator_active : R.drawable.indicator_inactive
            );
        }
    }

    private void setupTabs() {
        View.OnClickListener tabListener = view -> {
            if (view.getId() == R.id.btnAllCoins) {
                loadAllCoins();
            } else if (view.getId() == R.id.btnTopGainers) {
                loadGainers();
            } else if (view.getId() == R.id.btnTopLosers) {
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
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
