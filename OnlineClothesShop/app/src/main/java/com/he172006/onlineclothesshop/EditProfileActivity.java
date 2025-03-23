package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.he172006.onlineclothesshop.DAO.AccountDAO;
import com.he172006.onlineclothesshop.entity.Account;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etAddress, etPhone;
    private Button btnSaveProfile;
    private ImageButton ivBack;
    private AccountDAO accountDAO;
    private Account currentAccount;
    private Session sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize SessionManager
        sessionManager = new Session(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please log in to edit your profile", Toast.LENGTH_SHORT).show();
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
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        ivBack = findViewById(R.id.ivBack);

        // Initialize DAO
        accountDAO = new AccountDAO(this);

        // Load current user details into the fields
        etName.setText(currentAccount.getName());
        etEmail.setText(currentAccount.getEmail());
        etPassword.setText(""); // Password field is left blank for security
        etAddress.setText(currentAccount.getAddress());
        etPhone.setText(currentAccount.getPhone());

        // Handle Back button click
        ivBack.setOnClickListener(v -> finish());

        // Handle Save button click
        btnSaveProfile.setOnClickListener(v -> saveProfileChanges());
    }

    private void saveProfileChanges() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Validate required fields
        if (name.isEmpty() || email.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate password if provided
        if ((!password.isEmpty() && !isValidPassword(password))||password.isEmpty()) {
            Toast.makeText(this, "Password must be at least 8 characters, contain at least one number, and one special character (!,*,/,<,>,?,@)", Toast.LENGTH_LONG).show();
            return;
        }

        // Update Account object
        currentAccount.setName(name);
        currentAccount.setEmail(email);
        if (!password.isEmpty()) {
            currentAccount.setPassword(password);
        }
        currentAccount.setAddress(address);
        currentAccount.setPhone(phone);

        // Update in database
        int result = accountDAO.updateAccount(currentAccount);
        if (result > 0) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    // Validate password requirements (same as in SignUpActivity)
    private boolean isValidPassword(String password) {
        // At least 8 characters
        if (password.length() < 8) {
            return false;
        }

        // At least one number
        if (!password.matches(".*\\d.*")) {
            return false;
        }

        // At least one special character from the set (!,*,/,<,>,?,@)
        if (!password.matches(".*[!*/<>\\@?].*")) {
            return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accountDAO != null) {
            accountDAO.close();
        }
    }
}