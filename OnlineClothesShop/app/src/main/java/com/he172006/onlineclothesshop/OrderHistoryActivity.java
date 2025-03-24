package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.he172006.onlineclothesshop.DAO.OrderDAO;
import com.he172006.onlineclothesshop.adapter.OrderHistoryAdapter;
import com.he172006.onlineclothesshop.entity.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrders;
    private OrderHistoryAdapter orderAdapter;
    private List<Order> orderList = new ArrayList<>();
    private OrderDAO orderDAO;
    private Session sessionManager;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Khởi tạo Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_revert);

        // Initialize SessionManager
        sessionManager = new Session(this);

        // Initialize DAO
        orderDAO = new OrderDAO(this);

        // Initialize RecyclerView
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderHistoryAdapter(this, orderList);
        recyclerViewOrders.setAdapter(orderAdapter);

        // Load orders
        loadOrders();
    }

    private void loadOrders() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please log in to view your orders", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        int accountId = sessionManager.getAccountId();
        executor.execute(() -> {
            List<Order> orders = orderDAO.getOrdersByAccountId(accountId);
            runOnUiThread(() -> {
                orderList.clear();
                orderList.addAll(orders);
                orderAdapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
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
        }
        else if (itemId == R.id.menu_orders) {
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
        if (orderDAO != null) {
            orderDAO.close();
        }
        executor.shutdown();
    }
}