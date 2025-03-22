package com.he172006.onlineclothesshop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.he172006.onlineclothesshop.DAO.AccountDAO;
import com.he172006.onlineclothesshop.entity.Account;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvSignUp;
    private AccountDAO accountDAO;
    private Session sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize SessionManager
        sessionManager = new Session(this);

        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            navigateBasedOnRole(sessionManager.getLoggedInAccount());
            return;
        }

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignUp);

        // Initialize DAO
        accountDAO = new AccountDAO(this);

        // Handle Login button click
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Email is required");
                etEmail.requestFocus();
                return;
            }
            if (password.isEmpty()) {
                etPassword.setError("Password is required");
                etPassword.requestFocus();
                return;
            }

            try {
                Account account = accountDAO.login(email, password);
                if (account != null) {
                    // Lưu phiên đăng nhập
                    sessionManager.createLoginSession(account);

                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                    navigateBasedOnRole(account);
                } else {
                    Toast.makeText(this, "Email or password is incorrect", Toast.LENGTH_SHORT).show();
                    etEmail.setText("");
                    etPassword.setText("");
                }
            } catch (Exception e) {
                Log.e(TAG, "Login error: ", e);
                Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Handle Forgot Password click
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Forgot Password feature is not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Sign Up click
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        // Handle WindowInsets for EdgeToEdge
        View rootView = findViewById(R.id.main);
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        } else {
            Log.w(TAG, "Root view (R.id.main) not found, insets not applied");
        }
    }

    // Navigate based on role
    private void navigateBasedOnRole(Account account) {
        if (account == null) {
            Log.w(TAG, "Account is null, cannot navigate");
            return;
        }

        switch (account.getRole()) {
            case "admin":
                Intent intentAdmin = new Intent(LoginActivity.this, AdminDashBroadActivity.class);
                intentAdmin.putExtra("accountId", account.getAccountId());
                startActivity(intentAdmin);
                finish();
                break;
            case "user":
                Intent intentUser = new Intent(LoginActivity.this, HomeActivity.class);
                intentUser.putExtra("accountId", account.getAccountId());
                startActivity(intentUser);
                finish();
                break;
            default:
                Toast.makeText(this, "Unknown role: " + account.getRole(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accountDAO != null) {
            accountDAO.close();
        }
    }
}