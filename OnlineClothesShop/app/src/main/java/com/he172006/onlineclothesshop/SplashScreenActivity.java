package com.he172006.onlineclothesshop;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

        public class SplashScreenActivity extends AppCompatActivity {

            // Duration of splash screen in milliseconds (2 seconds)
            private static final int SPLASH_DURATION = 2000;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_splash_screen);

                // Use Handler to delay the transition to MainActivity
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Start MainActivity
                        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(intent);
                        // Close SplashActivity
                        finish();
                    }
                }, SPLASH_DURATION);
            }
        }