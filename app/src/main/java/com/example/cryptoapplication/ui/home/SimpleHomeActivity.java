package com.example.cryptoapplication.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cryptoapplication.R;
import com.example.cryptoapplication.model.home.CoinModel;
import com.example.cryptoapplication.repository.CoinRepositoryRetrofit;
import com.example.cryptoapplication.ui.home.adapter.CoinAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleHomeActivity extends AppCompatActivity {

    private RecyclerView coinListView;
    private CircularProgressIndicator loadingSpinner;
    private TextView errorMessage;
    private SearchView searchBox;
    private MaterialButton allCoinsButton;
    private MaterialButton winnersButton;
    private MaterialButton losersButton;
    
    private CoinAdapter coinAdapter;
    private CoinRepositoryRetrofit coinRepository;
    private ExecutorService backgroundWorker;
    private Handler mainThreadHandler;
    
    private List<CoinModel> allCoinsData = new ArrayList<>();
    private String currentFilter = "ALL";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        setupTools();
        findUIComponents();
        setupCoinList();
        setupFilterButtons();
        setupSearchBox();
        loadAllCoins();
    }
    
    private void setupTools() {
        coinRepository = new CoinRepositoryRetrofit();
        backgroundWorker = Executors.newSingleThreadExecutor();
        mainThreadHandler = new Handler(Looper.getMainLooper());
    }
    
    private void findUIComponents() {
        coinListView = findViewById(R.id.coinListRecyclerView);
        loadingSpinner = findViewById(R.id.loadingProgressBar);
        errorMessage = findViewById(R.id.errorTextView);
        searchBox = findViewById(R.id.searchView);
        allCoinsButton = findViewById(R.id.btnAllCoins);
        winnersButton = findViewById(R.id.btnTopGainers);
        losersButton = findViewById(R.id.btnTopLosers);
    }
    
    private void setupCoinList() {
        coinAdapter = new CoinAdapter(new ArrayList<>());
        coinListView.setLayoutManager(new LinearLayoutManager(this));
        coinListView.setAdapter(coinAdapter);
        
        coinAdapter.setOnCoinClickListener(coin -> {
            Intent detailIntent = new Intent(SimpleHomeActivity.this, 
                com.example.cryptoapplication.ui.detail.CoinDetailActivity.class);
            detailIntent.putExtra("coin_id", coin.getId());
            detailIntent.putExtra("coin_symbol", coin.getSymbol());
            detailIntent.putExtra("coin_name", coin.getName());
            detailIntent.putExtra("coin_price", coin.getCurrentPrice());
            detailIntent.putExtra("coin_image", coin.getImage());
            detailIntent.putExtra("coin_change_24h", coin.getPriceChangePercentage24h());
            startActivity(detailIntent);
        });
    }
    
    private void setupFilterButtons() {
        allCoinsButton.setOnClickListener(view -> {
            currentFilter = "ALL";
            showFilteredCoins();
        });
        
        winnersButton.setOnClickListener(view -> {
            currentFilter = "WINNERS";
            showFilteredCoins();
        });
        
        losersButton.setOnClickListener(view -> {
            currentFilter = "LOSERS";
            showFilteredCoins();
        });
    }
    
    private void setupSearchBox() {
        if (searchBox == null) return;
        
        searchBox.setIconifiedByDefault(false);
        searchBox.clearFocus();
        
        searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchForCoins(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchForCoins(newText);
                return true;
            }
        });
    }
    
    private void loadAllCoins() {
        showLoadingState();
        
        backgroundWorker.execute(() -> {
            try {
                List<CoinModel> coins = coinRepository.getCoins();
                
                mainThreadHandler.post(() -> {
                    if (coins != null && !coins.isEmpty()) {
                        allCoinsData.clear();
                        allCoinsData.addAll(coins);
                        showFilteredCoins();
                        showContentState();
                    } else {
                        showErrorState("No cryptocurrency data available. Please check your internet connection.");
                    }
                });
                
            } catch (Exception error) {
                mainThreadHandler.post(() -> {
                    showErrorState("Error loading data: " + error.getMessage());
                });
            }
        });
    }
    
    private void showFilteredCoins() {
        if (allCoinsData.isEmpty()) return;
        
        List<CoinModel> coinsToShow;
        
        switch (currentFilter) {
            case "WINNERS":
                coinsToShow = coinRepository.getTopGainersTab(allCoinsData);
                break;
            case "LOSERS":
                coinsToShow = coinRepository.getTopLosersTab(allCoinsData);
                break;
            default:
                coinsToShow = coinRepository.getAllCoinsTab(allCoinsData);
                break;
        }
        
        coinAdapter.updateCoins(coinsToShow);
    }
    
    private void searchForCoins(String searchText) {
        if (allCoinsData.isEmpty()) return;
        
        String cleanSearchText = searchText == null ? "" : searchText.trim().toLowerCase();
        
        List<CoinModel> baseCoins;
        switch (currentFilter) {
            case "WINNERS":
                baseCoins = coinRepository.getTopGainersTab(allCoinsData);
                break;
            case "LOSERS":
                baseCoins = coinRepository.getTopLosersTab(allCoinsData);
                break;
            default:
                baseCoins = coinRepository.getAllCoinsTab(allCoinsData);
                break;
        }
        
        List<CoinModel> matchingCoins = new ArrayList<>();
        for (CoinModel coin : baseCoins) {
            String coinName = coin.getName() != null ? coin.getName().toLowerCase() : "";
            String coinSymbol = coin.getSymbol() != null ? coin.getSymbol().toLowerCase() : "";
            
            if (coinName.contains(cleanSearchText) || coinSymbol.contains(cleanSearchText)) {
                matchingCoins.add(coin);
            }
        }
        
        coinAdapter.updateCoins(matchingCoins);
        
        if (matchingCoins.isEmpty() && !cleanSearchText.isEmpty()) {
            showErrorState("No coins match your search for '" + searchText + "'");
        } else if (!matchingCoins.isEmpty()) {
            showContentState();
        }
    }
    
    private void showLoadingState() {
        if (loadingSpinner != null) loadingSpinner.setVisibility(View.VISIBLE);
        if (errorMessage != null) errorMessage.setVisibility(View.GONE);
        if (coinListView != null) coinListView.setVisibility(View.GONE);
    }
    
    private void showErrorState(String message) {
        if (loadingSpinner != null) loadingSpinner.setVisibility(View.GONE);
        if (errorMessage != null) {
            errorMessage.setVisibility(View.VISIBLE);
            errorMessage.setText(message);
        }
        if (coinListView != null) coinListView.setVisibility(View.GONE);
    }
    
    private void showContentState() {
        if (loadingSpinner != null) loadingSpinner.setVisibility(View.GONE);
        if (errorMessage != null) errorMessage.setVisibility(View.GONE);
        if (coinListView != null) coinListView.setVisibility(View.VISIBLE);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        if (backgroundWorker != null && !backgroundWorker.isShutdown()) {
            backgroundWorker.shutdown();
        }
    }
}