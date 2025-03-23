package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.he172006.onlineclothesshop.entity.Account;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvAddress, tvPhone;
    private Button btnEditProfile;
    private ImageButton ivBack;
    private Session sessionManager;
    private Account currentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize SessionManager
        sessionManager = new Session(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please log in to view your profile", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Get current logged-in account
        currentAccount = sessionManager.getLoggedInAccount();
        if (currentAccount == null) {
            Toast.makeText(this, "Error loading user data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhone = findViewById(R.id.tvPhone);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        ivBack = findViewById(R.id.ivBack);

        // Display user details
        tvName.setText(currentAccount.getName());
        tvEmail.setText(currentAccount.getEmail());
        tvAddress.setText(currentAccount.getAddress());
        tvPhone.setText(currentAccount.getPhone());

        // Handle Back button click
        ivBack.setOnClickListener(v -> finish());

        // Handle Edit Profile button click
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh user data in case it was updated
        currentAccount = sessionManager.getLoggedInAccount();
        if (currentAccount != null) {
            tvName.setText(currentAccount.getName());
            tvEmail.setText(currentAccount.getEmail());
            tvAddress.setText(currentAccount.getAddress());
            tvPhone.setText(currentAccount.getPhone());
        }
    }
}