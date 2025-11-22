package com.example.cryptoapplication.model;

import com.example.cryptoapplication.model.home.CoinModel;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CoinModelTest {

    private CoinModel coin;

    @Before
    public void setUp() {
        coin = new CoinModel(
            "bitcoin",
            "btc",
            "Bitcoin",
            50000.0,
            "https://example.com/bitcoin.png",
            5.5
        );
    }

    @Test
    public void testGetters() {
        assertEquals("bitcoin", coin.getId());
        assertEquals("btc", coin.getSymbol());
        assertEquals("Bitcoin", coin.getName());
        assertEquals(50000.0, coin.getCurrentPrice(), 0.01);
        assertEquals("https://example.com/bitcoin.png", coin.getImage());
        assertEquals(5.5, coin.getPriceChangePercentage24h(), 0.01);
    }

    @Test
    public void testFormattedPriceChange() {
        // Test positive change
        assertEquals("+5.50%", coin.getFormattedPriceChange());
        
        // Test negative change
        CoinModel negativeCoin = new CoinModel(
            "ethereum",
            "eth", 
            "Ethereum",
            3000.0,
            "https://example.com/ethereum.png",
            -3.2
        );
        assertEquals("-3.20%", negativeCoin.getFormattedPriceChange());
        
        // Test zero change
        CoinModel zeroCoin = new CoinModel(
            "cardano",
            "ada",
            "Cardano", 
            1.5,
            "https://example.com/cardano.png",
            0.0
        );
        assertEquals("+0.00%", zeroCoin.getFormattedPriceChange());
    }

    @Test
    public void testNullValues() {
        CoinModel nullCoin = new CoinModel(
            null,
            null,
            null,
            0.0,
            null,
            0.0
        );
        
        assertNull(nullCoin.getId());
        assertNull(nullCoin.getSymbol());
        assertNull(nullCoin.getName());
        assertNull(nullCoin.getImage());
        assertEquals(0.0, nullCoin.getCurrentPrice(), 0.01);
        assertEquals(0.0, nullCoin.getPriceChangePercentage24h(), 0.01);
    }

    @Test
    public void testPriceFormatting() {
        // Test high price
        CoinModel highPriceCoin = new CoinModel(
            "bitcoin",
            "btc",
            "Bitcoin",
            123456.789,
            "https://example.com/bitcoin.png",
            1.23
        );
        assertEquals(123456.789, highPriceCoin.getCurrentPrice(), 0.001);
        
        // Test low price
        CoinModel lowPriceCoin = new CoinModel(
            "shitcoin",
            "shit",
            "Shitcoin",
            0.000123,
            "https://example.com/shitcoin.png",
            45.67
        );
        assertEquals(0.000123, lowPriceCoin.getCurrentPrice(), 0.000001);
    }
}