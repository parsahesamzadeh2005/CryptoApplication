package com.example.cryptoapplication.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class CoinGeckoService {
    private static final String BASE_URL = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&x_cg_demo_api_key=CG-1JxNfXW9es36Ub4NAA68YXJq";

    public String fetchCoinsJson() throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .get()
                .addHeader("accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                return response.body().string();
            } else {
                throw new IOException("Empty response body");
            }
        }
    }
}
