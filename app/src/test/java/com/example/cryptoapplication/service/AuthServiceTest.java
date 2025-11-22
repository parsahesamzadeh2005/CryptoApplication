package com.example.cryptoapplication.service;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class AuthServiceTest {

    private AuthService authService;
    
    @Mock
    private Context mockContext;
    
    @Mock
    private SharedPreferences mockSharedPreferences;
    
    @Mock
    private SharedPreferences.Editor mockEditor;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(ApplicationProvider.getApplicationContext());
        
        // Setup mock behavior
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
        when(mockEditor.remove(anyString())).thenReturn(mockEditor);
    }

    @Test
    public void testValidEmailFormat() {
        assertTrue(authService.isValidEmail("test@example.com"));
        assertTrue(authService.isValidEmail("user.name@domain.co.uk"));
        assertFalse(authService.isValidEmail("invalid-email"));
        assertFalse(authService.isValidEmail("@example.com"));
        assertFalse(authService.isValidEmail("test@"));
        assertFalse(authService.isValidEmail(""));
        assertFalse(authService.isValidEmail(null));
    }

    @Test
    public void testValidPasswordFormat() {
        assertTrue(authService.isValidPassword("Password123!"));
        assertTrue(authService.isValidPassword("SecurePass1@"));
        assertFalse(authService.isValidPassword("short"));
        assertFalse(authService.isValidPassword("nouppercase123!"));
        assertFalse(authService.isValidPassword("NOLOWERCASE123!"));
        assertFalse(authService.isValidPassword("NoNumbers!"));
        assertFalse(authService.isValidPassword("NoSpecial123"));
        assertFalse(authService.isValidPassword(""));
        assertFalse(authService.isValidPassword(null));
    }

    @Test
    public void testLoginWithValidCredentials() {
        // Test login with valid credentials
        AuthResult result = authService.login("test@example.com", "Password123!");
        assertEquals(AuthResult.AuthStatus.SUCCESS, result.getStatus());
        assertTrue(authService.isLoggedIn());
        assertEquals("test@example.com", authService.getUserEmail());
        assertNotNull(authService.getUserToken());
    }

    @Test
    public void testLoginWithInvalidEmail() {
        AuthResult result = authService.login("invalid-email", "Password123!");
        assertNotEquals(AuthResult.AuthStatus.SUCCESS, result.getStatus());
        assertFalse(authService.isLoggedIn());
    }

    @Test
    public void testLoginWithInvalidPassword() {
        AuthResult result = authService.login("test@example.com", "weak");
        assertNotEquals(AuthResult.AuthStatus.SUCCESS, result.getStatus());
        assertFalse(authService.isLoggedIn());
    }

    @Test
    public void testRegisterWithValidData() {
        AuthResult result = authService.register("testuser", "test@example.com", "Password123!");
        assertEquals(AuthResult.AuthStatus.SUCCESS, result.getStatus());
        assertTrue(authService.isLoggedIn());
        assertEquals("test@example.com", authService.getUserEmail());
        assertNotNull(authService.getUserToken());
    }

    @Test
    public void testRegisterWithInvalidEmail() {
        AuthResult result = authService.register("testuser", "invalid-email", "Password123!");
        assertNotEquals(AuthResult.AuthStatus.SUCCESS, result.getStatus());
        assertFalse(authService.isLoggedIn());
    }

    @Test
    public void testRegisterWithInvalidPassword() {
        AuthResult result = authService.register("testuser", "test@example.com", "weak");
        assertNotEquals(AuthResult.AuthStatus.SUCCESS, result.getStatus());
        assertFalse(authService.isLoggedIn());
    }

    @Test
    public void testLogout() {
        // First login
        authService.login("test@example.com", "Password123!");
        assertTrue(authService.isLoggedIn());
        
        // Then logout
        authService.logout();
        assertFalse(authService.isLoggedIn());
        assertNull(authService.getUserEmail());
        assertNull(authService.getUserToken());
    }

    @Test
    public void testGetUserTokenWhenNotLoggedIn() {
        assertNull(authService.getUserToken());
    }

    @Test
    public void testGetUserEmailWhenNotLoggedIn() {
        assertNull(authService.getUserEmail());
    }
}