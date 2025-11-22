package com.example.cryptoapplication.ui.loading;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cryptoapplication.R;
import com.example.cryptoapplication.ui.auth.LoginActivity;

public class SplashScreen extends AppCompatActivity {
    
    Handler handler;

    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                finish();
            }
        }, 5000);

        logo = findViewById(R.id.mainLogoLoading);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.loader);
        logo.startAnimation(animation);
    }
}