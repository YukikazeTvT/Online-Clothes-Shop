package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.he172006.onlineclothesshop.DAO.AccountDAO;

public class SignUpActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword, etAddress, etPhone;
    private Button btnSignUp;
    private ImageView ivBack;
    private AccountDAO accountDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        btnSignUp = findViewById(R.id.btnSignUp);
        ivBack = findViewById(R.id.ivBack);

        // Initialize DAO
        accountDAO = new AccountDAO(this);

        // Handle sign up button click
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String role = "user"; // Automatically set role to "user"

                // Validate required fields
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate email format
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(SignUpActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate password requirements
                if (!isValidPassword(password)) {
                    Toast.makeText(SignUpActivity.this, "Password must be at least 8 characters, contain at least one number, and one special character (!,*,/,<,>,?,@)", Toast.LENGTH_LONG).show();
                    return;
                }

                // Register the user
                long result = accountDAO.register(name, email, password, address, phone, role);
                if (result != -1) {
                    Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle back button click
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close SignUpActivity to prevent stacking
            }
        });
    }

    // Validate password requirements
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
        accountDAO.close();
    }
}