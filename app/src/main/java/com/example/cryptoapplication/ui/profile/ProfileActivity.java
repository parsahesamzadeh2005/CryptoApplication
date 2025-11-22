package com.example.cryptoapplication.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cryptoapplication.R;
import com.example.cryptoapplication.ui.auth.LoginActivity;
import com.example.cryptoapplication.ui.home.HomeActivity;

public class ProfileActivity extends AppCompatActivity {
    LinearLayout logOut;
    private static final int PICK_IMAGE_REQUEST = 1;
    private de.hdodenhof.circleimageview.CircleImageView personIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logOut = findViewById(R.id.layoutLogout);

        Button homeButton = findViewById(R.id.btnHome);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_left);
            finish();
        });

        logOut.setOnClickListener(v -> {
            new AlertDialog.Builder(ProfileActivity.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // پاک‌کردن Backstack
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });

        personIcon = findViewById(R.id.personIcon);
        personIcon.setOnClickListener(v -> openGallery());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            personIcon.setImageURI(selectedImageUri);
        }
    }
}