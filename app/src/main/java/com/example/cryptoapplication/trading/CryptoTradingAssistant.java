package com.example.cryptoapplication.trading;

import android.content.Context;
import com.example.cryptoapplication.database.SimpleDatabaseService;
import com.example.cryptoapplication.models.CoinModel;
import com.example.cryptoapplication.models.User;
import com.example.cryptoapplication.repository.CoinRepositoryRetrofit;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CryptoTradingAssistant {
    
    private final SimpleDatabaseService databaseService;
    private final CoinRepositoryRetrofit coinRepository;
    private final ExecutorService executorService;
    private TradingCallback callback;
    
    public interface TradingCallback {
        void onTradingResult(TradingResult result);
        void onPriceCheckComplete(CoinModel coin);
        void onError(String error);
    }
    
    public static class TradingResult {
        public final boolean success;
        public final String message;
        public final String coinId;
        public final double coinAmount;
        public final double pricePerCoin;
        public final double totalCost;
        public final String operation;
        public final double newBalance;
        
        public TradingResult(boolean success, String message, String coinId, 
                           double coinAmount, double pricePerCoin, double totalCost, 
                           String operation, double newBalance) {
            this.success = success;
            this.message = message;
            this.coinId = coinId;
            this.coinAmount = coinAmount;
            this.pricePerCoin = pricePerCoin;
            this.totalCost = totalCost;
            this.operation = operation;
            this.newBalance = newBalance;
        }
    }
    
    public CryptoTradingAssistant(Context context) {
        this.databaseService = SimpleDatabaseService.getInstance(context);
        this.coinRepository = new CoinRepositoryRetrofit();
        this.executorService = Executors.newSingleThreadExecutor();
    }
    
    public void setTradingCallback(TradingCallback callback) {
        this.callback = callback;
    }
    
    public void processTradingCommand(String command) {
        executorService.execute(() -> {
            try {
                TradeCommand tradeCommand = parseCommand(command);
                if (tradeCommand == null) {
                    notifyError("Invalid command format. Use: 'Buy [amount] [coin]' or 'Sell [amount] [coin]'");
                    return;
                }
                
                executeTrade(tradeCommand);
                
            } catch (Exception e) {
                notifyError("Error processing command: " + e.getMessage());
            }
        });
    }
    
    public void buyCoin(String coinSymbol, double coinAmount) {
        String command = String.format("Buy %.8f %s", coinAmount, coinSymbol);
        processTradingCommand(command);
    }
    
    public void sellCoin(String coinSymbol, double coinAmount) {
        String command = String.format("Sell %.8f %s", coinAmount, coinSymbol);
        processTradingCommand(command);
    }
    
    private TradeCommand parseCommand(String command) {
        String[] parts = command.trim().split("\\s+");
        if (parts.length != 3) return null;
        
        String operation = parts[0].toLowerCase();
        if (!operation.equals("buy") && !operation.equals("sell")) return null;
        
        try {
            double amount = Double.parseDouble(parts[1]);
            String coinSymbol = parts[2].toUpperCase();
            
            if (amount <= 0) return null;
            
            return new TradeCommand(operation, amount, coinSymbol);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private void executeTrade(TradeCommand command) {
        // Step 1: Check if user is logged in
        if (!databaseService.isUserLoggedIn()) {
            notifyError("Please log in to trade cryptocurrencies");
            return;
        }
        
        // Step 2: Get current coin price
        CoinModel coin = getCurrentCoinPrice(command.coinSymbol);
        if (coin == null) {
            notifyError("Could not fetch current price for " + command.coinSymbol);
            return;
        }
        
        // Step 3: Execute buy or sell operation
        if (command.operation.equals("buy")) {
            executeBuyOrder(command, coin);
        } else {
            executeSellOrder(command, coin);
        }
    }
    
    private void executeBuyOrder(TradeCommand command, CoinModel coin) {
        User currentUser = databaseService.getCurrentUser();
        double pricePerCoin = coin.getCurrentPrice();
        double totalCost = command.amount * pricePerCoin;
        
        // Check if user has enough balance
        if (currentUser.getBalance() < totalCost) {
            String message = String.format("Not enough balance to buy %.8f %s. Required: $%.2f, Available: $%.2f", 
                                         command.amount, command.coinSymbol, totalCost, currentUser.getBalance());
            notifyResult(new TradingResult(false, message, coin.getId(), command.amount, 
                                         pricePerCoin, totalCost, "BUY", currentUser.getBalance()));
            return;
        }
        
        // Fixed: Pass coin quantity (command.amount) instead of totalCost
        boolean success = databaseService.buyCryptocurrency(coin.getId(), command.amount, pricePerCoin);
        
        if (success) {
            User updatedUser = databaseService.getCurrentUser();
            String message = String.format("Successfully bought %.8f %s at $%.2f each. Total cost: $%.2f", 
                                         command.amount, command.coinSymbol, pricePerCoin, totalCost);
            notifyResult(new TradingResult(true, message, coin.getId(), command.amount, 
                                         pricePerCoin, totalCost, "BUY", updatedUser.getBalance()));
        } else {
            notifyResult(new TradingResult(false, "Failed to execute buy order", coin.getId(), 
                                         command.amount, pricePerCoin, totalCost, "BUY", currentUser.getBalance()));
        }
    }
    
    private void executeSellOrder(TradeCommand command, CoinModel coin) {
        double pricePerCoin = coin.getCurrentPrice();
        double totalProceeds = command.amount * pricePerCoin;
        
        // Fixed: Use the new sellCryptocurrency method that handles coin quantities properly
        boolean success = databaseService.sellCryptocurrency(coin.getId(), command.amount, pricePerCoin);
        
        if (success) {
            User updatedUser = databaseService.getCurrentUser();
            String message = String.format("Successfully sold %.8f %s at $%.2f each. Total proceeds: $%.2f", 
                                         command.amount, command.coinSymbol, pricePerCoin, totalProceeds);
            notifyResult(new TradingResult(true, message, coin.getId(), command.amount, 
                                         pricePerCoin, totalProceeds, "SELL", updatedUser.getBalance()));
        } else {
            User currentUser = databaseService.getCurrentUser();
            notifyResult(new TradingResult(false, "Failed to execute sell order", coin.getId(), 
                                         command.amount, pricePerCoin, totalProceeds, "SELL", currentUser.getBalance()));
        }
    }
    
    private CoinModel getCurrentCoinPrice(String coinSymbol) {
        try {
            List<CoinModel> coins = coinRepository.getCoins();
            if (coins != null) {
                for (CoinModel coin : coins) {
                    if (coin.getSymbol().equalsIgnoreCase(coinSymbol)) {
                        return coin;
                    }
                }
            }
        } catch (Exception e) {
            // Try to get from local cache
            List<CoinModel> cachedCoins = databaseService.getAllCoins();
            for (CoinModel coin : cachedCoins) {
                if (coin.getSymbol().equalsIgnoreCase(coinSymbol)) {
                    return coin;
                }
            }
        }
        return null;
    }
    
    private void notifyResult(TradingResult result) {
        if (callback != null) {
            callback.onTradingResult(result);
        }
    }
    
    private void notifyError(String error) {
        if (callback != null) {
            callback.onError(error);
        }
    }
    
    public void getCurrentBalance() {
        if (databaseService.isUserLoggedIn()) {
            User user = databaseService.getCurrentUser();
            String message = String.format("Current balance: $%.2f", user.getBalance());
            notifyResult(new TradingResult(true, message, "", 0, 0, 0, "BALANCE", user.getBalance()));
        } else {
            notifyError("Please log in to check balance");
        }
    }
    
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    private static class TradeCommand {
        final String operation;
        final double amount;
        final String coinSymbol;
        
        TradeCommand(String operation, double amount, String coinSymbol) {
            this.operation = operation;
            this.amount = amount;
            this.coinSymbol = coinSymbol;
        }
    }
}