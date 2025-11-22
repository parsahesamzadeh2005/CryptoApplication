package com.example.cryptoapplication.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.example.cryptoapplication.repository.CoinRepository;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cryptoapplication.R;
import com.example.cryptoapplication.model.home.CoinModel;
import com.example.cryptoapplication.ui.profile.ProfileActivity;
import com.example.cryptoapplication.ui.home.adapter.CoinAdapter;
import com.example.cryptoapplication.ui.home.adapter.ImageSliderAdapter;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

    private ViewPager2 imageSlider;
    private LinearLayout indicatorLayout;
    private RecyclerView coinRecyclerView;
    private List<Integer> imageList = new ArrayList<>();
    private List<ImageView> indicatorDots = new ArrayList<>();
    private CoinAdapter coinAdapter;
    private Button btnAll;
    private Button btnGainers;
    private Button btnLosers;
    private CoinRepository coinRepository;

    private enum CoinTabType {
        ALL, GAINERS, LOSERS
    }

    private class FetchCoinsTask extends AsyncTask<Void, Void, List<CoinModel>> {
        private CoinTabType tabType;

        public FetchCoinsTask(CoinTabType tabType) {
            this.tabType = tabType;
        }

        @Override
        protected List<CoinModel> doInBackground(Void... voids) {
            List<CoinModel> all = coinRepository.getCoins();
            switch (tabType) {
                case GAINERS:
                    return coinRepository.getTopGainersTab(all);
                case LOSERS:
                    return coinRepository.getTopLosersTab(all);
                default:
                    return coinRepository.getAllCoinsTab(all);
            }
        }

        @Override
        protected void onPostExecute(List<CoinModel> result) {
            coinAdapter.updateData(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        coinRepository = new CoinRepository();

        imageSlider = findViewById(R.id.imageSlider);
        indicatorLayout = findViewById(R.id.indicatorLayout);
        coinRecyclerView = findViewById(R.id.coinListRecyclerView);
        btnAll = findViewById(R.id.btnAllCoins);
        btnGainers = findViewById(R.id.btnTopGainers);
        btnLosers = findViewById(R.id.btnTopLosers);

        coinAdapter = new CoinAdapter(new ArrayList<>());
        coinRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        coinRecyclerView.setAdapter(coinAdapter);

        View.OnClickListener tabClickListener = view -> {
            resetTabStyles();

            Button clicked = (Button) view;
            clicked.setBackgroundResource(R.drawable.tab_selected_bg);
            clicked.setTextColor(Color.WHITE);

            if (view == btnAll) {
                loadAllCoins();
            } else if (view == btnGainers) {
                loadGainers();
            } else if (view == btnLosers) {
                loadLosers();
            }
        };

        btnAll.setOnClickListener(tabClickListener);
        btnGainers.setOnClickListener(tabClickListener);
        btnLosers.setOnClickListener(tabClickListener);

        setupImageSlider();
        btnAll.performClick();

        Button profileButton = findViewById(R.id.btnProfile);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_right);
            finish();
        });
    }

    private void resetTabStyles() {
        btnAll.setBackgroundResource(R.drawable.tab_unselected_bg);
        btnAll.setTextColor(Color.parseColor("#450BAC"));

        btnGainers.setBackgroundResource(R.drawable.tab_unselected_bg);
        btnGainers.setTextColor(Color.parseColor("#450BAC"));

        btnLosers.setBackgroundResource(R.drawable.tab_unselected_bg);
        btnLosers.setTextColor(Color.parseColor("#450BAC"));
    }

    private void setupImageSlider() {
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
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });
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

    private void loadAllCoins() {
        new FetchCoinsTask(CoinTabType.ALL).execute();
    }

    private void loadGainers() {
        new FetchCoinsTask(CoinTabType.GAINERS).execute();
    }

    private void loadLosers() {
        new FetchCoinsTask(CoinTabType.LOSERS).execute();
    }
}