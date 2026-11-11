package com.example.th3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private TextView tvError;
    private Button btnLogin;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        tvError = findViewById(R.id.tvError);
        btnLogin = findViewById(R.id.btnLogin);

        View heroBanner = findViewById(R.id.heroBanner);
        float density = getResources().getDisplayMetrics().density;
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(heroBanner, (v, insets) -> {
            androidx.core.graphics.Insets statusBarInsets = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.statusBars());
            int topPadding = statusBarInsets.top + (int) (48 * density);
            int bottomPadding = (int) (48 * density);
            v.setPadding(v.getPaddingLeft(), topPadding, v.getPaddingRight(), bottomPadding);
            return insets;
        });

        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            tvError.setText(R.string.login_error_empty);
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        boolean isValid = db.checkLogin(username, password);
        if (isValid) {
            tvError.setVisibility(View.GONE);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            tvError.setText(R.string.login_error_invalid);
            tvError.setVisibility(View.VISIBLE);
        }
    }
}