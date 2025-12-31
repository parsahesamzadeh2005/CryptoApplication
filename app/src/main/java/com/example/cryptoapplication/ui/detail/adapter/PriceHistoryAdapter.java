package com.example.cryptoapplication.ui.detail.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptoapplication.R;

import java.text.DecimalFormat;
import java.util.List;

public class PriceHistoryAdapter extends RecyclerView.Adapter<PriceHistoryAdapter.PriceViewHolder> {

    private List<PriceEntry> priceEntries;
    private final DecimalFormat priceFormat = new DecimalFormat("#,##0.00");
    private final DecimalFormat quantityFormat = new DecimalFormat("0.0000");

    public PriceHistoryAdapter(List<PriceEntry> priceEntries) {
        this.priceEntries = priceEntries;
    }

    @NonNull
    @Override
    public PriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_price_history, parent, false);
        return new PriceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceViewHolder holder, int position) {
        PriceEntry entry = priceEntries.get(position);
        
        holder.tvPrice.setText(priceFormat.format(entry.price));
        holder.tvQuantity.setText(quantityFormat.format(entry.quantity));
        
        // Set color based on price movement
        int textColor = entry.isPositive ? 
            ContextCompat.getColor(holder.itemView.getContext(), R.color.green_profit) :
            ContextCompat.getColor(holder.itemView.getContext(), R.color.red_loss);
        
        holder.tvPrice.setTextColor(textColor);
    }

    @Override
    public int getItemCount() {
        return priceEntries != null ? priceEntries.size() : 0;
    }

    public void updatePrices(List<PriceEntry> newPrices) {
        this.priceEntries = newPrices;
        notifyDataSetChanged();
    }

    static class PriceViewHolder extends RecyclerView.ViewHolder {
        TextView tvPrice, tvQuantity;

        PriceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }

    public static class PriceEntry {
        public final double price;
        public final double quantity;
        public final boolean isPositive;

        public PriceEntry(double price, double quantity, boolean isPositive) {
            this.price = price;
            this.quantity = quantity;
            this.isPositive = isPositive;
        }
    }
}