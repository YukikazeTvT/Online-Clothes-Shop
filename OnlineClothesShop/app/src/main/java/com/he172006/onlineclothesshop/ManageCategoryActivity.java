package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.he172006.onlineclothesshop.DAO.CategoryDAO;
import com.he172006.onlineclothesshop.adapter.ManageCategoryAdapter;
import com.he172006.onlineclothesshop.entity.Category;
import java.io.File;
import java.util.List;

public class ManageCategoryActivity extends AppCompatActivity implements ManageCategoryAdapter.OnCategoryActionListener {

    private RecyclerView rvCategoryList;
    private ManageCategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private FloatingActionButton btnAddCategory;
    private ActivityResultLauncher<Intent> addCategoryLauncher;
    private ActivityResultLauncher<Intent> editCategoryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);

        // Initialize views
        rvCategoryList = findViewById(R.id.rvCategoryList);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        rvCategoryList.setLayoutManager(new LinearLayoutManager(this));

        // Load categories from database
        refreshCategoryList();

        // Initialize launcher for adding category
        addCategoryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        refreshCategoryList();
                    }
                });

        // Initialize launcher for editing category
        editCategoryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        refreshCategoryList();
                    }
                });

        // Handle Add Category button click
        btnAddCategory.setOnClickListener(v -> {
            Intent intent = new Intent(ManageCategoryActivity.this, AddCategoryActivity.class);
            addCategoryLauncher.launch(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCategoryList();
    }

    public void refreshCategoryList() {
        CategoryDAO categoryDAO = new CategoryDAO(this);
        categoryList = categoryDAO.getAllCategories();
        categoryAdapter = new ManageCategoryAdapter(this, categoryList, this);
        rvCategoryList.setAdapter(categoryAdapter);
        categoryDAO.close();
    }

    @Override
    public void onEditCategory(int position) {
        Category category = categoryList.get(position);
        Intent intent = new Intent(ManageCategoryActivity.this, EditCategoryActivity.class);
        intent.putExtra("categoryId", category.getCategoryId());
        editCategoryLauncher.launch(intent);
    }

    @Override
    public void onDeleteCategory(int position) {
        Category category = categoryList.get(position);
        showDeleteConfirmationDialog(category, position);
    }

    private void showDeleteConfirmationDialog(Category category, int position) {
        // Inflate the custom dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_category, null);

        // Initialize dialog views
        ImageView ivCategoryImage = dialogView.findViewById(R.id.ivCategoryImage);
        TextView tvCategoryName = dialogView.findViewById(R.id.tvCategoryName);
        EditText etConfirmCategoryName = dialogView.findViewById(R.id.etConfirmCategoryName);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        // Set category details
        tvCategoryName.setText(category.getCategoryName());
        if (category.getImage() != null && !category.getImage().isEmpty()) {
            Uri imageUri = Uri.fromFile(new File(category.getImage()));
            ivCategoryImage.setImageURI(imageUri);
        } else {
            ivCategoryImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Create the dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Handle Cancel button
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Handle Delete button
        btnDelete.setOnClickListener(v -> {
            String inputCategoryName = etConfirmCategoryName.getText().toString().trim();
            if (inputCategoryName.equals(category.getCategoryName())) {
                // Delete the category from the database
                CategoryDAO categoryDAO = new CategoryDAO(this);
                categoryDAO.deleteCategory(category.getCategoryId());
                categoryDAO.close();

                // Remove from the list and update the adapter
                categoryList.remove(position);
                categoryAdapter.notifyItemRemoved(position);
                categoryAdapter.notifyItemRangeChanged(position, categoryList.size());

                Toast.makeText(this, "Category deleted successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Category name does not match. Deletion canceled.", Toast.LENGTH_SHORT).show();
            }
        });

        // Show the dialog
        dialog.show();
    }
}