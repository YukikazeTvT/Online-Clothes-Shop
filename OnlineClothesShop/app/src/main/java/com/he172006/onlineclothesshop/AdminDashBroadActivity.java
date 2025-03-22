package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AdminDashBroadActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout layoutManageProduct;
    private LinearLayout layoutManageCategory;
    private LinearLayout layoutManageOrder;
    private LinearLayout layoutLogout;
    private Session sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash_broad);

        // Initialize SessionManager
        sessionManager = new Session(this);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        layoutManageProduct = findViewById(R.id.layoutManageProduct);
        layoutManageCategory = findViewById(R.id.layoutManageCategory);
        layoutManageOrder = findViewById(R.id.layoutManageOrder);
        layoutLogout = findViewById(R.id.layoutLogout);

        // Set up Toolbar
        setSupportActionBar(toolbar);

        // Handle clicks
        layoutManageProduct.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashBroadActivity.this, ManageProductActivity.class);
            startActivity(intent);
        });

        layoutManageCategory.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashBroadActivity.this, ManageCategoryActivity.class);
            startActivity(intent);
        });

        layoutManageOrder.setOnClickListener(v -> {
            // Chưa có ManageOrderActivity, hiển thị thông báo
            Toast.makeText(AdminDashBroadActivity.this, "Manage Order feature is not implemented yet", Toast.LENGTH_SHORT).show();
            // Nếu sau này có ManageOrderActivity, bạn có thể bỏ comment dòng dưới
            // Intent intent = new Intent(AdminDashBroadActivity.this, ManageOrderActivity.class);
            // startActivity(intent);
        });

        layoutLogout.setOnClickListener(v -> {
            // Xóa phiên đăng nhập
            sessionManager.logout();
            Toast.makeText(AdminDashBroadActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            // Chuyển về LoginActivity
            Intent intent = new Intent(AdminDashBroadActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}