package com.he172006.onlineclothesshop;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.he172006.onlineclothesshop.DAO.OrderDAO;
import com.he172006.onlineclothesshop.entity.Order;

public class OrderDetailActivity extends AppCompatActivity {
    private TextView txtOrderId, txtAccountId, txtOrderDate, txtTotalAmount, txtStatus;
    private OrderDAO orderDAO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Ánh xạ UI
        txtOrderId = findViewById(R.id.txtOrderId);
        txtAccountId = findViewById(R.id.txtAccountId);
        txtOrderDate = findViewById(R.id.txtOrderDate);
        txtTotalAmount = findViewById(R.id.txtTotalAmount);
        txtStatus = findViewById(R.id.txtStatus);

        // Khởi tạo DAO
        orderDAO = new OrderDAO(this);

        // Lấy orderId từ Intent
        int orderId = getIntent().getIntExtra("orderId", -1);
        if (orderId != -1) {
            loadOrderDetails(orderId);
        } else {
            txtOrderId.setText("Order not found");
        }
    }

    private void loadOrderDetails(int orderId) {
        Order order = orderDAO.getOrderById(orderId);
        if (order != null) {
            txtOrderId.setText("Order ID: " + order.getOrderId());
            txtAccountId.setText("Account ID: " + order.getAccountId());
            txtOrderDate.setText("Order Date: " + order.getOrderDate());
            txtTotalAmount.setText("Total Amount: $" + order.getTotalAmount());
            txtStatus.setText("Status: " + order.getStatus());
        } else {
            txtOrderId.setText("Order not found");
        }
    }
}
