package com.he172006.onlineclothesshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.he172006.onlineclothesshop.DAO.ProductDAO;
import com.he172006.onlineclothesshop.ProductDetail;
import com.he172006.onlineclothesshop.R;
import com.he172006.onlineclothesshop.entity.OrderDetail;
import com.he172006.onlineclothesshop.entity.Product;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {

    private Context context;
    private List<OrderDetail> orderDetailList;
    private ProductDAO productDAO;

    public OrderDetailAdapter(Context context, List<OrderDetail> orderDetailList, ProductDAO productDAO) {
        this.context = context;
        this.orderDetailList = orderDetailList;
        this.productDAO = productDAO;
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderDetail orderDetail = orderDetailList.get(position);

        Product product = productDAO.getProductById(orderDetail.getProductId());
        if (product != null) {
            holder.txtProductName.setText(product.getProductName());

            // Hiển thị ảnh sản phẩm từ đường dẫn cục bộ
            String imagePath = product.getImage();
            Log.d("OrderDetailAdapter", "Product ID: " + product.getProductId() + ", Image Path: " + imagePath);

            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Glide.with(context)
                            .load(imageFile)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.placeholder_image)
                            .into(holder.imgProduct);
                } else {
                    Log.e("OrderDetailAdapter", "Image file does not exist: " + imagePath);
                    holder.imgProduct.setImageResource(R.drawable.placeholder_image);
                }
            } else {
                Log.e("OrderDetailAdapter", "Image path is null or empty for Product ID: " + product.getProductId());
                holder.imgProduct.setImageResource(R.drawable.placeholder_image);
            }

            // Xử lý nút Review
            holder.btnReview.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProductDetail.class);
                intent.putExtra("product_id", product.getProductId());
                intent.putExtra("product_name", product.getProductName());
                intent.putExtra("product_price", product.getPrice());
                intent.putExtra("product_description", product.getDescription());
                intent.putExtra("product_image", product.getImage());
                context.startActivity(intent);
            });
        } else {
            holder.txtProductName.setText("Unknown Product");
            holder.imgProduct.setImageResource(R.drawable.placeholder_image);
            holder.btnReview.setEnabled(false); // Vô hiệu hóa nút Review nếu không tìm thấy sản phẩm
        }

        holder.txtQuantity.setText("Quantity: " + orderDetail.getQuantity());
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        holder.txtSubtotal.setText("Subtotal: USD$" + formatter.format(orderDetail.getSubtotal()));
    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }

    static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtProductName, txtQuantity, txtSubtotal;
        Button btnReview;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtSubtotal = itemView.findViewById(R.id.txtSubtotal);
            btnReview = itemView.findViewById(R.id.btnReview);
        }
    }
}