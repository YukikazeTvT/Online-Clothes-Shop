package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.he172006.onlineclothesshop.DAO.ProductDAO;
import com.he172006.onlineclothesshop.adapter.ManageProductAdapter;
import com.he172006.onlineclothesshop.entity.Product;
import java.util.List;

public class ManageProductActivity extends AppCompatActivity {

    private RecyclerView rvProductList;
    private ManageProductAdapter productAdapter;
    private List<Product> productList;
    private FloatingActionButton btnAddProduct;
    private ActivityResultLauncher<Intent> addProductLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_product);

        // Initialize views
        rvProductList = findViewById(R.id.rvProductList);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        rvProductList.setLayoutManager(new LinearLayoutManager(this));

        // Load products from database
        refreshProductList();

        // Initialize launcher for adding product
        addProductLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        refreshProductList(); // Cập nhật danh sách sau khi thêm sản phẩm
                    }
                });

        // Handle Add Product button click
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageProductActivity.this, AddProductActivity.class);
                addProductLauncher.launch(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshProductList();
    }

    public void refreshProductList() {
        ProductDAO productDAO = new ProductDAO(this);
        productList = productDAO.getAllProducts();
        productAdapter = new ManageProductAdapter(this, productList);
        rvProductList.setAdapter(productAdapter);
        productDAO.close();
    }
}