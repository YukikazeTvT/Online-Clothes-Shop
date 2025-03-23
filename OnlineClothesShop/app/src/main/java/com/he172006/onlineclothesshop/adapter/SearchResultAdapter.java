package com.he172006.onlineclothesshop.adapter;








import android.content.Context;
import android.content.Intent;
import android.util.Log;
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


import java.util.List;




public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    private Context context;
    private List<Product> searchResults;








    public SearchResultAdapter(Context context, List<Product> searchResults) {
        this.context = context;
        this.searchResults = searchResults;
    }








    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }








    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = searchResults.get(position);
        holder.productName.setText(product.getProductName());
        holder.productPrice.setText(product.getPrice() + "đ");








        Glide.with(context)
                .load(product.getImage())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.productImage);
        holder.itemView.setOnClickListener(v -> {
            Log.d("ProductAdapter", "Product Name: " + product.getProductName());
            Log.d("ProductAdapter", "Product Price: " + product.getPrice());
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
        return searchResults.size();
    }








    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice;








        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.searchProductImage);
            productName = itemView.findViewById(R.id.searchProductName);
            productPrice = itemView.findViewById(R.id.searchProductPrice);
        }
    }
}










