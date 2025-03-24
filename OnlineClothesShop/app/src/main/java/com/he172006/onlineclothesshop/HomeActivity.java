package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;



import com.bumptech.glide.Glide;

import com.he172006.onlineclothesshop.DAO.BannerDAO;
import com.he172006.onlineclothesshop.DAO.CategoryDAO;
import com.he172006.onlineclothesshop.DAO.ProductDAO;
import com.he172006.onlineclothesshop.adapter.BannerAdapter;
import com.he172006.onlineclothesshop.adapter.CategoryAdapter;
import com.he172006.onlineclothesshop.adapter.ProductDisplayAdapter;
import com.he172006.onlineclothesshop.dtb.DataBase;
import com.he172006.onlineclothesshop.entity.Banner;
import com.he172006.onlineclothesshop.entity.Category;
import com.he172006.onlineclothesshop.entity.Product;
import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView categoryRecyclerView, productRecyclerView;
    private CategoryAdapter categoryAdapter;
    private ProductDisplayAdapter productDisplayAdapter;
    private ProductDAO productDAO;
    private BannerDAO bannerDAO;
    private CategoryDAO categoryDAO;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler bannerHandler = new Handler();
    private int bannerCurrentPosition = 0;
    private ViewPager2 bannerViewPager;
    private DataBase dbHelper;
    private SQLiteDatabase db;
    private Session sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize SessionManager
        sessionManager = new Session(this);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle = toolbar.findViewById(R.id.tvTitle);
        if (tvTitle != null) {
            tvTitle.setText("GUCIT");
        }

        bannerDAO = new BannerDAO(this);
        categoryDAO = new CategoryDAO(this);
        productDAO = new ProductDAO(this);
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        productRecyclerView = findViewById(R.id.productRecyclerView);
        bannerViewPager = findViewById(R.id.bannerViewPager);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        productRecyclerView.setHasFixedSize(true);
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        setupBanner();
        loadData();
    }

    private void setupBanner() {
        executor.execute(() -> {
            if (bannerDAO.getAllBanners().isEmpty()) { // Sử dụng đối tượng bannerDAO
                List<Banner> banners = Arrays.asList(
                        new Banner("https://cdn.venngage.com/template/thumbnail/small/01b644bd-e75b-4e70-b476-3a786261f066.webp"),
                        new Banner("https://marketplace.canva.com/EAFHG6sbLsQ/1/0/1600w/canva-brown-beige-simple-special-sale-banner-lQfPvhnznqs.jpg"),
                        new Banner("https://img.freepik.com/free-psd/luxury-men-s-fashion-template-design_23-2150855892.jpg?semt=ais_hybrid")
                );
                for (Banner banner : banners) {
                    bannerDAO.insertBanner(banner.getImage()); // Chèn từng banner vào DB
                }
            }
            List<Banner> banners = bannerDAO.getAllBanners();
            List<String> bannerImages = new ArrayList<>();
            for (Banner banner : banners) {
                bannerImages.add(banner.getImage());
            }
            for (String url : bannerImages) {
                Log.d("BannerDebug", "Image URL: " + url);
            }
            runOnUiThread(() -> {
                BannerAdapter bannerAdapter = new BannerAdapter(HomeActivity.this, bannerImages);
                bannerViewPager.setAdapter(bannerAdapter);
                bannerHandler.postDelayed(bannerRunnable, 3000);
            });
        });
    }

    private final Runnable bannerRunnable = new Runnable() {
        @Override
        public void run() {
            if (bannerCurrentPosition < 2) {
                bannerCurrentPosition++;
            } else {
                bannerCurrentPosition = 0;
            }
            bannerViewPager.setCurrentItem(bannerCurrentPosition, true);
            bannerHandler.postDelayed(this, 3000);
        }
    };

    private void loadData() {
        executor.execute(() -> {
            if (categoryDAO.getAllCategories().isEmpty()) {
                List<Category> categories = Arrays.asList(
//                        new Category("Quần jeans", ""),
//                        new Category("Áo sơ mi", ""),
//                        new Category("Giày sneaker", ""),
//                        new Category("Dép sandal", ""),
//                        new Category("Mũ lưỡi trai", ""),
//                        new Category("Ba lô", ""),
//                        new Category("Túi xách", ""),
//                        new Category("Kính râm", "")
                );
                categoryDAO.insertCategories(categories);
            }
            if (productDAO.getAllProducts().isEmpty()) {
                List<Product> products = Arrays.asList(
//                        new Product(1, "Quần Jean Dáng Carpenter", "Quần chất jean thuần cotton độ co dãn không nhiều, cho form quần luôn giữ dáng nguyên bản", 14000, 10, "https://bizweb.dktcdn.net/100/045/077/products/o1cn01vri9gj1zqm7pjqxex-2201202046709-0-cib-5f7a7fba-c83f-4d92-b8da-a95b9799feed.jpg?v=1740247074120"),
//                        new Product(1, "Áo sơ mi", "Mô tả sản phẩm", 13200, 10, "https://example.com/image.jpg"),
//                        new Product(1, "Giày sneaker", "Mô tả sản phẩm", 14100, 10, "https://example.com/image.jpg"),
//                        new Product(1, "Mũ lưỡi trai", "Mô tả sản phẩm", 15000, 10, "https://example.com/image.jpg"),
//                        new Product(1, "Ba lô", "Mô tả sản phẩm", 15600, 10, "https://example.com/image.jpg"),
//                        new Product(1, "Túi xách", "Mô tả sản phẩm", 17000, 10, "https://example.com/image.jpg")
                );
                productDAO.insertProducts(products);
            }

            List<Category> categories = categoryDAO.getAllCategories();
            List<Product> products = productDAO.getAllProducts();

            runOnUiThread(() -> {
                categoryAdapter = new CategoryAdapter(categories);
                productDisplayAdapter = new ProductDisplayAdapter(HomeActivity.this, products);
                categoryRecyclerView.setAdapter(categoryAdapter);
                productRecyclerView.setAdapter(productDisplayAdapter);
                categoryAdapter.setOnItemClickListener(category -> {
                    Intent intent = new Intent(HomeActivity.this, ListProductByCategory.class);
                    intent.putExtra("categoryId", category.getCategoryId());
                    intent.putExtra("categoryName", category.getCategoryName());
                    String imagePath = intent.getStringExtra("product_image");

                    startActivity(intent);
                });
            });
        });
    }

    public void showSortMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.menu_sort, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.sort_price_low_high) {
                sortProducts(true);
                return true;
            } else if (id == R.id.sort_price_high_low) {
                sortProducts(false);
                return true;
            }
            return false;
        });
        popup.show();
    }

    public void sortProducts(boolean lowToHigh) {
        executor.execute(() -> {
            List<Product> sortedProducts = productDAO.getAllProducts();
            Collections.sort(sortedProducts, (p1, p2) -> {
                if (lowToHigh) {
                    return Float.compare((float) p1.getPrice(), (float) p2.getPrice());
                } else {
                    return Float.compare((float) p2.getPrice(), (float) p1.getPrice());
                }
            });
            runOnUiThread(() -> {
                productDisplayAdapter.updateProducts(sortedProducts);
            });
        });
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
        } else if (itemId == R.id.menu_home) {
            // Đã ở HomeActivity, không cần làm gì
            return true;
        }

        else if (itemId == R.id.menu_cart) {
            startActivity(new Intent(this, ShoppingCartActivity.class));
            return true;
        }
        else if (itemId == R.id.menu_search) {
            startActivity(new Intent(this, SearchProductActivity.class));
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
//        else if (itemId == R.id.menu_user_profile) {
//            if (sessionManager.isLoggedIn()) {
//                startActivity(new Intent(this, ProfileActivity.class));
//            } else {
//                Toast.makeText(this, "Please log in to view your profile", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(this, LoginActivity.class));
//            }
//            return true;
//        }
        else if (itemId == R.id.menu_sort) {
            // Gọi hàm showSortMenu để hiển thị menu sort hiện có
            showSortMenu(findViewById(R.id.toolbar));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}