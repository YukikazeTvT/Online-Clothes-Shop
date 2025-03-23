package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.he172006.onlineclothesshop.DAO.AccountDAO;
import com.he172006.onlineclothesshop.entity.Account;

public class UserProfileActivity extends AppCompatActivity {
    private TextView txtName, txtEmail, txtAddress, txtPhone, txtRole;
    private Button btnEditProfile, btnLogout;
    private AccountDAO accountDAO;
    private int accountId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtAddress = findViewById(R.id.txtAddress);
        txtPhone = findViewById(R.id.txtPhone);
        txtRole = findViewById(R.id.txtRole);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);

        accountDAO = new AccountDAO(this);

        // Lấy accountId từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        accountId = prefs.getInt("accountId", -1);

        if (accountId != -1) {
            loadUserProfile(accountId);
        } else {
            txtName.setText("User not found");
        }

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("accountId", accountId);
            startActivity(intent);
        });

    }

    private void loadUserProfile(int accountId) {
        Account account = accountDAO.getAccountById(accountId);
        if (account != null) {
            txtName.setText("Name: " + account.getName());
            txtEmail.setText("Email: " + account.getEmail());
            txtAddress.setText("Address: " + account.getAddress());
            txtPhone.setText("Phone: " + account.getPhone());
            txtRole.setText("Role: " + account.getRole());
        } else {
            txtName.setText("User not found");
        }
    }
}
