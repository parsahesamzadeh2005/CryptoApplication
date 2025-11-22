package com.example.cryptoapplication.repository;

import com.example.cryptoapplication.model.home.CoinModel;
import com.example.cryptoapplication.network.CoinGeckoService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CoinRepository {
    private CoinGeckoService service = new CoinGeckoService();

    public List<CoinModel> getCoins() {
        List<CoinModel> coins = new ArrayList<>();
        try {
            String json = service.fetchCoinsJson();
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                coins.add(new CoinModel(
                        obj.getString("id"),
                        obj.getString("symbol"),
                        obj.getString("name"),
                        String.valueOf(obj.getDouble("current_price")),
                        obj.getString("image")
                ));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace(); // Handle more gracefully later
        }

        return coins;
    }

    public List<CoinModel> getAllCoinsTab(List<CoinModel> all) {
        return all.subList(0, Math.min(all.size(), 10));
    }

    public List<CoinModel> getTopGainersTab(List<CoinModel> all) {
        return all.subList(10, Math.min(all.size(), 20));
    }

    public List<CoinModel> getTopLosersTab(List<CoinModel> all) {
        return all.subList(20, Math.min(all.size(), 30));
    }
}