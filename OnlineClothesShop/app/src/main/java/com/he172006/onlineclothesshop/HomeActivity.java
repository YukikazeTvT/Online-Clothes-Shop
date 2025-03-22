package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.he172006.onlineclothesshop.DAO.CategoryDAO;
import com.he172006.onlineclothesshop.DAO.ProductDAO;
import com.he172006.onlineclothesshop.adapter.CategorySliderAdapter;
import com.he172006.onlineclothesshop.adapter.ProductGridAdapter;
import com.he172006.onlineclothesshop.entity.Category;
import com.he172006.onlineclothesshop.entity.Product;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager2 vpCategorySlider;
    private TabLayout tabDots;
    private GridView gvProducts;
    private Button btnToProductList;
    private CategorySliderAdapter categorySliderAdapter;
    private ProductGridAdapter productGridAdapter;
    private List<Category> categoryList;
    private List<Product> productList;
    private Handler sliderHandler = new Handler();
    private Runnable sliderRunnable;
    private Session sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize SessionManager
        sessionManager = new Session(this);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        vpCategorySlider = findViewById(R.id.vpCategorySlider);
        tabDots = findViewById(R.id.tabDots);
        gvProducts = findViewById(R.id.gvProducts);
        btnToProductList = findViewById(R.id.btnToProductList);

        // Set up Toolbar
        setSupportActionBar(toolbar);

        // Load categories and products from database
        loadCategories();
        loadProducts();

        // Setup slider for categories
        setupCategorySlider();

        // Setup GridView for products
        setupProductGrid();

        // Handle "To Product List" button click
        btnToProductList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent = new Intent(HomeActivity.this, ProductListActivity.class);
                // startActivity(intent);
            }
        });
    }

    private void loadCategories() {
        CategoryDAO categoryDAO = new CategoryDAO(this);
        categoryList = categoryDAO.getAllCategories();
        categoryDAO.close();

        // Lấy 4 danh mục ngẫu nhiên
        if (categoryList.size() > 4) {
            Collections.shuffle(categoryList);
            categoryList = categoryList.subList(0, 4);
        }
    }

    private void loadProducts() {
        ProductDAO productDAO = new ProductDAO(this);
        productList = productDAO.getAllProducts();
        productDAO.close();

        // Lấy 8 sản phẩm đầu tiên
        if (productList.size() > 8) {
            productList = productList.subList(0, 8);
        }
    }

    private void setupCategorySlider() {
        categorySliderAdapter = new CategorySliderAdapter(this, categoryList);
        vpCategorySlider.setAdapter(categorySliderAdapter);

        // Kết nối TabLayout với ViewPager2 để hiển thị chấm
        new TabLayoutMediator(tabDots, vpCategorySlider, (tab, position) -> {
            // Không cần thiết lập nội dung cho tab vì chỉ hiển thị chấm
        }).attach();

        // Tự động cuộn slider
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = vpCategorySlider.getCurrentItem();
                int totalItems = categorySliderAdapter.getItemCount();
                if (currentItem < totalItems - 1) {
                    vpCategorySlider.setCurrentItem(currentItem + 1);
                } else {
                    vpCategorySlider.setCurrentItem(0);
                }
                sliderHandler.postDelayed(this, 3000); // Cuộn mỗi 3 giây
            }
        };
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    private void setupProductGrid() {
        productGridAdapter = new ProductGridAdapter(this, productList);
        gvProducts.setAdapter(productGridAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    // Thêm menu vào Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Xử lý sự kiện khi chọn item trong menu
                @Override
                public boolean onOptionsItemSelected(MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.menu_logout) {
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
                    }
//                    } else if (itemId == R.id.menu_home) {
//                        // Đã ở HomeActivity, không cần làm gì
//                        return true;
//                    } else if (itemId == R.id.menu_categories) {
//                        startActivity(new Intent(this, CategoriesActivity.class));
//                        return true;
//                    } else if (itemId == R.id.menu_cart) {
//                        startActivity(new Intent(this, ShoppingCartActivity.class));
//                        return true;
//                    } else if (itemId == R.id.menu_orders) {
//                        if (sessionManager.isLoggedIn()) {
//                            startActivity(new Intent(this, OrderListActivity.class));
//                        } else {
//                            Toast.makeText(this, "Please log in to view your orders", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(this, LoginActivity.class));
//                        }
//                        return true;
//                    } else if (itemId == R.id.menu_user_profile) {
//                        if (sessionManager.isLoggedIn()) {
//                            startActivity(new Intent(this, ProfileActivity.class));
//                        } else {
//                            Toast.makeText(this, "Please log in to view your profile", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(this, LoginActivity.class));
//                        }
//                        return true;
//                    }
                    return super.onOptionsItemSelected(item);
                }
            }