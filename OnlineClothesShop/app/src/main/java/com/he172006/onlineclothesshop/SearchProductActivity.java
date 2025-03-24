package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.he172006.onlineclothesshop.DAO.ProductDAO;
import com.he172006.onlineclothesshop.adapter.SearchProductAdapter;
import com.he172006.onlineclothesshop.entity.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchProductActivity extends AppCompatActivity {

    private EditText searchInput;
    private RecyclerView searchResultsRecyclerView;
    private SearchProductAdapter searchAdapter;
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> filteredProducts = new ArrayList<>();
    private ProductDAO productDAO;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Session sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);

        // Khởi tạo Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Bỏ tiêu đề mặc định của Toolbar (vì đã có TextView tvTitle trong layout)
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Thêm nút quay lại trên Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_revert);

        // Initialize SessionManager
        sessionManager = new Session(this);

        // Initialize views
        searchInput = findViewById(R.id.searchInput);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize DAO
        productDAO = new ProductDAO(this);

        // Load all products
        loadProducts();

        // Setup TextWatcher for search input
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadProducts() {
        executor.execute(() -> {
            allProducts = productDAO.getAllProducts();
            filteredProducts.addAll(allProducts);
            runOnUiThread(() -> {
                searchAdapter = new SearchProductAdapter(SearchProductActivity.this, filteredProducts);
                searchResultsRecyclerView.setAdapter(searchAdapter);
            });
        });
    }

    private void filterProducts(String query) {
        filteredProducts.clear();
        if (query.isEmpty()) {
            filteredProducts.addAll(allProducts);
        } else {
            for (Product product : allProducts) {
                if (product.getProductName().toLowerCase().contains(query.toLowerCase()) ||
                        product.getDescription().toLowerCase().contains(query.toLowerCase())) {
                    filteredProducts.add(product);
                }
            }
        }
        if (searchAdapter != null) {
            searchAdapter.updateProducts(filteredProducts);
        }
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
        }else if (itemId == R.id.menu_orders) {
            if (sessionManager.isLoggedIn()) {
                startActivity(new Intent(this, OrderHistoryActivity.class));
            } else {
                Toast.makeText(this, "Please log in to view your orders", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            }
            return true;
        }
        else if (itemId == R.id.menu_user_profile) {
            if (sessionManager.isLoggedIn()) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else {
                Toast.makeText(this, "Please log in to view your profile", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (productDAO != null) {
            productDAO.close();
        }
        executor.shutdown();
    }
}