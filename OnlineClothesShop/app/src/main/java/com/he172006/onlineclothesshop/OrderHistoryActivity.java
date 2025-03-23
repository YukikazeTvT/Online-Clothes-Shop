package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.he172006.onlineclothesshop.DAO.OrderDAO;
import com.he172006.onlineclothesshop.adapter.OrderAdapter;
import com.he172006.onlineclothesshop.entity.Order;

import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private OrderDAO orderDAO;
    private TextView txtNoOrders;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Ánh xạ UI
        recyclerView = findViewById(R.id.recyclerViewOrderHistory);
        txtNoOrders = findViewById(R.id.txtNoOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo OrderDAO
        orderDAO = new OrderDAO(this);

        // Lấy accountId từ Intent (user đã đăng nhập)
        int accountId = getIntent().getIntExtra("accountId", -1);
        if (accountId != -1) {
            loadOrderHistory(accountId);
        } else {
            txtNoOrders.setText("User ID not found");
        }
    }

    private void loadOrderHistory(int accountId) {
        List<Order> orderList = orderDAO.getOrdersByAccountId(accountId);

        if (orderList.isEmpty()) {
            txtNoOrders.setText("No orders found.");
            recyclerView.setVisibility(RecyclerView.GONE);
        } else {
            txtNoOrders.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);
            orderAdapter = new OrderAdapter(this, orderList, order -> {
                // Khi nhấn vào Order trong lịch sử, mở màn hình chi tiết đơn hàng
                Intent intent = new Intent(OrderHistoryActivity.this, OrderDetailActivity.class);
                intent.putExtra("orderId", order.getOrderId());
                startActivity(intent);
            });
            recyclerView.setAdapter(orderAdapter);
        }
    }
}
