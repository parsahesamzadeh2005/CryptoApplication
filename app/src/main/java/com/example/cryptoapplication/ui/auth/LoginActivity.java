package com.example.cryptoapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import com.google.android.material.button.MaterialButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cryptoapplication.R;
import com.example.cryptoapplication.service.AuthService;
import com.example.cryptoapplication.ui.home.HomeActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.cryptoapplication.service.AuthResult;

public class LoginActivity extends AppCompatActivity {

    MaterialButton loginButton;
    EditText emailEditText, passwordEditText;
    TextView createOneText;
    AuthService authService;
    private ExecutorService executorService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize AuthService
        authService = new AuthService(this);
        executorService = Executors.newSingleThreadExecutor();

        loginButton = findViewById(R.id.loginButton);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        createOneText = findViewById(R.id.createOneText);

        createOneText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Validate email
            if (TextUtils.isEmpty(email)) {
                emailEditText.setError("Email is required.");
                return;
            } else if (!AuthService.isValidEmail(email)) {
                emailEditText.setError("Please enter a valid email.");
                return;
            }

            // Validate password
            if (TextUtils.isEmpty(password)) {
                passwordEditText.setError("Password is required.");
                return;
            } else if (!AuthService.isValidPassword(password)) {
                passwordEditText.setError("Password must be at least 6 characters.");
                return;
            }

            // Attempt login using AuthService in the background without deprecated AsyncTask
            executorService.execute(() -> {
                AuthResult authResult = authService.login(email, password);
                runOnUiThread(() -> {
                    if (authResult == null) {
                        Toast.makeText(getApplicationContext(), "An unexpected error occurred.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    switch (authResult.getStatus()) {
                        case SUCCESS:
                            Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        case INVALID_CREDENTIALS:
                        case INVALID_INPUT:
                        case FAILURE:
                            Toast.makeText(getApplicationContext(), authResult.getMessage(), Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "An unknown error occurred.", Toast.LENGTH_LONG).show();
                            break;
                    }
                });
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
