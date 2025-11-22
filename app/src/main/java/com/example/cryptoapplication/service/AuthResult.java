package com.example.cryptoapplication.service;

import com.example.cryptoapplication.models.User;

public class AuthResult {

    public enum AuthStatus {
        SUCCESS,
        INVALID_CREDENTIALS,
        USER_ALREADY_EXISTS,
        INVALID_INPUT,
        FAILURE
    }

    private final AuthStatus status;
    private final User user;
    private final String message;

    public AuthResult(AuthStatus status, User user, String message) {
        this.status = status;
        this.user = user;
        this.message = message;
    }

    public AuthStatus getStatus() {
        return status;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}