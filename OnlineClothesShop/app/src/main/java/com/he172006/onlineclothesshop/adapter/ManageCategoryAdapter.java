package com.he172006.onlineclothesshop.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.he172006.onlineclothesshop.R;
import com.he172006.onlineclothesshop.entity.Category;
import java.io.File;
import java.util.List;

public class ManageCategoryAdapter extends RecyclerView.Adapter<ManageCategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categoryList;
    private OnCategoryActionListener listener;

    public interface OnCategoryActionListener {
        void onEditCategory(int position);
        void onDeleteCategory(int position);
    }

    public ManageCategoryAdapter(Context context, List<Category> categoryList, OnCategoryActionListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);

        // Display category name
        holder.tvCategoryName.setText(category.getCategoryName());

        // Display category image (if available)
        if (category.getImage() != null && !category.getImage().isEmpty()) {
            Uri imageUri = Uri.fromFile(new File(category.getImage()));
            holder.ivCategoryImage.setImageURI(imageUri);
        } else {
            holder.ivCategoryImage.setImageResource(android.R.drawable.ic_menu_gallery); // Default image if none exists
        }

        // Handle Edit button click
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditCategory(position);
            }
        });

        // Handle Delete button click
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteCategory(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryImage;
        TextView tvCategoryName;
        Button btnEdit;
        Button btnDelete;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryImage = itemView.findViewById(R.id.ivCategoryImage);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}