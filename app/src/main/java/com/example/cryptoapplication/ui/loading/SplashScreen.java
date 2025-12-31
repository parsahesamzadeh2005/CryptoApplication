package com.example.cryptoapplication.ui.loading;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cryptoapplication.R;
import com.example.cryptoapplication.service.AuthService;
import com.example.cryptoapplication.ui.auth.LoginActivity;
import com.example.cryptoapplication.ui.home.HomeActivity;

public class SplashScreen extends AppCompatActivity {
    
    Handler handler;
    AuthService authService;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        // Initialize AuthService
        authService = new AuthService(this);

        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if user is already logged in
                if (authService.isLoggedIn()) {
                    // User is logged in, go to home activity
                    startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                } else {
                    // User is not logged in, go to login activity
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                }
                finish();
            }
        }, 3000); // 3 seconds splash duration

        logo = findViewById(R.id.appLogo);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.loader);
        logo.startAnimation(animation);
    }
}