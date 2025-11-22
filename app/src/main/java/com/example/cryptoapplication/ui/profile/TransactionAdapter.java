package com.example.cryptoapplication.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptoapplication.R;
import com.example.cryptoapplication.models.TransactionRecord;
import com.example.cryptoapplication.database.DatabaseService;
import com.example.cryptoapplication.models.CoinModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final List<TransactionRecord> items;
    private final DatabaseService databaseService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public TransactionAdapter(List<TransactionRecord> items, DatabaseService databaseService) {
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
        TransactionRecord item = items.get(position);
        String displayName = item.getCoinId();
        if (item.getCoinId() == null || item.getCoinId().isEmpty()) {
            displayName = "Fiat";
        } else if (databaseService != null) {
            CoinModel coin = databaseService.getCoinById(item.getCoinId());
            if (coin != null && coin.getName() != null) {
                displayName = coin.getName();
            }
        }
        holder.txtCoinId.setText(displayName);
        holder.txtType.setText(item.getType());

        if ("WITHDRAW".equalsIgnoreCase(item.getType()) || "DEPOSIT".equalsIgnoreCase(item.getType())) {
            holder.txtQuantity.setText(String.format(Locale.getDefault(), "$%.2f", item.getFiatAmount()));
            holder.txtPrice.setText("—");
        } else {
            holder.txtQuantity.setText(String.format(Locale.getDefault(), "%.6f", item.getQuantity()));
            holder.txtPrice.setText(String.format(Locale.getDefault(), "$%.2f", item.getPricePerCoin()));
        }
        holder.txtDate.setText(dateFormat.format(new java.util.Date(item.getTimestamp())));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCoinId, txtType, txtQuantity, txtPrice, txtDate;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCoinId = itemView.findViewById(R.id.txtCoinId);
            txtType = itemView.findViewById(R.id.txtType);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }
}