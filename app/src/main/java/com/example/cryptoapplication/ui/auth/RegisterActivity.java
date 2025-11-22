package com.example.cryptoapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cryptoapplication.R;
import com.example.cryptoapplication.ui.home.HomeActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText usernameEditText, emailEditText, passwordEditText;
    Button registerButton;
    TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
                usernameEditText.setError("Username is required.");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                emailEditText.setError("Email is required.");
                return;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Please enter a valid email.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                passwordEditText.setError("Password is required.");
                return;
            } else if (password.length() < 6) {
                passwordEditText.setError("Password must be at least 6 characters.");
                return;
            }

            Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }
}