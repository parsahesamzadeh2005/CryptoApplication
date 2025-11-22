package com.example.cryptoapplication.network;

import com.example.cryptoapplication.model.home.CoinModel;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit interface for CoinGecko API
 * Provides type-safe API calls for cryptocurrency data
 */
public interface CoinGeckoApi {
    
    /**
     * Get cryptocurrency market data
     * @param vsCurrency The target currency of market data (usd, eur, jpy, etc.)
     * @param order Sort results by field (market_cap_desc, gecko_desc, gecko_asc, market_cap_asc, market_cap_desc, volume_asc, volume_desc, id_asc, id_desc)
     * @param perPage Total results per page (valid values: 1...250)
     * @param page Page through results
     * @param sparkline Include sparkline 7 days data (true/false)
     * @return Call object for cryptocurrency market data
     */
    @GET("coins/markets")
    Call<List<CoinModel>> getCoinMarkets(
        @Query("vs_currency") String vsCurrency,
        @Query("order") String order,
        @Query("per_page") int perPage,
        @Query("page") int page,
        @Query("sparkline") boolean sparkline
    );
    
    /**
     * Simplified method to get top cryptocurrencies by market cap
     * @param vsCurrency The target currency (default: usd)
     * @param limit Number of results (default: 10)
     * @return Call object for top cryptocurrencies
     */
    @GET("coins/markets")
    Call<List<CoinModel>> getTopCoins(
        @Query("vs_currency") String vsCurrency,
        @Query("per_page") int limit,
        @Query("order") String order
    );
}