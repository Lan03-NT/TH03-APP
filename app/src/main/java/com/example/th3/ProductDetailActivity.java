package com.example.th3;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageButton btnBackHeader;
    private TextView tvDetailCategory;
    private TextView tvDetailStatus;
    private TextView tvDetailName;
    private TextView tvDetailPrice;
    private TextView tvDetailQuantity;
    private TextView tvDetailLowStockWarning;
    private TextView tvDetailInventoryValue;
    private TextView tvDetailDescription;
    private Button btnImportOne;
    private Button btnSellOne;
    private Button btnBack;
    private Button btnDelete;

    private DatabaseHelper db;
    private int productId = -1;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        db = new DatabaseHelper(this);

        productId = getIntent().getIntExtra("product_id", -1);
        if (productId == -1) {
            Toast.makeText(this, "Sản phẩm không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnBackHeader = findViewById(R.id.btnBackHeader);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailQuantity = findViewById(R.id.tvDetailQuantity);
        tvDetailLowStockWarning = findViewById(R.id.tvDetailLowStockWarning);
        tvDetailInventoryValue = findViewById(R.id.tvDetailInventoryValue);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        btnImportOne = findViewById(R.id.btnImportOne);
        btnSellOne = findViewById(R.id.btnSellOne);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);

        View headerBar = findViewById(R.id.headerBar);
        float density = getResources().getDisplayMetrics().density;
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(headerBar, (v, insets) -> {
            androidx.core.graphics.Insets statusBarInsets = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.statusBars());
            int topPadding = statusBarInsets.top + (int) (10 * density);
            int bottomPadding = (int) (10 * density);
            v.setPadding(v.getPaddingLeft(), topPadding, v.getPaddingRight(), bottomPadding);
            return insets;
        });

        btnBackHeader.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());
        btnImportOne.setOnClickListener(v -> handleImportOne());
        btnSellOne.setOnClickListener(v -> handleSellOne());
        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());

        displayProductDetails();
    }

    private void displayProductDetails() {
        currentProduct = db.getProductById(productId);
        if (currentProduct == null) {
            Toast.makeText(this, "Sản phẩm không tồn tại trong CSDL", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailName.setText(currentProduct.getName());
        tvDetailCategory.setText(currentProduct.getCategory());

        if (currentProduct.getActive() == 1) {
            tvDetailStatus.setText(R.string.status_active);
            tvDetailStatus.setTextColor(ContextCompat.getColor(this, R.color.status_active_text));
            tvDetailStatus.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_badge_active));
        } else {
            tvDetailStatus.setText(R.string.status_inactive);
            tvDetailStatus.setTextColor(ContextCompat.getColor(this, R.color.status_inactive_text));
            tvDetailStatus.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_badge_inactive));
        }

        tvDetailPrice.setText(Product.formatCurrency(currentProduct.getPrice()));

        String formattedQty = String.format(Locale.getDefault(), "%02d", currentProduct.getQuantity());
        tvDetailQuantity.setText(formattedQty);

        if (currentProduct.getQuantity() < 5) {
            tvDetailLowStockWarning.setVisibility(View.VISIBLE);
        } else {
            tvDetailLowStockWarning.setVisibility(View.GONE);
        }

        long inventoryValue = (long) currentProduct.getPrice() * currentProduct.getQuantity();
        tvDetailInventoryValue.setText(Product.formatCurrency(inventoryValue));

        if (currentProduct.getDescription() != null && !currentProduct.getDescription().trim().isEmpty()) {
            tvDetailDescription.setText(currentProduct.getDescription());
        } else {
            tvDetailDescription.setText(R.string.no_description);
        }
    }

    private void handleImportOne() {
        if (currentProduct == null) return;
        int newQuantity = currentProduct.getQuantity() + 1;
        db.updateQuantity(productId, newQuantity);
        displayProductDetails();
    }

    private void handleSellOne() {
        if (currentProduct == null) return;
        if (currentProduct.getQuantity() == 0) {
            Toast.makeText(this, R.string.out_of_stock_toast, Toast.LENGTH_SHORT).show();
            return;
        }
        int newQuantity = currentProduct.getQuantity() - 1;
        db.updateQuantity(productId, newQuantity);
        displayProductDetails();
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(R.string.delete_confirm_msg)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int rows = db.deleteProduct(productId);
                    if (rows > 0) {
                        Toast.makeText(ProductDetailActivity.this, R.string.delete_success_toast, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}