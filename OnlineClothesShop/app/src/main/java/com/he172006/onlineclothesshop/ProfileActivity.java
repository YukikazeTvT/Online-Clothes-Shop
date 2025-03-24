package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.he172006.onlineclothesshop.DAO.AccountDAO;
import com.he172006.onlineclothesshop.entity.Account;

import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {

    private EditText edtName, edtPassword, edtAddress, edtPhone;
    private Button btnSaveChanges;
    private AccountDAO accountDAO;
    private Session sessionManager;
    private Account currentAccount;

    // Regex để kiểm tra ký tự đặc biệt
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_\\+\\-\\=\\[\\]{};:'\",.<>?/|]");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Ẩn tiêu đề mặc định
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Thêm nút quay lại
        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_revert); // Icon nút quay lại

        // Initialize SessionManager
        sessionManager = new Session(this);

        // Initialize DAO
        accountDAO = new AccountDAO(this);

        // Initialize views
        edtName = findViewById(R.id.edtName);
        edtPassword = findViewById(R.id.edtPassword);
        edtAddress = findViewById(R.id.edtAddress);
        edtPhone = findViewById(R.id.edtPhone);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // Load current user information
        loadUserProfile();

        // Handle Save Changes button
        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void loadUserProfile() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please log in to view your profile", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        currentAccount = sessionManager.getLoggedInAccount();
        if (currentAccount != null) {
            edtName.setText(currentAccount.getName());
            edtPassword.setText(currentAccount.getPassword());
            edtAddress.setText(currentAccount.getAddress());
            edtPhone.setText(currentAccount.getPhone());
        } else {
            Toast.makeText(this, "Error: Unable to load user profile", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void saveChanges() {
        String name = edtName.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        // Validate input
        if (name.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate password
        String passwordValidationMessage = validatePassword(password);
        if (passwordValidationMessage != null) {
            Toast.makeText(this, passwordValidationMessage, Toast.LENGTH_LONG).show();
            return;
        }

        // Update account information
        currentAccount.setName(name);
        currentAccount.setPassword(password);
        currentAccount.setAddress(address);
        currentAccount.setPhone(phone);

        int rowsUpdated = accountDAO.updateAccount(currentAccount);
        if (rowsUpdated > 0) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

            // Hiển thị dialog hỏi người dùng
            new AlertDialog.Builder(this)
                    .setTitle("Profile Updated")
                    .setMessage("You want to return to Home Page ?")
                    .setNegativeButton("Yes", (dialog, which) -> {
                        // Chuyển về HomeActivity
                        startActivity(new Intent(this, HomeActivity.class));
                        finish();
                    })
                    .setPositiveButton("No", (dialog, which) -> {
                        // Load lại thông tin từ bảng Account
                        loadUserProfile();
                    })
                    .setCancelable(false) // Không cho phép đóng dialog bằng nút Back
                    .show();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    private String validatePassword(String password) {
        // Kiểm tra độ dài ít nhất 8 ký tự
        if (password.length() < 8) {
            return "Please enter at least 8 characters";
        }

        // Kiểm tra có ít nhất một chữ số
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
                break;
            }
        }
        if (!hasDigit) {
            return "Please enter at least one number";
        }

        // Kiểm tra có ít nhất một ký tự đặc biệt
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            return "Please enter at least one special character (!, @, #, $, %, ^, &, *, (, ), _, +, -, =, [, ], {, }, |, ;, :, ,, ., /, ?, <, >)";
        }

        return null; // Mật khẩu hợp lệ
    }

    // Thêm menu vào Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Ẩn các mục menu không cần thiết
        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.menu_sort).setVisible(false);
        menu.findItem(R.id.menu_user_profile).setVisible(false);
        return true;
    }

    // Xử lý sự kiện khi chọn item trong menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            // Xử lý nút quay lại trên Toolbar
            finish();
            return true;
        } else if (itemId == R.id.menu_logout) {
            if (sessionManager.isLoggedIn()) {
                sessionManager.logout();
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.menu_home) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return true;
        } else if (itemId == R.id.menu_cart) {
            startActivity(new Intent(this, ShoppingCartActivity.class));
            return true;
        } else if (itemId == R.id.menu_orders) {
            if (sessionManager.isLoggedIn()) {
                startActivity(new Intent(this, OrderHistoryActivity.class));
            } else {
                Toast.makeText(this, "Please log in to view your orders", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accountDAO != null) {
            accountDAO.close();
        }
    }
}