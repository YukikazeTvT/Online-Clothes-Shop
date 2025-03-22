package com.he172006.onlineclothesshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.he172006.onlineclothesshop.DAO.ProductDAO;
import com.he172006.onlineclothesshop.entity.Product;
import java.io.File;
import android.net.Uri;

public class DeleteProductDialogFragment extends DialogFragment {

    private static final String ARG_PRODUCT_ID = "productId";
    private Product product;

    public static DeleteProductDialogFragment newInstance(int productId) {
        DeleteProductDialogFragment fragment = new DeleteProductDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Adjust dialog width to match the screen width with some padding
        if (getDialog() != null && getDialog().getWindow() != null) {
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
            // Optional: Add dim background
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_delete_product, container, false);

        // Initialize views
        ImageView ivProductImage = view.findViewById(R.id.ivProductImage);
        TextView tvProductName = view.findViewById(R.id.tvProductName);
        TextView tvPrice = view.findViewById(R.id.tvPrice);
        TextView tvStock = view.findViewById(R.id.tvStock);
        TextView tvTotalSales = view.findViewById(R.id.tvTotalSales);
        TextView tvLastSold = view.findViewById(R.id.tvLastSold);
        Button btnDeleteProduct = view.findViewById(R.id.btnDeleteProduct);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        // Get productId from arguments
        int productId = getArguments().getInt(ARG_PRODUCT_ID, -1);
        if (productId == -1) {
            Toast.makeText(getContext(), "Invalid product ID", Toast.LENGTH_SHORT).show();
            dismiss();
            return view;
        }

        // Load product from database
        ProductDAO productDAO = new ProductDAO(getContext());
        product = productDAO.getProductById(productId);
        if (product == null) {
            Toast.makeText(getContext(), "Product not found", Toast.LENGTH_SHORT).show();
            dismiss();
            return view;
        }

        // Display product details
        tvProductName.setText(product.getProductName());
        tvPrice.setText("Price: $" + String.format("%.2f", product.getPrice()));
        tvStock.setText("Stock: " + product.getStock() + " units");

        // Since we don't have sales history in the database, set default values
        tvTotalSales.setText("Total Sales: 0 units");
        tvLastSold.setText("Last Sold: N/A");

        // Load product image
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            ivProductImage.setImageURI(Uri.fromFile(new File(product.getImage())));
        } else {
            ivProductImage.setImageResource(android.R.drawable.ic_menu_gallery); // Placeholder image
        }

        // Handle Delete button click
        btnDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDAO productDAO = new ProductDAO(getContext());
                boolean deleted = productDAO.deleteProduct(product.getProductId());
                if (deleted) {
                    Toast.makeText(getContext(), "Product deleted successfully", Toast.LENGTH_SHORT).show();
                    // Notify the activity to refresh the product list
                    if (getActivity() instanceof ManageProductActivity) {
                        ((ManageProductActivity) getActivity()).refreshProductList();
                    }
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Failed to delete product", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle Cancel button click
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}