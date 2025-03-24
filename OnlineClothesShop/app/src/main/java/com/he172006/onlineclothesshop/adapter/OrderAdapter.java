package com.he172006.onlineclothesshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.he172006.onlineclothesshop.ProductDetail;
import com.he172006.onlineclothesshop.R;
import com.he172006.onlineclothesshop.DAO.ProductDAO;
import com.he172006.onlineclothesshop.entity.OrderDetail;
import com.he172006.onlineclothesshop.entity.Product;

import java.text.DecimalFormat;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<OrderDetail> orderDetailList;
    private ProductDAO productDAO;
    private int orderId;
    private String orderDate;

    public OrderAdapter(Context context, List<OrderDetail> orderDetailList, int orderId, String orderDate) {
        this.context = context;
        this.orderDetailList = orderDetailList;
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.productDAO = new ProductDAO(context);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderDetail orderDetail = orderDetailList.get(position);
        Product product = productDAO.getProductById(orderDetail.getProductId());

        if (product != null) {
            Glide.with(context).load(product.getImage()).into(holder.imgProduct);
            holder.txtDescription.setText(product.getDescription());
            holder.txtOrderId.setText("Order #" + orderId);
            holder.txtOrderDate.setText(orderDate);
            holder.txtQuantity.setText("Quantity: " + orderDetail.getQuantity());
            DecimalFormat formatter = new DecimalFormat("#,##0");
            holder.txtTotalPrice.setText("Total: USD$" + formatter.format(orderDetail.getSubtotal()));

            holder.btnReview.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProductDetail.class);
                intent.putExtra("product_id", product.getProductId());
                intent.putExtra("product_name", product.getProductName());
                intent.putExtra("product_price", product.getPrice());
                intent.putExtra("product_description", product.getDescription());
                intent.putExtra("product_image", product.getImage());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtDescription, txtOrderId, txtOrderDate, txtQuantity, txtTotalPrice;
        Button btnReview;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtOrderDate = itemView.findViewById(R.id.txtOrderDate);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtTotalPrice = itemView.findViewById(R.id.txtTotalPrice);
            btnReview = itemView.findViewById(R.id.btnReview);
        }
    }
}