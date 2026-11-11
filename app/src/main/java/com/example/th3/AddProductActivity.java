package com.example.th3;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class AddProductActivity extends AppCompatActivity {

    private ImageButton btnBackHeader;
    private EditText edtName;
    private EditText edtPrice;
    private EditText edtQuantity;
    private Spinner spCategory;
    private SwitchCompat swActive;
    private EditText edtDescription;
    private Button btnSave;

    private DatabaseHelper db;

    private static final String[] CATEGORIES = new String[]{
            "Điện tử",
            "Thực phẩm",
            "Văn phòng phẩm",
            "Gia dụng",
            "Khác"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = new DatabaseHelper(this);

        btnBackHeader = findViewById(R.id.btnBackHeader);
        edtName = findViewById(R.id.edtName);
        edtPrice = findViewById(R.id.edtPrice);
        edtQuantity = findViewById(R.id.edtQuantity);
        spCategory = findViewById(R.id.spCategory);
        swActive = findViewById(R.id.swActive);
        edtDescription = findViewById(R.id.edtDescription);
        btnSave = findViewById(R.id.btnSave);

        android.view.View headerBar = findViewById(R.id.headerBar);
        float density = getResources().getDisplayMetrics().density;
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(headerBar, (v, insets) -> {
            androidx.core.graphics.Insets statusBarInsets = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.statusBars());
            int topPadding = statusBarInsets.top + (int) (10 * density);
            int bottomPadding = (int) (10 * density);
            v.setPadding(v.getPaddingLeft(), topPadding, v.getPaddingRight(), bottomPadding);
            return insets;
        });

        // Populate Category Spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                CATEGORIES
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        btnBackHeader.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = edtName.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();
        String quantityStr = edtQuantity.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Vui lòng nhập tên sản phẩm");
            edtName.requestFocus();
            return;
        }

        if (priceStr.isEmpty()) {
            edtPrice.setError("Vui lòng nhập giá bán");
            edtPrice.requestFocus();
            return;
        }

        int price;
        try {
            price = Integer.parseInt(priceStr);
            if (price <= 0) {
                edtPrice.setError("Giá bán phải lớn hơn 0");
                edtPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtPrice.setError("Giá bán không hợp lệ");
            edtPrice.requestFocus();
            return;
        }

        if (quantityStr.isEmpty()) {
            edtQuantity.setError("Vui lòng nhập số lượng tồn");
            edtQuantity.requestFocus();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                edtQuantity.setError("Số lượng không được âm");
                edtQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtQuantity.setError("Số lượng không hợp lệ");
            edtQuantity.requestFocus();
            return;
        }

        String category = spCategory.getSelectedItem().toString();
        int active = swActive.isChecked() ? 1 : 0;

        Product newProduct = new Product(name, price, quantity, category, active, description);
        long result = db.insertProduct(newProduct);

        if (result > 0) {
            Toast.makeText(this, R.string.add_success_toast, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Thêm sản phẩm thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}