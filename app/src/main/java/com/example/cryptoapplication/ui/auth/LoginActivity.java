package com.example.cryptoapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cryptoapplication.R;
import com.example.cryptoapplication.service.AuthService;
import com.example.cryptoapplication.ui.home.HomeActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.cryptoapplication.service.AuthResult;

public class LoginActivity extends AppCompatActivity {

    LinearLayout loginButton;
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
                Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
                return;
            } else if (!AuthService.isValidEmail(email)) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate password
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
                return;
            } else if (!AuthService.isValidPassword(password)) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Attempt login using AuthService in the background
            executorService.execute(() -> {
                AuthResult authResult = authService.login(email, password);
                runOnUiThread(() -> {
                    if (authResult == null) {
                        Toast.makeText(getApplicationContext(), "An unexpected error occurred", Toast.LENGTH_LONG).show();
                        return;
                    }
                    switch (authResult.getStatus()) {
                        case SUCCESS:
                            Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(), "An unknown error occurred", Toast.LENGTH_LONG).show();
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
