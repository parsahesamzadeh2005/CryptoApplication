package com.example.cryptoapplication.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptoapplication.R;
import com.example.cryptoapplication.models.ConsolidatedAsset;

import java.text.DecimalFormat;
import java.util.List;

public class ConsolidatedAssetsAdapter extends RecyclerView.Adapter<ConsolidatedAssetsAdapter.ViewHolder> {

    public interface OnAssetClickListener {
        void onAssetClick(ConsolidatedAsset asset);
    }

    private final List<ConsolidatedAsset> assets;
    private final DecimalFormat cryptoFormat = new DecimalFormat("#,##0.########");
    private OnAssetClickListener listener;

    public ConsolidatedAssetsAdapter(List<ConsolidatedAsset> assets) {
        this.assets = assets;
    }

    public void setOnAssetClickListener(OnAssetClickListener l) {
        this.listener = l;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consolidated_asset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConsolidatedAsset asset = assets.get(position);
        
        // Coin info
        holder.txtCoinName.setText(asset.getCoinName());
        holder.txtCoinSymbol.setText(asset.getCoinSymbol());
        
        // Amount only
        holder.txtQuantity.setText(cryptoFormat.format(asset.getTotalQuantity()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onAssetClick(asset);
        });
    }

    @Override
    public int getItemCount() {
        return assets != null ? assets.size() : 0;
    }

    public void updateAssets(List<ConsolidatedAsset> newAssets) {
        if (assets != null) {
            assets.clear();
            if (newAssets != null) assets.addAll(newAssets);
            notifyDataSetChanged();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCoinName, txtCoinSymbol, txtQuantity;
        
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCoinName = itemView.findViewById(R.id.txtCoinName);
            txtCoinSymbol = itemView.findViewById(R.id.txtCoinSymbol);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
        }
    }
}