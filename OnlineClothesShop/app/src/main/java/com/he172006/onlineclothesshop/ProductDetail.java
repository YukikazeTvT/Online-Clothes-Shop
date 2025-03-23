package com.he172006.onlineclothesshop;




import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;




import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;




import com.bumptech.glide.Glide;
import com.he172006.onlineclothesshop.DAO.ProductDAO;
import com.he172006.onlineclothesshop.DAO.ReviewDAO;
import com.he172006.onlineclothesshop.adapter.ReviewAdapter;
import com.he172006.onlineclothesshop.component.HeaderComponent;
import com.he172006.onlineclothesshop.entity.Review;




public class ProductDetail extends AppCompatActivity {




    private ImageView imgProduct;
    private TextView txtProductName, txtProductPrice, txtProductDescription;
    private int productId;
    private ProductDAO productDAO;
    private Button btnSubmitReview;
    private RatingBar ratingBar;
    private EditText edtReview;
    private ReviewDAO reviewDAO;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private ExecutorService executor = Executors.newSingleThreadExecutor();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        HeaderComponent.setupHeader(this);
        productDAO = new ProductDAO(this);
        reviewDAO = new ReviewDAO(this);
        imgProduct = findViewById(R.id.imgProduct);
        txtProductName = findViewById(R.id.txtProductName);
        txtProductPrice = findViewById(R.id.txtProductPrice);
        txtProductDescription = findViewById(R.id.txtProductDescription);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        ratingBar = findViewById(R.id.ratingBar);
        edtReview = findViewById(R.id.edtReview);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        if (intent != null) {
            productId = intent.getIntExtra("product_id", -1);
            String name = intent.getStringExtra("product_name");
            double price = intent.getDoubleExtra("product_price", 0.0);
            String description = intent.getStringExtra("product_description");
            String imageUrl = intent.getStringExtra("product_image");
            DecimalFormat formatter = new DecimalFormat("#,##0");
            String formattedPrice = " $" + formatter.format(price);
            Log.d("ProductDetail", "Product ID: " + productId);
            Log.d("ProductDetail", "Product Name: " + name);
            Log.d("ProductDetail", "Product Price: " + price);
            Log.d("ProductDetail", "Product Description: " + description);
            Log.d("ProductDetail", "Product Image URL: " + imageUrl);
            txtProductName.setText(name);
            txtProductPrice.setText(formattedPrice);
            txtProductDescription.setText(description);




            Glide.with(this).load(imageUrl).into(imgProduct);
        }
        loadReviews();
        btnSubmitReview.setOnClickListener(v -> submitReview());
    }
    private void loadReviews() {
        executor.execute(() -> {
            if (productDAO == null) {
                runOnUiThread(() -> Toast.makeText(this, "Lỗi: Database chưa được khởi tạo!", Toast.LENGTH_SHORT).show());
                return;
            }




            List<Review> reviews = reviewDAO.getReviewsByProduct(productId);
            Log.d("ProductDetail", "Số lượng đánh giá: " + reviews.size());








            runOnUiThread(() -> {
                if (reviewAdapter == null) {
                    reviewAdapter = new ReviewAdapter(reviews);
                    recyclerViewReviews.setAdapter(reviewAdapter);
                } else {
                    reviewAdapter.updateReviews(reviews);
                }
                recyclerViewReviews.invalidate();
            });
        });
    }




    private void submitReview() {
        if (productId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }




        float rating = ratingBar.getRating();
        String reviewText = edtReview.getText().toString().trim();




        if (reviewText.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đánh giá!", Toast.LENGTH_SHORT).show();
            return;
        }




        executor.execute(() -> {
            Review review = new Review(productId, rating, reviewText);
            reviewDAO.insertReview(review);




            runOnUiThread(() -> {
                Toast.makeText(this, "Đã gửi đánh giá!", Toast.LENGTH_SHORT).show();
                edtReview.setText("");
                ratingBar.setRating(0);
                loadReviews();
            });
        });
    }




}






