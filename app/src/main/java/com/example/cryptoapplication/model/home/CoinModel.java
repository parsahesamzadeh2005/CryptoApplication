package com.example.cryptoapplication.model.home;

public class CoinModel {
    private String id;
    private String symbol;
    private String name;
    private String price;
    private String image;

    public CoinModel(String id, String symbol, String name, String price, String image) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.image = image;
    }
    public String getId() { return id; }
    public String getSymbol() {
        return symbol;
    }
    public String getFullName() {
        return name;
    }
    public String getName() {
        return name;
    }
    public String getPrice() {
        return price;
    }
    public String getImage() { return image; }
}
