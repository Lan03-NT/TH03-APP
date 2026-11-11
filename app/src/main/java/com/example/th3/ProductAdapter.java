package com.example.th3;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    private final Context context;
    private final ArrayList<Product> productList;
    private final OnItemClickListener listener;

    public ProductAdapter(Context context, ArrayList<Product> productList, OnItemClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText("Đơn giá: " + Product.formatCurrency(product.getPrice()));
        holder.tvCategory.setText(product.getCategory());
        holder.tvQuantity.setText("Tồn: " + product.getQuantity());

        boolean isLowStock = product.getQuantity() < 5;

        if (isLowStock) {
            // Low stock styling (< 5)
            holder.vIndicator.setBackgroundColor(Color.parseColor("#E11D48"));
            holder.tvQuantity.setTextColor(Color.parseColor("#E11D48"));
            holder.tvQuantity.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_badge_low_stock));
            holder.tvLowStockBadge.setVisibility(View.VISIBLE);
        } else {
            // Normal stock styling (>= 5)
            holder.vIndicator.setBackgroundColor(Color.parseColor("#4F46E5"));
            holder.tvQuantity.setTextColor(Color.parseColor("#0F172A"));
            holder.tvQuantity.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_input));
            holder.tvLowStockBadge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        View vIndicator;
        TextView tvName, tvPrice, tvCategory, tvQuantity, tvLowStockBadge;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            vIndicator = itemView.findViewById(R.id.vIndicator);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvLowStockBadge = itemView.findViewById(R.id.tvLowStockBadge);
        }
    }
}