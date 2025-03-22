package com.he172006.onlineclothesshop.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.he172006.onlineclothesshop.R;
import com.he172006.onlineclothesshop.entity.Category;
import java.io.File;
import java.util.List;

public class CategorySliderAdapter extends RecyclerView.Adapter<CategorySliderAdapter.SliderViewHolder> {

    private Context context;
    private List<Category> categoryList;

    public CategorySliderAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        Category category = categoryList.get(position);

        // Hiển thị tên danh mục
        holder.tvCategoryName.setText(category.getCategoryName());

        // Hiển thị hình ảnh danh mục (nếu có)
        if (category.getImage() != null && !category.getImage().isEmpty()) {
            Uri imageUri = Uri.fromFile(new File(category.getImage()));
            holder.ivCategoryImage.setImageURI(imageUri);
        } else {
            holder.ivCategoryImage.setImageResource(android.R.drawable.ic_menu_gallery); // Hình ảnh mặc định nếu không có ảnh
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryImage;
        TextView tvCategoryName;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryImage = itemView.findViewById(R.id.ivCategoryImage);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}