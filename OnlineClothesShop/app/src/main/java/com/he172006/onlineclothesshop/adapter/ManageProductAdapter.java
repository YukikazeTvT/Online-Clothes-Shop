package com.he172006.onlineclothesshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.he172006.onlineclothesshop.DAO.CategoryDAO;
import com.he172006.onlineclothesshop.DeleteProductDialogFragment;
import com.he172006.onlineclothesshop.EditProductActivity;
import com.he172006.onlineclothesshop.R;
import com.he172006.onlineclothesshop.entity.Product;
import java.io.File;
import java.util.List;

    public class ManageProductAdapter extends RecyclerView.Adapter<ManageProductAdapter.ProductViewHolder> {

        private Context context;
        private List<Product> productList;

        public ManageProductAdapter(Context context, List<Product> productList) {
            this.context = context;
            this.productList = productList;
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_manage_product, parent, false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            Product product = productList.get(position);

            // Set product details
            holder.tvProductName.setText(product.getProductName());
            holder.tvDescription.setText(product.getDescription());
            holder.tvPrice.setText("Price: $" + String.format("%.2f", product.getPrice()));
            holder.tvStock.setText("Stock: " + product.getStock());

            // Load category name
            CategoryDAO categoryDAO = new CategoryDAO(context);
            String categoryName = categoryDAO.getCategoryNameById(product.getCategoryId());
            holder.tvCategory.setText("Category: " + (categoryName != null ? categoryName : "Unknown"));

            // Load product image
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                holder.ivProductImage.setImageURI(Uri.fromFile(new File(product.getImage())));
            } else {
                holder.ivProductImage.setImageResource(android.R.drawable.ic_menu_gallery); // Placeholder image
            }

            // Handle Edit button click
            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EditProductActivity.class);
                    intent.putExtra("productId", product.getProductId());
                    context.startActivity(intent);
                }
            });

            // Handle Delete button click
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show DeleteProductDialogFragment
                    DeleteProductDialogFragment dialog = DeleteProductDialogFragment.newInstance(product.getProductId());
                    dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "DeleteProductDialog");
                }
            });
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        public static class ProductViewHolder extends RecyclerView.ViewHolder {
            ImageView ivProductImage;
            TextView tvProductName, tvDescription, tvCategory, tvPrice, tvStock;
            Button btnEdit, btnDelete;

            public ProductViewHolder(@NonNull View itemView) {
                super(itemView);
                ivProductImage = itemView.findViewById(R.id.ivProductImage);
                tvProductName = itemView.findViewById(R.id.tvProductName);
                tvDescription = itemView.findViewById(R.id.tvDescription);
                tvCategory = itemView.findViewById(R.id.tvCategory);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvStock = itemView.findViewById(R.id.tvStock);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }