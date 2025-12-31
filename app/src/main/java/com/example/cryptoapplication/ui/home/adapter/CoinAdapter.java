package com.example.cryptoapplication.ui.home.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cryptoapplication.R;
import com.example.cryptoapplication.model.home.CoinModel;

import java.util.List;

public class CoinAdapter extends RecyclerView.Adapter<CoinAdapter.CoinViewHolder> {

    private List<CoinModel> coinList;
    private OnCoinClickListener onCoinClickListener;

    public CoinAdapter(List<CoinModel> coinList) {
        this.coinList = coinList;
    }

    @NonNull
    @Override
    public CoinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_coin, parent, false);
        return new CoinViewHolder(view);
    }

    public void updateData(List<CoinModel> newData) {
        if (this.coinList != null && newData != null) {
            this.coinList.clear();
            this.coinList.addAll(newData);
            notifyDataSetChanged();
        }
    }

    public void updateCoins(List<CoinModel> newData) {
        updateData(newData);
    }

    public void setOnCoinClickListener(OnCoinClickListener listener) {
        this.onCoinClickListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull CoinViewHolder holder, int position) {
        CoinModel coin = coinList.get(position);
        if (coin != null) {
            holder.coinName.setText(coin.getName());
            holder.coinSymbol.setText(coin.getSymbol().toUpperCase());
            
            // Format price
            holder.coinPrice.setText(String.format("$%.2f", coin.getCurrentPrice()));
            
            // Format and color the price change
            double priceChange = coin.getPriceChangePercentage24h();
            String changeText = String.format("%+.2f%%", priceChange);
            holder.coinChange.setText(changeText);
            
            // Set color based on positive/negative change
            if (priceChange >= 0) {
                holder.coinChange.setTextColor(Color.parseColor("#00D4AA")); // green_profit
            } else {
                holder.coinChange.setTextColor(Color.parseColor("#FF6B6B")); // red_loss
            }

            // Load coin image
            Glide.with(holder.itemView.getContext())
                    .load(coin.getImage())
                    .placeholder(R.drawable.mianlogo)
                    .error(R.drawable.mianlogo)
                    .into(holder.coinImage);

            holder.itemView.setOnClickListener(v -> {
                if (onCoinClickListener != null) {
                    onCoinClickListener.onCoinClick(coin);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return coinList != null ? coinList.size() : 0;
    }

    public static class CoinViewHolder extends RecyclerView.ViewHolder {
        TextView coinName, coinSymbol, coinPrice, coinChange;
        ImageView coinImage;

        public CoinViewHolder(@NonNull View itemView) {
            super(itemView);
            coinName = itemView.findViewById(R.id.coinName);
            coinSymbol = itemView.findViewById(R.id.coinSymbol);
            coinPrice = itemView.findViewById(R.id.coinPrice);
            coinChange = itemView.findViewById(R.id.coinChange);
            coinImage = itemView.findViewById(R.id.coinImage);
        }
    }

    public interface OnCoinClickListener {
        void onCoinClick(CoinModel coin);
    }
}