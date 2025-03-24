package com.he172006.onlineclothesshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.he172006.onlineclothesshop.ProductDetail;
import com.he172006.onlineclothesshop.R;
import com.he172006.onlineclothesshop.entity.Product;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductDisplayAdapter extends RecyclerView.Adapter<ProductDisplayAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductDisplayAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = new ArrayList<>(productList);
    }

    public void updateProducts(List<Product> newProductList) {
        this.productList.clear();
        this.productList.addAll(newProductList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_home, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productName.setText(product.getProductName());

        // Định dạng giá với 1 chữ số thập phân
        DecimalFormat formatter = new DecimalFormat("#,##0.0");
        String formattedPrice = formatter.format(product.getPrice()) + "$";
        holder.productPrice.setText(formattedPrice);

        Glide.with(context)
                .load(product.getImage())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.productImage);

        // Khi nhấn vào item, chuyển sang ProductDetail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetail.class);
            intent.putExtra("product_id", product.getProductId());
            intent.putExtra("product_name", product.getProductName());
            intent.putExtra("product_price", product.getPrice());
            intent.putExtra("product_description", product.getDescription());
            intent.putExtra("product_image", product.getImage());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
        }
    }
}