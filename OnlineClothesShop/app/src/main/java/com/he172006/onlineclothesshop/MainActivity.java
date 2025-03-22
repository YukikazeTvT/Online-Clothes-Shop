package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.he172006.onlineclothesshop.adapter.OnboardingAdapter;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private DotsIndicator dotIndicator;
    private Button btnGetStarted;
    private TextView tvSkip;

    // Array of image resources for the onboarding screens
    private final int[] onboardingImages = {
            R.drawable.donche,
            R.drawable.img,
            R.drawable.img_1
            // Add more images as needed
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        viewPager2 = findViewById(R.id.viewPager2);
        dotIndicator = findViewById(R.id.dotIndicator);
        btnGetStarted = findViewById(R.id.btnGetStarted);
        tvSkip = findViewById(R.id.tvSkip);

        // Setup ViewPager2
        OnboardingAdapter adapter = new OnboardingAdapter(onboardingImages);
        viewPager2.setAdapter(adapter);

        // Connect DotsIndicator to ViewPager2
        dotIndicator.setViewPager2(viewPager2);

        // Get Started button click listener
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });

        // Skip link click listener
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}