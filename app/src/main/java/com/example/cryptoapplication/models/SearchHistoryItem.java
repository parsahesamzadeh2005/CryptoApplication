package com.example.cryptoapplication.models;

public class SearchHistoryItem {
    private long id;
    private long userId;
    private String searchQuery;
    private long searchedAt;

    public SearchHistoryItem() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getSearchQuery() { return searchQuery; }
    public void setSearchQuery(String searchQuery) { this.searchQuery = searchQuery; }

    public long getSearchedAt() { return searchedAt; }
    public void setSearchedAt(long searchedAt) { this.searchedAt = searchedAt; }
}