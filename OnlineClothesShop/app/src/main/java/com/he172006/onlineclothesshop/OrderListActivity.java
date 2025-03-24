package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.he172006.onlineclothesshop.DAO.OrderDAO;
import com.he172006.onlineclothesshop.DAO.OrderDetailDAO;
import com.he172006.onlineclothesshop.adapter.OrderAdapter;
import com.he172006.onlineclothesshop.entity.Order;
import com.he172006.onlineclothesshop.entity.OrderDetail;

import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrders;
    private OrderDAO orderDAO;
    private OrderDetailDAO orderDetailDAO;
    private Session sessionManager;
    private List<Order> orderList;
    private List<List<OrderDetail>> orderDetailLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        // Initialize SessionManager
        sessionManager = new Session(this);

        orderDAO = new OrderDAO(this);
        orderDetailDAO = new OrderDetailDAO(this);

        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));

        loadOrders();
    }

    private void loadOrders() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please log in to view your order history", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        int accountId = sessionManager.getAccountId();
        orderList = orderDAO.getOrdersByAccountId(accountId);
        orderDetailLists = new ArrayList<>();

        // Lấy chi tiết đơn hàng cho từng đơn hàng
        for (Order order : orderList) {
            List<OrderDetail> orderDetails = orderDetailDAO.getOrderDetailsByOrderId(order.getOrderId());
            orderDetailLists.add(orderDetails);
        }

        // Tạo danh sách các adapter cho từng đơn hàng
        List<OrderAdapter> adapters = new ArrayList<>();
        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            List<OrderDetail> orderDetails = orderDetailLists.get(i);
            OrderAdapter adapter = new OrderAdapter(this, orderDetails, order.getOrderId(), order.getOrderDate());
            adapters.add(adapter);
        }

        // Kết hợp tất cả các adapter vào một adapter duy nhất
        ConcatAdapter concatAdapter = new ConcatAdapter(adapters);
        recyclerViewOrders.setAdapter(concatAdapter);
    }
}