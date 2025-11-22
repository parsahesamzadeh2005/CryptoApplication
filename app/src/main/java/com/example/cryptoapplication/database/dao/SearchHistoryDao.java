package com.example.cryptoapplication.database.dao;

import com.example.cryptoapplication.models.SearchHistoryItem;

import java.util.List;

/**
 * DAO interface for Search History-related database operations.
 * Extends BaseDao for common CRUD operations and adds search-specific methods.
 */
public interface SearchHistoryDao extends BaseDao<SearchHistoryItem> {
    
    /**
     * Get search history for a specific user
     * @param userId The ID of the user
     * @return List of search history items
     */
    List<SearchHistoryItem> getByUserId(long userId);
    
    /**
     * Get recent search history for a user (ordered by timestamp)
     * @param userId The ID of the user
     * @param limit Maximum number of items to return
     * @return List of recent search history items
     */
    List<SearchHistoryItem> getRecentSearches(long userId, int limit);
    
    /**
     * Search for items in the search history
     * @param userId The ID of the user
     * @param query The search query
     * @return List of matching search history items
     */
    List<SearchHistoryItem> searchInHistory(long userId, String query);
    
    /**
     * Add a search query to history (creates new entry or updates existing)
     * @param userId The ID of the user
     * @param query The search query
     * @return true if successful, false otherwise
     */
    boolean addSearchQuery(long userId, String query);
    
    /**
     * Clear all search history for a user
     * @param userId The ID of the user
     * @return Number of items deleted
     */
    int clearUserSearchHistory(long userId);
    
    /**
     * Delete old search history entries (older than specified days)
     * @param daysToKeep Number of days to keep
     * @return Number of items deleted
     */
    int deleteOldEntries(int daysToKeep);
    
    /**
     * Get most searched queries across all users
     * @param limit Maximum number of queries to return
     * @return List of most searched queries
     */
    List<String> getMostSearchedQueries(int limit);
    
    /**
     * Check if a search query exists for a user
     * @param userId The ID of the user
     * @param query The search query to check
     * @return true if exists, false otherwise
     */
    boolean searchQueryExists(long userId, String query);
    
    /**
     * Update the timestamp of an existing search query
     * @param userId The ID of the user
     * @param query The search query
     * @return true if successful, false otherwise
     */
    boolean updateSearchTimestamp(long userId, String query);
    
    /**
     * Get unique search queries for a user
     * @param userId The ID of the user
     * @return List of unique search queries
     */
    List<String> getUniqueUserQueries(long userId);
}