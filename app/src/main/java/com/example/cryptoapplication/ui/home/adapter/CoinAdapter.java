package com.example.cryptoapplication.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // Import ImageView
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Import Glide
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

    // Backward-compatible method name used by HomeActivity
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
            holder.coinPrice.setText(coin.getPrice());


            Glide.with(holder.itemView.getContext())
                    .load(coin.getImage())
                    .placeholder(R.drawable.placeholder_foreground)
                    .error(R.drawable.error_placeholder_foreground)
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
        TextView coinName, coinPrice;
        ImageView coinImage;

        public CoinViewHolder(@NonNull View itemView) {
            super(itemView);
            coinName = itemView.findViewById(R.id.coinName);
            coinPrice = itemView.findViewById(R.id.coinPrice);
            coinImage = itemView.findViewById(R.id.coinImage);

        }
    }

    public interface OnCoinClickListener {
        void onCoinClick(CoinModel coin);
    }
}