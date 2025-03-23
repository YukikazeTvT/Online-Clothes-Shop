package com.he172006.onlineclothesshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.he172006.onlineclothesshop.R;
import com.he172006.onlineclothesshop.entity.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{
    private Context context;
    private List<Order> orderList;
    private OnOrderClickListener listener;

    public OrderAdapter(Context context, List<Order> orderList, OnOrderClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_items, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.txtOrderId.setText("Order ID: " + order.getOrderId());
        holder.txtStatus.setText("Status: " + order.getStatus());
        holder.txtTotalAmount.setText("Total: $" + order.getTotalAmount());

        // Khi nhấn vào nút Update, chuyển đến màn hình Update Order
        holder.btnUpdate.setOnClickListener(v -> listener.onUpdateOrder(order));

        // Khi nhấn vào toàn bộ item, cũng có thể mở Update Order
        holder.itemView.setOnClickListener(v -> listener.onUpdateOrder(order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtStatus, txtTotalAmount;
        Button btnUpdate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtTotalAmount = itemView.findViewById(R.id.txtTotalAmount);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
        }
    }

    public interface OnOrderClickListener {
        void onUpdateOrder(Order order);
    }
}
