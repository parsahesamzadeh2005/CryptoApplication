package com.example.cryptoapplication.database.dao;

import com.example.cryptoapplication.models.User;

/**
 * DAO interface for User-related database operations.
 * Extends BaseDao for common CRUD operations and adds user-specific methods.
 */
public interface UserDao extends BaseDao<User> {
    
    /**
     * Find a user by email address
     * @param email The email address to search for
     * @return The user if found, null otherwise
     */
    User findByEmail(String email);
    
    /**
     * Find a user by username
     * @param username The username to search for
     * @return The user if found, null otherwise
     */
    User findByUsername(String username);
    

    /**
     * Check if a user exists with the given email
     * @param email The email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if a user exists with the given username
     * @param username The username to check
     * @return true if user exists, false otherwise
     */

    
    /**
     * Update the last login timestamp for a user
     * @param userId The ID of the user
     * @return The number of rows affected
     */
    int updateLastLogin(long userId);
    
    /**
     * Get the currently logged in user
     * @return The currently logged in user, or null if none
     */
    User getCurrentUser();
    
    /**
     * Set the currently logged in user
     * @param userId The ID of the user to set as current
     * @return true if successful, false otherwise
     */
    boolean setCurrentUser(long userId);
    
    /**
     * Clear the currently logged in user
     * @return true if successful, false otherwise
     */
    boolean clearCurrentUser();
}