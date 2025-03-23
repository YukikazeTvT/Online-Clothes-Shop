package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.he172006.onlineclothesshop.DAO.CartDAO;
import com.he172006.onlineclothesshop.DAO.OrderDAO;
import com.he172006.onlineclothesshop.DAO.ProductDAO;
import com.he172006.onlineclothesshop.entity.Cart;
import com.he172006.onlineclothesshop.entity.Order;
import com.he172006.onlineclothesshop.entity.Product;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CheckoutActivity extends AppCompatActivity {

    private TextView txtSubtotal, txtShipping, txtTotal;
    private Button btnConfirmCheckout;
    private CartDAO cartDAO;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private ArrayList<Cart> cartItems;
    private double totalPrice;
    private Session sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize SessionManager
        sessionManager = new Session(this);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle = toolbar.findViewById(R.id.tvTitle);
        if (tvTitle != null) {
            tvTitle.setText("CHECKOUT");
        }

        cartDAO = new CartDAO(this);
        productDAO = new ProductDAO(this);
        orderDAO = new OrderDAO(this);

        txtSubtotal = findViewById(R.id.txtSubtotal);
        txtShipping = findViewById(R.id.txtShipping);
        txtTotal = findViewById(R.id.txtTotal);
        btnConfirmCheckout = findViewById(R.id.btnConfirmCheckout);

        // Lấy danh sách sản phẩm từ Intent
        Intent intent = getIntent();
        cartItems = (ArrayList<Cart>) intent.getSerializableExtra("cartItems");

        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "No items to checkout", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        calculateOrderSummary();

        btnConfirmCheckout.setOnClickListener(v -> confirmCheckout());
    }

    private void calculateOrderSummary() {
        totalPrice = 0;
        for (Cart cart : cartItems) {
            Product product = productDAO.getProductById(cart.getProductId());
            if (product != null) {
                totalPrice += product.getPrice() * cart.getQuantity();
            }
        }

        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        txtSubtotal.setText("Subtotal: USD$" + formatter.format(totalPrice));
        txtShipping.setText("Shipping: Free express");
        txtTotal.setText("Total: USD$" + formatter.format(totalPrice));
    }

    private void confirmCheckout() {
        // Lấy accountId từ session
        int accountId = sessionManager.getAccountId();
        if (accountId == -1) {
            Toast.makeText(this, "Please log in to checkout", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Kiểm tra số lượng tồn kho trước khi checkout
        for (Cart cart : cartItems) {
            Product product = productDAO.getProductById(cart.getProductId());
            if (product == null) {
                Toast.makeText(this, "Sản phẩm không tồn tại!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (product.getStock() < cart.getQuantity()) {
                Toast.makeText(this, "Sản phẩm " + product.getProductName() + " không đủ hàng!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Tạo đơn hàng mới
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String orderDate = sdf.format(new Date());
        Order order = new Order();
        order.setAccountId(accountId);
        order.setOrderDate(orderDate);
        order.setTotalAmount(totalPrice);
        order.setStatus("Pending");

        // Lưu đơn hàng vào bảng Orders
        long orderId = orderDAO.insertOrder(order);
        if (orderId == -1) {
            Toast.makeText(this, "Failed to create order", Toast.LENGTH_SHORT).show();
            return;
        }

        // Giảm số lượng tồn kho và xóa giỏ hàng
        for (Cart cart : cartItems) {
            Product product = productDAO.getProductById(cart.getProductId());
            if (product != null) {
                // Giảm số lượng tồn kho
                product.setStock(product.getStock() - cart.getQuantity());
                productDAO.updateProduct(product);

                // Xóa sản phẩm khỏi giỏ hàng
                cartDAO.deleteCart(cart.getCartId());
            }
        }

        // Hiển thị thông báo "Mua hàng thành công" và kết thúc activity
        Toast.makeText(this, "Mua hàng thành công!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cartDAO != null) {
            cartDAO.close();
        }
        if (productDAO != null) {
            productDAO.close();
        }
        if (orderDAO != null) {
            orderDAO.close();
        }
    }
}