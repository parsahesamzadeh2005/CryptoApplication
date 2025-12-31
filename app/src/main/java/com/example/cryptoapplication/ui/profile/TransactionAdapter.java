package com.example.cryptoapplication.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptoapplication.R;
import com.example.cryptoapplication.models.PortfolioItem;
import com.example.cryptoapplication.database.SimpleDatabaseService;
import com.example.cryptoapplication.models.CoinModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final List<PortfolioItem> items;
    private final SimpleDatabaseService databaseService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    public TransactionAdapter(List<PortfolioItem> items, SimpleDatabaseService databaseService) {
        this.items = items;
        this.databaseService = databaseService;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PortfolioItem item = items.get(position);
        
        // Get coin name
        String displayName = item.getCoinId();
        if (item.getCoinId() != null && !item.getCoinId().isEmpty()) {
            CoinModel coin = databaseService.findCoinById(item.getCoinId());
            if (coin != null && coin.getName() != null) {
                displayName = coin.getName();
            } else {
                displayName = item.getCoinId().toUpperCase();
            }
        }
        
        holder.txtCoinName.setText(displayName);
        holder.txtCoinSymbol.setText(item.getCoinId() != null ? item.getCoinId().toUpperCase() : "");
        
        // Set transaction type with color
        String transactionType = item.getTransactionType();
        holder.txtType.setText(transactionType);
        
        if ("BUY".equals(transactionType)) {
            holder.txtType.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green_profit));
        } else if ("SELL".equals(transactionType)) {
            holder.txtType.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red_loss));
        }
        
        // Set quantity and price
        holder.txtQuantity.setText(String.format(Locale.getDefault(), "%.8f", item.getQuantity()));
        holder.txtPrice.setText(String.format(Locale.getDefault(), "$%.2f", item.getPurchasePrice()));
        
        // Set total value
        double totalValue = item.getQuantity() * item.getPurchasePrice();
        holder.txtTotalValue.setText(String.format(Locale.getDefault(), "$%.2f", totalValue));
        
        // Set date
        holder.txtDate.setText(dateFormat.format(new java.util.Date(item.getPurchaseDate())));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCoinName, txtCoinSymbol, txtType, txtQuantity, txtPrice, txtTotalValue, txtDate;
        
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCoinName = itemView.findViewById(R.id.txtCoinName);
            txtCoinSymbol = itemView.findViewById(R.id.txtCoinSymbol);
            txtType = itemView.findViewById(R.id.txtType);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtTotalValue = itemView.findViewById(R.id.txtTotalValue);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }
}