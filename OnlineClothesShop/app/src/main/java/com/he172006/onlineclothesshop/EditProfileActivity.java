package com.he172006.onlineclothesshop;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.he172006.onlineclothesshop.DAO.AccountDAO;
import com.he172006.onlineclothesshop.entity.Account;

public class EditProfileActivity extends AppCompatActivity {
    private EditText edtName, edtEmail, edtAddress, edtPhone, edtPassword;
    private Button btnSave, btnCancel;
    private AccountDAO accountDAO;
    private int accountId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtAddress = findViewById(R.id.edtAddress);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        accountDAO = new AccountDAO(this);

        // Lấy accountId từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        accountId = prefs.getInt("accountId", -1);

        if (accountId != -1) {
            loadUserProfile(accountId);
        } else {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnSave.setOnClickListener(v -> updateUserProfile());

        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadUserProfile(int accountId) {
        Account account = accountDAO.getAccountById(accountId);
        if (account != null) {
            edtName.setText(account.getName());
            edtEmail.setText(account.getEmail());
            edtAddress.setText(account.getAddress());
            edtPhone.setText(account.getPhone());
            edtPassword.setText(account.getPassword());
        }
    }

    private void updateUserProfile() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(address) ||
                TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        Account updatedAccount = new Account(accountId, name, email, password, address, phone, "User");
        boolean isUpdated = accountDAO.updateAccounts(updatedAccount);

        if (isUpdated) {
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Update failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
