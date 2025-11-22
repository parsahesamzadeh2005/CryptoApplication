package com.example.cryptoapplication.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptoapplication.R;
import com.example.cryptoapplication.database.DatabaseService;
import com.example.cryptoapplication.models.CoinModel;
import com.example.cryptoapplication.models.PortfolioItem;

import java.util.List;
import java.util.Locale;

public class WalletAssetsAdapter extends RecyclerView.Adapter<WalletAssetsAdapter.ViewHolder> {

    private final List<PortfolioItem> items;
    private final DatabaseService databaseService;

    public WalletAssetsAdapter(List<PortfolioItem> items, DatabaseService databaseService) {
        this.items = items;
        this.databaseService = databaseService;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet_asset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PortfolioItem item = items.get(position);
        String displayName = item.getCoinId();
        if (databaseService != null && item.getCoinId() != null) {
            CoinModel coin = databaseService.getCoinById(item.getCoinId());
            if (coin != null && coin.getName() != null) {
                displayName = coin.getName();
            }
        }
        holder.txtAssetName.setText(displayName);
        holder.txtAssetQuantity.setText(String.format(Locale.getDefault(), "%.6f", item.getQuantity()));
        holder.txtAssetPrice.setText(String.format(Locale.getDefault(), "$%.2f", item.getCurrentPrice()));
        holder.txtAssetValue.setText(String.format(Locale.getDefault(), "$%.2f", item.getTotalValue()));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void updateItems(List<PortfolioItem> newItems) {
        if (items != null) {
            items.clear();
            if (newItems != null) items.addAll(newItems);
            notifyDataSetChanged();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtAssetName, txtAssetQuantity, txtAssetPrice, txtAssetValue;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAssetName = itemView.findViewById(R.id.txtAssetName);
            txtAssetQuantity = itemView.findViewById(R.id.txtAssetQuantity);
            txtAssetPrice = itemView.findViewById(R.id.txtAssetPrice);
            txtAssetValue = itemView.findViewById(R.id.txtAssetValue);
        }
    }
}