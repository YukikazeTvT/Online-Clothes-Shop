package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.he172006.onlineclothesshop.DAO.CartDAO;
import com.he172006.onlineclothesshop.adapter.CartAdapter;
import com.he172006.onlineclothesshop.entity.Cart;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity implements CartAdapter.OnCartChangeListener {

    private RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    private CartDAO cartDAO;
    private List<Cart> cartList;
    private TextView txtTotalPrice;
    private CheckBox chkSelectAll;
    private Button btnDeleteSelected, btnCheckout;
    private Session sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        // Initialize SessionManager
        sessionManager = new Session(this);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Ẩn tiêu đề mặc định
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Thêm nút quay lại
        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_revert); // Icon nút quay lại

        TextView tvTitle = toolbar.findViewById(R.id.tvTitle);
        if (tvTitle != null) {
            tvTitle.setText("SHOPPING BAG");
        }

        cartDAO = new CartDAO(this);
        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        chkSelectAll = findViewById(R.id.chkSelectAll);
        btnDeleteSelected = findViewById(R.id.btnDeleteSelected);
        btnCheckout = findViewById(R.id.btnCheckout);

        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));

        loadCartItems();

        chkSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartAdapter.selectAll(isChecked);
        });

        btnDeleteSelected.setOnClickListener(v -> {
            cartAdapter.removeSelectedItems();
        });

        btnCheckout.setOnClickListener(v -> {
            List<Cart> selectedItems = cartAdapter.getSelectedItems();
            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Please select at least one item to checkout", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, CheckoutActivity.class);
            intent.putExtra("cartItems", new ArrayList<>(selectedItems));
            startActivity(intent);
        });
    }

    private void loadCartItems() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please log in to view your cart", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        int accountId = sessionManager.getAccountId();
        cartList = cartDAO.getCartItemsByAccountId(accountId);
        cartAdapter = new CartAdapter(this, cartList, this);
        recyclerViewCart.setAdapter(cartAdapter);
        updateTotalPrice();
    }

    @Override
    public void onCartChanged(double totalPrice) {
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double total = cartAdapter.calculateTotalPrice();
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        txtTotalPrice.setText("Total: USD$" + formatter.format(total));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems(); // Làm mới danh sách giỏ hàng khi quay lại
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cartAdapter != null) {
            cartAdapter.close();
        }
        if (cartDAO != null) {
            cartDAO.close();
        }
    }

    // Thêm menu vào Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Ẩn các mục menu không cần thiết
        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.menu_sort).setVisible(false);
        menu.findItem(R.id.menu_cart).setVisible(false); // Ẩn mục "Cart" vì đang ở ShoppingCartActivity
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
        } else if (itemId == R.id.menu_orders) {
            if (sessionManager.isLoggedIn()) {
                startActivity(new Intent(this, OrderHistoryActivity.class));
            } else {
                Toast.makeText(this, "Please log in to view your orders", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            }
            return true;
        }  else if (itemId == R.id.menu_user_profile) {
            if (sessionManager.isLoggedIn()) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else {
                Toast.makeText(this, "Please log in to view your profile", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            }
            return true;
        }
        //        else if (itemId == R.id.menu_categories) {
//            startActivity(new Intent(this, CategoriesActivity.class));
//            return true;
//        }
//        else if (itemId == R.id.menu_cart) {
//
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}

