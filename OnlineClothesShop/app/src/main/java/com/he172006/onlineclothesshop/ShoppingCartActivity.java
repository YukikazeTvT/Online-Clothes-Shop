package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.os.Bundle;
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
}