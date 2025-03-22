package com.he172006.onlineclothesshop;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.he172006.onlineclothesshop.DAO.CategoryDAO;
import com.he172006.onlineclothesshop.entity.Category;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class AddCategoryActivity extends AppCompatActivity {

    private EditText etCategoryName;
    private LinearLayout llImageContainer;
    private TextView tvImageInstruction;
    private Button btnAddCategory;
    private ImageButton btnBack;
    private Uri imageUri;

    // ActivityResultLauncher for image selection
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
        setContentView(R.layout.activity_add_category);

        // Initialize views
        etCategoryName = findViewById(R.id.etCategoryName);
        llImageContainer = findViewById(R.id.llImageContainer);
        tvImageInstruction = findViewById(R.id.tvImageInstruction);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        btnBack = findViewById(R.id.btnBack);

        // Handle Back button click
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Setup image upload
        llImageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickerLauncher.launch("image/*");
            }
        });

        // Handle Add Category button click
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = etCategoryName.getText().toString().trim();

                // Validate inputs
                if (categoryName.isEmpty()) {
                    Toast.makeText(AddCategoryActivity.this, "Please enter category name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (imageUri == null) {
                    Toast.makeText(AddCategoryActivity.this, "Please upload an image", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save the image to storage and get its path
                String imagePath = saveImageToStorage(imageUri);

                if (imagePath == null) {
                    Toast.makeText(AddCategoryActivity.this, "Failed to save image", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create new category
                Category category = new Category();
                category.setCategoryName(categoryName);
                category.setImage(imagePath);

                // Save to database
                CategoryDAO categoryDAO = new CategoryDAO(AddCategoryActivity.this);
                long result = categoryDAO.insertCategory(category);
                if (result != -1) {
                    Toast.makeText(AddCategoryActivity.this, "Category added successfully", Toast.LENGTH_SHORT).show();
                    categoryDAO.close();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddCategoryActivity.this, "Failed to add category", Toast.LENGTH_SHORT).show();
                    categoryDAO.close();
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
            File dir = new File(getFilesDir(), "category_images");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, "category_" + System.currentTimeMillis() + ".jpg");
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