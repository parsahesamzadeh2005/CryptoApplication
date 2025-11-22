package com.example.cryptoapplication.ui.profile;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cryptoapplication.R;
import com.example.cryptoapplication.database.DatabaseService;
import com.example.cryptoapplication.models.User;
import com.example.cryptoapplication.service.AuthService;
import com.example.cryptoapplication.ui.auth.LoginActivity;
import com.example.cryptoapplication.ui.profile.TransactionHistoryActivity;
import com.example.cryptoapplication.ui.home.HomeActivity;
import com.example.cryptoapplication.ui.home.adapter.WalletAssetsAdapter;
import com.example.cryptoapplication.models.PortfolioItem;

public class ProfileActivity extends AppCompatActivity {
    LinearLayout logOut;
    private static final int PICK_IMAGE_REQUEST = 1;
    private de.hdodenhof.circleimageview.CircleImageView personIcon;
    private AuthService authService;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private TextView txtUsername;
    private TextView txtBalanceAmount;
    private Button btnAddBalance;
    private Button btnWithdraw;
    private RecyclerView walletAssetsRecyclerView;
    private WalletAssetsAdapter walletAssetsAdapter;
    private View cardWalletAssets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize AuthService
        authService = new AuthService(this);
        DatabaseService databaseService = DatabaseService.getInstance(this);

        logOut = findViewById(R.id.layoutLogout);
        txtUsername = findViewById(R.id.txtUsername);
        txtBalanceAmount = findViewById(R.id.txtBalanceAmount);
        btnAddBalance = findViewById(R.id.btnAddBalance);
        btnWithdraw = findViewById(R.id.btnWithdraw);
        walletAssetsRecyclerView = findViewById(R.id.walletAssetsRecyclerView);
        cardWalletAssets = findViewById(R.id.cardWalletAssets);
        walletAssetsAdapter = new WalletAssetsAdapter(new java.util.ArrayList<>(), databaseService);
        walletAssetsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        walletAssetsRecyclerView.setAdapter(walletAssetsAdapter);
        java.util.List<PortfolioItem> assetsInitial = databaseService.getPortfolio();
        if (assetsInitial != null && !assetsInitial.isEmpty()) {
            walletAssetsAdapter.updateItems(assetsInitial);
            cardWalletAssets.setVisibility(View.VISIBLE);
        } else {
            cardWalletAssets.setVisibility(View.GONE);
        }

        // Populate username and balance
        User currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            txtUsername.setText(currentUser.getUsername());
            txtBalanceAmount.setText(String.format("$%.2f", currentUser.getBalance()));
        } else if (databaseService.getCurrentUser() != null) {
            User u = databaseService.getCurrentUser();
            txtUsername.setText(u.getUsername());
            txtBalanceAmount.setText(String.format("$%.2f", u.getBalance()));
        } else {
            txtUsername.setText("Guest");
            txtBalanceAmount.setText("—");
        }

        Button homeButton = findViewById(R.id.btnHome);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(
                    ProfileActivity.this,
                    R.anim.slide_in_from_left,
                    R.anim.slide_out_to_left
            );
            startActivity(intent, options.toBundle());
            finish();
        });

        logOut.setOnClickListener(v -> {
            new AlertDialog.Builder(ProfileActivity.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Logout user using AuthService
                        authService.logout();
                        
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
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        personIcon.setImageURI(selectedImageUri);
                    }
                }
        );
        personIcon.setOnClickListener(v -> openGallery());

        // Navigate to Transaction History
        LinearLayout layoutHistory = findViewById(R.id.layoutHistory);
        if (layoutHistory != null) {
            layoutHistory.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, TransactionHistoryActivity.class);
                startActivity(intent);
            });
        }

        // Add Balance flow
        btnAddBalance.setOnClickListener(v -> showAddBalanceDialog());

        // Withdraw Balance flow
        btnWithdraw.setOnClickListener(v -> showWithdrawDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseService db = DatabaseService.getInstance(this);
        if (walletAssetsAdapter != null) {
            java.util.List<PortfolioItem> assets = db.getPortfolio();
            if (assets != null && !assets.isEmpty()) {
                walletAssetsAdapter.updateItems(assets);
                if (cardWalletAssets != null) cardWalletAssets.setVisibility(View.VISIBLE);
            } else {
                if (cardWalletAssets != null) cardWalletAssets.setVisibility(View.GONE);
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void showAddBalanceDialog() {
        DatabaseService databaseService = DatabaseService.getInstance(this);
        User user = databaseService.getCurrentUser();
        if (user == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not logged in")
                    .setMessage("Please log in to add balance.")
                    .setPositiveButton("OK", (d, w) -> d.dismiss())
                    .show();
            return;
        }

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Amount (e.g., 100.00)");
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(padding, padding, padding, padding);

        new AlertDialog.Builder(this)
                .setTitle("Add Balance")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String text = input.getText().toString().trim();
                    try {
                        double amount = Double.parseDouble(text);
                        if (amount <= 0) throw new NumberFormatException();
                        boolean ok = databaseService.addUserBalance(amount);
                        if (ok) {
                            txtBalanceAmount.setText(String.format("$%.2f", databaseService.getCurrentUser().getBalance()));
                        } else {
                            new AlertDialog.Builder(this)
                                    .setTitle("Error")
                                    .setMessage("Failed to update balance.")
                                    .setPositiveButton("OK", (d, w) -> d.dismiss())
                                    .show();
                        }
                    } catch (Exception e) {
                        new AlertDialog.Builder(this)
                                .setTitle("Invalid amount")
                                .setMessage("Please enter a valid positive number.")
                                .setPositiveButton("OK", (d, w) -> d.dismiss())
                                .show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showWithdrawDialog() {
        DatabaseService databaseService = DatabaseService.getInstance(this);
        User user = databaseService.getCurrentUser();
        if (user == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not logged in")
                    .setMessage("Please log in to withdraw.")
                    .setPositiveButton("OK", (d, w) -> d.dismiss())
                    .show();
            return;
        }

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Amount (e.g., 100.00)");
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(padding, padding, padding, padding);

        new AlertDialog.Builder(this)
                .setTitle("Withdraw Balance")
                .setView(input)
                .setPositiveButton("Withdraw", (dialog, which) -> {
                    String text = input.getText().toString().trim();
                    try {
                        double amount = Double.parseDouble(text);
                        if (amount <= 0) throw new NumberFormatException();
                        boolean ok = databaseService.withdrawFiat(amount);
                        if (ok) {
                            txtBalanceAmount.setText(String.format("$%.2f", databaseService.getCurrentUser().getBalance()));
                        } else {
                            new AlertDialog.Builder(this)
                                    .setTitle("Error")
                                    .setMessage("Insufficient balance or failed to update.")
                                    .setPositiveButton("OK", (d, w) -> d.dismiss())
                                    .show();
                        }
                    } catch (Exception e) {
                        new AlertDialog.Builder(this)
                                .setTitle("Invalid amount")
                                .setMessage("Please enter a valid positive number.")
                                .setPositiveButton("OK", (d, w) -> d.dismiss())
                                .show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

}