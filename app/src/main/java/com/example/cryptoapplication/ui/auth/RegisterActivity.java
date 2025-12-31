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
import com.example.cryptoapplication.service.AuthResult;
import com.example.cryptoapplication.ui.home.HomeActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    EditText usernameEditText, emailEditText, passwordEditText;
    LinearLayout registerButton;
    TextView loginText;
    AuthService authService;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize AuthService
        authService = new AuthService(this);
        executorService = Executors.newSingleThreadExecutor();

        usernameEditText = findViewById(R.id.userNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        loginText = findViewById(R.id.LoginText);

        loginText.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
                return;
            }

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

            // Attempt registration using AuthService in the background
            executorService.execute(() -> {
                AuthResult authResult = authService.register(username, email, password);
                runOnUiThread(() -> {
                    if (authResult == null) {
                        Toast.makeText(getApplicationContext(), "An unexpected error occurred", Toast.LENGTH_LONG).show();
                        return;
                    }
                    switch (authResult.getStatus()) {
                        case SUCCESS:
                            Toast.makeText(getApplicationContext(), "Registration Successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        case USER_ALREADY_EXISTS:
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
