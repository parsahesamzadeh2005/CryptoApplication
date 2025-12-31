package com.example.cryptoapplication.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.cryptoapplication.database.SimpleDatabaseService;
import com.example.cryptoapplication.models.User;

/**
 * Authentication service for managing user login/logout
 * Integrates with SQLite database for persistent user management
 */
public class AuthService {
    
    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    
    private final SharedPreferences sharedPreferences;
    private final SimpleDatabaseService databaseService;
    
    public AuthService(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.databaseService = SimpleDatabaseService.getInstance(context);
    }
    
    /**
     * Login user with email and password
     * Uses SQLite database for authentication
     */
    public AuthResult login(String email, String password) {
        if (!isValidEmail(email) || !isValidPassword(password)) {
            return new AuthResult(AuthResult.AuthStatus.INVALID_INPUT, null, "Invalid email or password format.");
        }

        User user = databaseService.loginUser(email, password);
        if (user != null) {
            // Store authentication state in SharedPreferences for quick access
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.apply();

            return new AuthResult(AuthResult.AuthStatus.SUCCESS, user, "Login successful.");
        }
        return new AuthResult(AuthResult.AuthStatus.INVALID_CREDENTIALS, null, "Invalid email or password.");
    }

    /**
     * Register new user
     * Uses SQLite database for user registration
     */
    public AuthResult register(String username, String email, String password) {
        if (!isValidEmail(email) || !isValidPassword(password) ||
            username == null || username.trim().isEmpty()) {
            return new AuthResult(AuthResult.AuthStatus.INVALID_INPUT, null, "Invalid username, email, or password format.");
        }

        User user = databaseService.registerNewUser(username, email, password);
        if (user != null) {
            return new AuthResult(AuthResult.AuthStatus.SUCCESS, user, "Registration successful.");
        } else {
            return new AuthResult(AuthResult.AuthStatus.FAILURE, null, "Registration failed.");
        }
    }

    /**
     * Register new user (overloaded method for backward compatibility)
     * Uses email as username for simplicity
     */
    public AuthResult register(String email, String password) {
        return register(email, email, password); // Use email as username
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        databaseService.logoutCurrentUser();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
    
    /**
     * Check if user is currently logged in
     */
    public boolean isLoggedIn() {
        return databaseService.isUserLoggedIn();
    }
    
    /**
     * Get current user details from database
     */
    public User getCurrentUser() {
        return databaseService.getCurrentUser();
    }
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    /**
     * Validate password strength
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        // Basic validation: at least 6 characters
        return password.length() >= 6;
    }
}