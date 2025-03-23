package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.he172006.onlineclothesshop.DAO.OrderDAO;
import com.he172006.onlineclothesshop.entity.Order;

public class UpdateOrderActivity extends AppCompatActivity {
    private TextView tvOrderId, tvOrderDate, tvTotalAmount;
    private Spinner spinnerStatus;
    private Button btnUpdateOrder;
    private OrderDAO orderDAO;
    private int orderId;
    private String selectedStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_order);

        // Ánh xạ UI
        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnUpdateOrder = findViewById(R.id.btnUpdateOrder);

        // Khởi tạo DAO
        orderDAO = new OrderDAO(this);

        // Lấy orderId từ Intent
        Intent intent = getIntent();
        orderId = intent.getIntExtra("orderId", -1);

        if (orderId == -1) {
            Toast.makeText(this, "Error: Order not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy thông tin đơn hàng từ DB
        Order order = orderDAO.getOrderById(orderId);
        if (order != null) {
            tvOrderId.setText("Order ID: " + order.getOrderId());
            tvOrderDate.setText("Order Date: " + order.getOrderDate());
            tvTotalAmount.setText("Total Amount: $" + order.getTotalAmount());

            // Set up Spinner với giá trị từ danh sách trạng thái
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this, R.array.order_status_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerStatus.setAdapter(adapter);

            // Đặt trạng thái hiện tại
            int spinnerPosition = adapter.getPosition(order.getStatus());
            spinnerStatus.setSelection(spinnerPosition);
        }

        // Lắng nghe sự kiện chọn trạng thái
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStatus = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedStatus = order.getStatus();
            }
        });

        // Xử lý nút cập nhật trạng thái
        btnUpdateOrder.setOnClickListener(v -> {
            if (selectedStatus != null) {
                order.setStatus(selectedStatus);
                int result = orderDAO.updateOrder(order);

                if (result > 0) {
                    Toast.makeText(UpdateOrderActivity.this, "Order updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UpdateOrderActivity.this, "Failed to update order.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
