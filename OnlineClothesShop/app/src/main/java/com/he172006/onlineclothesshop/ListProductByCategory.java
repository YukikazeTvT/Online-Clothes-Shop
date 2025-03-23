

package com.he172006.onlineclothesshop;


import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.he172006.onlineclothesshop.DAO.ProductDAO;
import com.he172006.onlineclothesshop.adapter.ProductDisplayAdapter;
import com.he172006.onlineclothesshop.component.HeaderComponent;
import com.he172006.onlineclothesshop.entity.Product;


import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;




public class ListProductByCategory extends AppCompatActivity {
    private RecyclerView productRecyclerView;
    private ProductDisplayAdapter productAdapter;
    private ProductDAO productDAO;
    private Executor executor = Executors.newSingleThreadExecutor();




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product_by_category);


        HeaderComponent.setupHeader(this);
        productRecyclerView = findViewById(R.id.recyclerViewProducts);
        productRecyclerView.setHasFixedSize(true);
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));




        productDAO = new ProductDAO(this);




        int categoryId = getIntent().getIntExtra("categoryId", -1);
        Log.d("ListProductByCategory", "Received category ID: " + categoryId);




        TextView categoryTitle = findViewById(R.id.txtCategoryTitle);
        categoryTitle.setText(getIntent().getStringExtra("categoryName"));




        if (categoryId != -1) {
            loadProducts(categoryId);
        } else {
            Log.e("ListProductByCategory", "Invalid category ID received: " + categoryId);
        }
    }




    private void loadProducts(int categoryId) {
        Log.d("ListProductByCategory", "Loading products for category ID: " + categoryId);
        executor.execute(() -> {
            List<Product> products = productDAO.getProductsByCategoryId(categoryId);
            Log.d("ListProductByCategory", "Number of products: " + products.size());
            runOnUiThread(() -> {
                productAdapter = new ProductDisplayAdapter(this, products);
                productRecyclerView.setAdapter(productAdapter);
            });
        });
    }
}






