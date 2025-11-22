package com.example.cryptoapplication.network;

import com.example.cryptoapplication.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Factory class for creating Retrofit instances
 * Provides configured Retrofit client with logging and API key handling
 */
public class RetrofitClient {
    
    private static final String BASE_URL = "https://api.coingecko.com/api/v3/";
    private static Retrofit retrofit = null;
    
    /**
     * Get configured Retrofit instance with logging and API key interceptor
     * @return Configured Retrofit instance
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            
            // Create logging interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            
            // Create OkHttp client with logging and API key
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);
            httpClient.addInterceptor(chain -> {
                okhttp3.Request original = chain.request();
                okhttp3.Request request = original.newBuilder()
                    .header("x-cg-demo-api-key", BuildConfig.COINGECKO_API_KEY)
                    .header("Accept", "application/json")
                    .method(original.method(), original.body())
                    .build();
                return chain.proceed(request);
            });
            
            retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        }
        return retrofit;
    }
    
    /**
     * Get CoinGecko API service
     * @return CoinGecko API service interface
     */
    public static CoinGeckoApi getCoinGeckoApi() {
        return getClient().create(CoinGeckoApi.class);
    }
}