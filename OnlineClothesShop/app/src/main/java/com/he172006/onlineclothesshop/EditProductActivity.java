package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.Arrays;
import java.util.List;

public class EditProductActivity extends AppCompatActivity {

    private TextView tvProductId, tvImageInstruction;
    private EditText etProductName, etDescription, etPrice, etStock;
    private Spinner spinnerCategory, spinnerStatus;
    private Button btnSaveChanges;
    private ImageButton btnDelete;
    private LinearLayout llImageContainer;
    private Uri imageUri; // Chỉ lưu một hình ảnh
    private List<Category> categoryList; // Lưu danh sách Category để lấy categoryId
    private Product product; // Sản phẩm đang chỉnh sửa

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
        setContentView(R.layout.activity_edit_product);

        // Initialize views
        tvProductId = findViewById(R.id.tvProductId);
        etProductName = findViewById(R.id.etProductName);
        etDescription = findViewById(R.id.etDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        etPrice = findViewById(R.id.etPrice);
        etStock = findViewById(R.id.etStock);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnDelete = findViewById(R.id.btnDelete);
        llImageContainer = findViewById(R.id.llImageContainer);
        tvImageInstruction = findViewById(R.id.tvImageInstruction);

        // Setup Status Spinner (tạm thời, không lưu vào database)
        List<String> statusList = Arrays.asList("Active", "Inactive");
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusList);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Get product ID from Intent
        int productId = getIntent().getIntExtra("productId", -1);
        if (productId == -1) {
            Toast.makeText(this, "Invalid product ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load product from database
        ProductDAO productDAO = new ProductDAO(this);
        product = productDAO.getProductById(productId);
        if (product == null) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup Category Spinner from database
        CategoryDAO categoryDAO = new CategoryDAO(this);
        categoryList = categoryDAO.getAllCategories();
        if (categoryList.isEmpty()) {
            categoryDAO.insertSampleCategories();
            categoryList = categoryDAO.getAllCategories();
        }
        List<String> categoryNames = new ArrayList<>();
        int selectedCategoryPosition = 0;
        for (int i = 0; i < categoryList.size(); i++) {
            Category category = categoryList.get(i);
            categoryNames.add(category.getCategoryName());
            if (category.getCategoryId() == product.getCategoryId()) {
                selectedCategoryPosition = i;
            }
        }
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerCategory.setSelection(selectedCategoryPosition);

        // Display product details
        tvProductId.setText("ID: " + product.getProductId()); // Hiển thị productId từ database
        etProductName.setText(product.getProductName());
        etDescription.setText(product.getDescription());
        etPrice.setText(String.valueOf(product.getPrice()));
        etStock.setText(String.valueOf(product.getStock()));
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            imageUri = Uri.fromFile(new File(product.getImage()));
        }
        updateImageDisplay();

        // Setup image upload (chỉ cho phép chọn một hình ảnh)
        llImageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickerLauncher.launch("image/*");
            }
        });

        // Handle Save Changes button click
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = etProductName.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                String priceStr = etPrice.getText().toString().trim();
                String stockStr = etStock.getText().toString().trim();

                // Validate inputs
                if (productName.isEmpty() || description.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                    Toast.makeText(EditProductActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (imageUri == null) {
                    Toast.makeText(EditProductActivity.this, "Please upload an image", Toast.LENGTH_SHORT).show();
                    return;
                }

                double price;
                int stock;
                try {
                    price = Double.parseDouble(priceStr);
                    stock = Integer.parseInt(stockStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(EditProductActivity.this, "Invalid price or stock value", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get categoryId from selected category
                int selectedPosition = spinnerCategory.getSelectedItemPosition();
                Category selectedCategory = categoryList.get(selectedPosition);
                int categoryId = selectedCategory.getCategoryId();

                // Save the image to storage and get its path (if image changed)
                String imagePath = product.getImage();
                if (imageUri != null && (product.getImage() == null || !imageUri.toString().equals(Uri.fromFile(new File(product.getImage())).toString()))) {
                    imagePath = saveImageToStorage(imageUri);
                    if (imagePath == null) {
                        Toast.makeText(EditProductActivity.this, "Failed to save image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // Update Product object
                product.setCategoryId(categoryId);
                product.setProductName(productName);
                product.setDescription(description);
                product.setPrice(price);
                product.setStock(stock);
                product.setImage(imagePath);

                // Update product in database
                ProductDAO productDAO = new ProductDAO(EditProductActivity.this);
                int result = productDAO.updateProduct(product);
                if (result > 0) {
                    Toast.makeText(EditProductActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                    productDAO.close();
                    finish();
                } else {
                    Toast.makeText(EditProductActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show();
                    productDAO.close();
                }
            }
        });



    }

    private void updateImageDisplay() {
        llImageContainer.removeAllViews();
        if (imageUri != null) {
            android.widget.ImageView imageView = new android.widget.ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
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