package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.he172006.onlineclothesshop.DAO.CartDAO;
import com.he172006.onlineclothesshop.DAO.ProductDAO;
import com.he172006.onlineclothesshop.DAO.ReviewDAO;
import com.he172006.onlineclothesshop.entity.Cart;
import com.he172006.onlineclothesshop.entity.Product;
import com.he172006.onlineclothesshop.entity.Review;
import com.he172006.onlineclothesshop.adapter.ReviewAdapter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductDetail extends AppCompatActivity {

    private ImageView imgProduct;
    private TextView txtProductName, txtProductPrice, txtProductDescription;
    private int productId;
    private ProductDAO productDAO;
    private CartDAO cartDAO;
    private Button btnAddToCart, btnSubmitReview;
    private RatingBar ratingBar;
    private EditText edtReview;
    private ReviewDAO reviewDAO;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Session sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        // Initialize SessionManager
        sessionManager = new Session(this);

        productDAO = new ProductDAO(this);
        cartDAO = new CartDAO(this);
        reviewDAO = new ReviewDAO(this);

        imgProduct = findViewById(R.id.imgProduct);
        txtProductName = findViewById(R.id.txtProductName);
        txtProductPrice = findViewById(R.id.txtProductPrice);
        txtProductDescription = findViewById(R.id.txtProductDescription);
        btnAddToCart = findViewById(R.id.btnAddToCart);
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

        // Xử lý nút "ADD TO CART"
        btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void addToCart() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please log in to add items to cart", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        executor.execute(() -> {
            // Lấy thông tin sản phẩm
            Product product = productDAO.getProductById(productId);
            if (product == null) {
                runOnUiThread(() -> Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show());
                return;
            }

            // Kiểm tra số lượng tồn kho
            if (product.getStock() <= 0) {
                runOnUiThread(() -> Toast.makeText(this, "Product is out of stock", Toast.LENGTH_SHORT).show());
                return;
            }

            // Lấy accountId từ session
            int accountId = sessionManager.getAccountId();

            // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
            List<Cart> cartItems = cartDAO.getCartItemsByAccountId(accountId);
            boolean productExistsInCart = false;
            for (Cart cartItem : cartItems) {
                if (cartItem.getProductId() == productId) {
                    productExistsInCart = true;
                    // Cập nhật số lượng
                    cartItem.setQuantity(cartItem.getQuantity() + 1);
                    cartDAO.updateCart(cartItem);
                    break;
                }
            }

            // Nếu sản phẩm chưa có trong giỏ hàng, thêm mới
            if (!productExistsInCart) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateAdded = sdf.format(new Date());
                Cart cart = new Cart();
                cart.setAccountId(accountId);
                cart.setProductId(productId);
                cart.setQuantity(1);
                cart.setDateAdded(dateAdded);
                cartDAO.insertCart(cart);
            }

            runOnUiThread(() -> {
                // Hiển thị dialog thông báo giống hình
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_to_cart, null);
                builder.setView(dialogView);

                ImageView dialogImage = dialogView.findViewById(R.id.dialogImage);
                TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
                TextView dialogPrice = dialogView.findViewById(R.id.dialogPrice);
                TextView dialogQuantity = dialogView.findViewById(R.id.dialogQuantity);
                Button btnCheckout = dialogView.findViewById(R.id.btnCheckout);
                Button btnViewCart = dialogView.findViewById(R.id.btnViewCart);

                Glide.with(this).load(product.getImage()).into(dialogImage);
                dialogTitle.setText(product.getProductName());
                dialogPrice.setText("USD$" + product.getPrice());
                dialogQuantity.setText("Quantity: 1");

                AlertDialog dialog = builder.create();
                dialog.show();

                btnCheckout.setOnClickListener(v -> {
                    dialog.dismiss();
                    startActivity(new Intent(this, CheckoutActivity.class));
                });

                btnViewCart.setOnClickListener(v -> {
                    dialog.dismiss();
                    startActivity(new Intent(this, ShoppingCartActivity.class));
                });
            });
        });
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
                loadReviews();
            });
        });
    }
}