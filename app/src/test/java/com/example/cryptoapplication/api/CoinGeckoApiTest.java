package com.example.cryptoapplication.api;

import com.example.cryptoapplication.models.CoinModel;
import com.example.cryptoapplication.network.CoinGeckoApi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class CoinGeckoApiTest {

    @Mock
    private CoinGeckoApi mockCoinGeckoApi;
    
    @Mock
    private Call<List<CoinModel>> mockCall;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCoinMarketsSuccess() throws Exception {
        // Create mock response data
        List<CoinModel> mockCoins = Arrays.asList(
            new CoinModel("bitcoin", "btc", "Bitcoin", 50000.0, "https://example.com/bitcoin.png", 5.5),
            new CoinModel("ethereum", "eth", "Ethereum", 3000.0, "https://example.com/ethereum.png", -2.3)
        );
        
        // Mock the API call
        when(mockCoinGeckoApi.getCoinMarkets("usd", "market_cap_desc", 50, 1, false))
            .thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(Response.success(mockCoins));
        
        // Execute the call
        Call<List<CoinModel>> call = mockCoinGeckoApi.getCoinMarkets("usd", "market_cap_desc", 50, 1, false);
        Response<List<CoinModel>> response = call.execute();
        
        // Verify the results
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals(2, response.body().size());
        assertEquals("bitcoin", response.body().get(0).getId());
        assertEquals("ethereum", response.body().get(1).getId());
    }

    @Test
    public void testGetTopCoinsSuccess() throws Exception {
        // Create mock response data
        List<CoinModel> mockCoins = Arrays.asList(
            new CoinModel("bitcoin", "btc", "Bitcoin", 50000.0, "https://example.com/bitcoin.png", 5.5),
            new CoinModel("ethereum", "eth", "Ethereum", 3000.0, "https://example.com/ethereum.png", -2.3),
            new CoinModel("cardano", "ada", "Cardano", 1.5, "https://example.com/cardano.png", 8.7)
        );
        
        // Mock the API call
        when(mockCoinGeckoApi.getTopCoins("usd", 10, "market_cap_desc")).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(Response.success(mockCoins));
        
        // Execute the call
        Call<List<CoinModel>> call = mockCoinGeckoApi.getTopCoins("usd", 10, "market_cap_desc");
        Response<List<CoinModel>> response = call.execute();
        
        // Verify the results
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals(3, response.body().size());
        assertEquals("bitcoin", response.body().get(0).getId());
    }

    @Test
    public void testApiCallFailure() throws Exception {
        // Mock a failed response
        when(mockCoinGeckoApi.getCoinMarkets("usd", "market_cap_desc", 50, 1, false))
            .thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(Response.error(404, null));
        
        // Execute the call
        Call<List<CoinModel>> call = mockCoinGeckoApi.getCoinMarkets("usd", "market_cap_desc", 50, 1, false);
        Response<List<CoinModel>> response = call.execute();
        
        // Verify the results
        assertFalse(response.isSuccessful());
        assertEquals(404, response.code());
    }

    @Test
    public void testApiCallWithDifferentParameters() throws Exception {
        // Test with different parameters
        when(mockCoinGeckoApi.getCoinMarkets("eur", "volume_desc", 100, 2, true))
            .thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(Response.success(Arrays.asList()));
        
        Call<List<CoinModel>> call = mockCoinGeckoApi.getCoinMarkets("eur", "volume_desc", 100, 2, true);
        Response<List<CoinModel>> response = call.execute();
        
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals(0, response.body().size());
    }
}