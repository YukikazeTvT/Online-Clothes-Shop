package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.he172006.onlineclothesshop.DAO.OrderDAO;
import com.he172006.onlineclothesshop.adapter.OrderAdapter;
import com.he172006.onlineclothesshop.entity.Order;

import java.util.List;

public class ManageOrderActivity extends AppCompatActivity {
    private RecyclerView recyclerViewOrders;
    private OrderAdapter orderAdapter;
    private OrderDAO orderDAO;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_order);

        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));

        orderDAO = new OrderDAO(this);
        loadOrders();
    }

    private void loadOrders() {
        orderList = orderDAO.getAllOrders();
        if (orderList != null && !orderList.isEmpty()) {
            orderAdapter = new OrderAdapter(this, orderList, order -> {
                Intent intent = new Intent(ManageOrderActivity.this, UpdateOrderActivity.class);
                intent.putExtra("orderId", order.getOrderId());
                startActivity(intent);
            });
            recyclerViewOrders.setAdapter(orderAdapter);
        } else {
            Toast.makeText(this, "No orders found!", Toast.LENGTH_SHORT).show();
        }
    }
}
