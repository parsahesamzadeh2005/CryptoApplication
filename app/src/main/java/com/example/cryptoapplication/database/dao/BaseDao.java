package com.example.cryptoapplication.database.dao;

import java.util.List;

/**
 * Base DAO interface defining common CRUD operations.
 * @param <T> The entity type
 */
public interface BaseDao<T> {
    
    /**
     * Insert an entity into the database
     * @param entity The entity to insert
     * @return The ID of the inserted entity, or -1 if failed
     */
    long insert(T entity);

    /**
     * Update an existing entity in the database
     * @param entity The entity to update
     * @return The number of rows affected
     */
    int update(T entity);
    
    /**
     * Delete an entity from the database
     * @param entity The entity to delete
     * @return The number of rows affected
     */
    int delete(T entity);
    
    /**
     * Get all entities from the database
     * @return List of all entities
     */
    List<T> getAll();
    
    /**
     * Get an entity by its ID
     * @param id The ID of the entity
     * @return The entity, or null if not found
     */
    T getById(long id);
    
    /**
     * Count the total number of entities
     * @return The count of entities
     */
    int count();
    
    /**
     * Check if an entity exists by ID
     * @param id The ID to check
     * @return true if exists, false otherwise
     */
    boolean exists(long id);
    
    /**
     * Delete all entities from the table
     * @return The number of rows deleted
     */
    int deleteAll();
}