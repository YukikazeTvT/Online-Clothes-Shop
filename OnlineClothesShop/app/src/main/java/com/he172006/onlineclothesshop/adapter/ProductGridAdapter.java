package com.he172006.onlineclothesshop.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.he172006.onlineclothesshop.R;
import com.he172006.onlineclothesshop.entity.Product;
import java.io.File;
import java.util.List;

public class ProductGridAdapter extends ArrayAdapter<Product> {

    private Context context;
    private List<Product> productList;

    public ProductGridAdapter(@NonNull Context context, List<Product> productList) {
        super(context, 0, productList);
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_product_grid, parent, false);
        }

        Product product = productList.get(position);

        ImageView ivProductImage = convertView.findViewById(R.id.ivProductImage);
        TextView tvProductName = convertView.findViewById(R.id.tvProductName);
        TextView tvProductPrice = convertView.findViewById(R.id.tvProductPrice);

        // Hiển thị tên sản phẩm
        tvProductName.setText(product.getProductName());

        // Hiển thị giá sản phẩm
        tvProductPrice.setText("$" + String.format("%.2f", product.getPrice()));

        // Hiển thị hình ảnh sản phẩm (nếu có)
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            File imageFile = new File(product.getImage());
            if (imageFile.exists()) {
                Uri imageUri = Uri.fromFile(imageFile);
                ivProductImage.setImageURI(imageUri);
            } else {
                ivProductImage.setImageResource(android.R.drawable.ic_menu_gallery); // Hình ảnh mặc định nếu file không tồn tại
            }
        } else {
            ivProductImage.setImageResource(android.R.drawable.ic_menu_gallery); // Hình ảnh mặc định nếu không có ảnh
        }

        return convertView;
    }
}