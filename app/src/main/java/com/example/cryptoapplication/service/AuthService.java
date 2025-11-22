package com.example.cryptoapplication.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.cryptoapplication.database.DatabaseService;
import com.example.cryptoapplication.models.User;

/**
 * Authentication service for managing user login/logout
 * Integrates with SQLite database for persistent user management
 */
public class AuthService {
    
    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_TOKEN = "user_token";
    
    private final SharedPreferences sharedPreferences;
    private final DatabaseService databaseService;
    
    public AuthService(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.databaseService = DatabaseService.getInstance(context);
    }
    
    /**
     * Login user with email and password
     * Uses SQLite database for authentication
     */
    public AuthResult login(String email, String password) {
        if (!isValidEmail(email) || !isValidPassword(password)) {
            return new AuthResult(AuthResult.AuthStatus.INVALID_INPUT, null, "Invalid email or password format.");
        }

        User user = databaseService.login(email, password);
        if (user != null) {
            // Store authentication state in SharedPreferences for quick access
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.putString(KEY_USER_EMAIL, email);
            editor.putString(KEY_USER_TOKEN, "auth_token_" + System.currentTimeMillis());
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

        if (databaseService.getUserByEmail(email) != null) {
            return new AuthResult(AuthResult.AuthStatus.USER_ALREADY_EXISTS, null, "User with this email already exists.");
        }

        User user = databaseService.register(username, email, password);
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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
    
    /**
     * Check if user is currently logged in
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    /**
     * Get current user email
     */
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }
    
    /**
     * Get current user details from database
     */
    public User getCurrentUser() {
        String email = getUserEmail();
        if (email != null) {
            return databaseService.getUserByEmail(email);
        }
        return null;
    }
    
    /**
     * Get current user's auth token
     */
    public String getAuthToken() {
        return sharedPreferences.getString(KEY_USER_TOKEN, null);
    }

    /**
     * Backward-compatible getter used by tests
     * Alias for getAuthToken()
     */
    public String getUserToken() {
        return getAuthToken();
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