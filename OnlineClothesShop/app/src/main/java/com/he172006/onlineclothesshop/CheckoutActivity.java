package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.he172006.onlineclothesshop.DAO.CartDAO;
import com.he172006.onlineclothesshop.DAO.OrderDAO;
import com.he172006.onlineclothesshop.DAO.OrderDetailDAO;
import com.he172006.onlineclothesshop.DAO.ProductDAO;
import com.he172006.onlineclothesshop.entity.Cart;
import com.he172006.onlineclothesshop.entity.Order;
import com.he172006.onlineclothesshop.entity.OrderDetail;
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
    private OrderDetailDAO orderDetailDAO; // Thêm OrderDetailDAO
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

        // Khởi tạo các DAO
        cartDAO = new CartDAO(this);
        productDAO = new ProductDAO(this);
        orderDAO = new OrderDAO(this);
        orderDetailDAO = new OrderDetailDAO(this); // Khởi tạo OrderDetailDAO

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

        // Lưu chi tiết đơn hàng vào bảng OrderDetails
        for (Cart cart : cartItems) {
            Product product = productDAO.getProductById(cart.getProductId());
            if (product != null) {
                // Tính subtotal cho sản phẩm này
                double subtotal = product.getPrice() * cart.getQuantity();

                // Tạo bản ghi OrderDetail
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderId((int) orderId);
                orderDetail.setProductId(cart.getProductId());
                orderDetail.setQuantity(cart.getQuantity());
                orderDetail.setSubtotal(subtotal);

                // Lưu vào bảng OrderDetails
                long orderDetailId = orderDetailDAO.insertOrderDetail(orderDetail);
                if (orderDetailId == -1) {
                    Toast.makeText(this, "Failed to save order details for product ID: " + cart.getProductId(), Toast.LENGTH_SHORT).show();
                    return;
                }

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
        if (orderDetailDAO != null) {
            orderDetailDAO.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.menu_sort).setVisible(false);
        return true;
    }

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
        else if (itemId == R.id.menu_user_profile) {
            if (sessionManager.isLoggedIn()) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else {
                Toast.makeText(this, "Please log in to view your profile", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            }
            return true;
        }
        else if (itemId == R.id.menu_user_profile) {
            if (sessionManager.isLoggedIn()) {
                Toast.makeText(this, "User Profile clicked", Toast.LENGTH_SHORT).show();
                // TODO: Start ProfileActivity if exists
            } else {
                Toast.makeText(this, "Please log in to view your profile", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}