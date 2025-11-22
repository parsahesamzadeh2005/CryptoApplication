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
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    EditText emailEditText, passwordEditText;
    TextView createOneText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
            }


            if (email.equals("test@gmail.com") && password.equals("123456")) {
                Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Invalid Email/Password!", Toast.LENGTH_LONG).show();
            }
        });
    }
}