package com.he172006.onlineclothesshop.adapter;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import  com.squareup.picasso.Picasso;
import com.he172006.onlineclothesshop.R;
import com.he172006.onlineclothesshop.entity.Category;
import java.io.File;

import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<Category> categoryList;
    private OnItemClickListener listener;



    public interface OnItemClickListener {
        void onItemClick(Category category);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public CategoryAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryName.setText(category.getCategoryName());



        File imageFile = new File(category.getImage());
        if (imageFile.exists()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageFile)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(holder.categoryImage);
        } else {
            holder.categoryImage.setImageResource(R.drawable.placeholder_image);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(category);
            }
        });
    }


    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView categoryImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryImage = itemView.findViewById(R.id.categoryImage);

        }
    }
}










