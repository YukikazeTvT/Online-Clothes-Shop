package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.he172006.onlineclothesshop.DAO.CategoryDAO;
import com.he172006.onlineclothesshop.DAO.ProductDAO;
import com.he172006.onlineclothesshop.entity.Category;
import com.he172006.onlineclothesshop.entity.Product;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etProductName, etDescription, etPrice, etStock;
    private Spinner spinnerCategory;
    private Button btnAddProduct;
    private LinearLayout llImageContainer;
    private TextView tvImageInstruction;
    private Uri imageUri; // Chỉ lưu một hình ảnh
    private List<Category> categoryList; // Lưu danh sách Category để lấy categoryId

    // ActivityResultLauncher for image selection (chỉ chọn một hình ảnh)
    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            result -> {
                if (result != null) {
                    imageUri = result;
                    updateImageDisplay();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize views
        ivBack = findViewById(R.id.ivBack);
        etProductName = findViewById(R.id.etProductName);
        etDescription = findViewById(R.id.etDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        etPrice = findViewById(R.id.etPrice);
        etStock = findViewById(R.id.etStock);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        llImageContainer = findViewById(R.id.llImageContainer);
        tvImageInstruction = findViewById(R.id.tvImageInstruction);

        // Setup Category Spinner from database
        CategoryDAO categoryDAO = new CategoryDAO(this);
        categoryList = categoryDAO.getAllCategories();
        if (categoryList.isEmpty()) {
            // Insert sample categories if the table is empty
            categoryDAO.insertSampleCategories();
            categoryList = categoryDAO.getAllCategories();
        }
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categoryList) {
            categoryNames.add(category.getCategoryName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        categoryDAO.close();

        // Setup image upload (chỉ cho phép chọn một hình ảnh)
        llImageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickerLauncher.launch("image/*");
            }
        });

        // Handle Back button click
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại ManageProductActivity
            }
        });

        // Handle Add Product button click
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = etProductName.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                String priceStr = etPrice.getText().toString().trim();
                String stockStr = etStock.getText().toString().trim();

                // Validate inputs
                if (productName.isEmpty() || description.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (imageUri == null) {
                    Toast.makeText(AddProductActivity.this, "Please upload an image", Toast.LENGTH_SHORT).show();
                    return;
                }

                double price;
                int stock;
                try {
                    price = Double.parseDouble(priceStr);
                    stock = Integer.parseInt(stockStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(AddProductActivity.this, "Invalid price or stock value", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get categoryId from selected category
                int selectedPosition = spinnerCategory.getSelectedItemPosition();
                Category selectedCategory = categoryList.get(selectedPosition);
                int categoryId = selectedCategory.getCategoryId();

                // Save the image to storage and get its path
                String imagePath = saveImageToStorage(imageUri);

                if (imagePath == null) {
                    Toast.makeText(AddProductActivity.this, "Failed to save image", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create Product object
                Product product = new Product();
                product.setCategoryId(categoryId);
                product.setProductName(productName);
                product.setDescription(description);
                product.setPrice(price);
                product.setStock(stock);
                product.setImage(imagePath);

                // Save product to database using ProductDAO
                ProductDAO productDAO = new ProductDAO(AddProductActivity.this);
                long result = productDAO.insertProduct(product);
                if (result != -1) {
                    Toast.makeText(AddProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                    productDAO.close();
                    setResult(RESULT_OK); // Trả về kết quả để ManageProductActivity cập nhật danh sách
                    finish(); // Quay lại ManageProductActivity
                } else {
                    Toast.makeText(AddProductActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                    productDAO.close();
                }
            }
        });
    }

    private void updateImageDisplay() {
        llImageContainer.removeAllViews();
        if (imageUri != null) {
            android.widget.ImageView imageView = new android.widget.ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(80, 80));
            imageView.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
            imageView.setImageURI(imageUri);
            imageView.setPadding(4, 4, 4, 4);
            llImageContainer.addView(imageView);
            tvImageInstruction.setText("Image uploaded");
        } else {
            tvImageInstruction.setText("Upload an image");
        }
    }

    private String saveImageToStorage(Uri uri) {
        try {
            File dir = new File(getFilesDir(), "product_images");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, "product_" + System.currentTimeMillis() + ".jpg");
            InputStream inputStream = getContentResolver().openInputStream(uri);
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}