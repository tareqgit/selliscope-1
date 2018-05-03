package com.humaclab.selliscope.helper;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.humaclab.selliscope.R;
import com.humaclab.selliscope.adapters.OrderProductRecyclerAdapter;
import com.humaclab.selliscope.databinding.ItemOrderProductSelectionBinding;
import com.humaclab.selliscope.model.VariantProduct.ProductsItem;

/**
 * Created by leon on 14/3/18.
 */

public class ShowProductSelectionDialog {
    private Context context;
    private ProductsItem productsItem;
    private SelectedProductHelper selectedProductHelper;
    private ItemOrderProductSelectionBinding binding;
    private AlertDialog alertDialog;
    private OrderProductRecyclerAdapter.OrderProductViewHolder orderProductRecyclerAdapter;

    public ShowProductSelectionDialog(OrderProductRecyclerAdapter.OrderProductViewHolder orderProductRecyclerAdapter, Context context, ProductsItem productsItem) {
        this.orderProductRecyclerAdapter = orderProductRecyclerAdapter;
        this.context = context;
        this.productsItem = productsItem;
        this.alertDialog = new AlertDialog.Builder(context).create();
    }

    public void setSelectedProduct(SelectedProductHelper selectedProductHelper) {
        this.selectedProductHelper = selectedProductHelper;
    }

    public void showDialog() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_order_product_selection, null, false);
        binding.tvProductName.setText(productsItem.getName());
        binding.tvProductPrice.setText(productsItem.getPrice());
        binding.tvProductStock.setText(productsItem.getStock());

        if (selectedProductHelper != null) {
            binding.etProductQty.setText(selectedProductHelper.getProductQuantity());
            binding.tvTotalPrice.setText(String.format("%.2f", Double.valueOf(binding.tvProductPrice.getText().toString().replace(",", "")) * Double.valueOf(binding.etProductQty.getText().toString())));
        }

        binding.etProductQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!s.toString().equals(""))
                        binding.tvTotalPrice.setText(String.format("%.2f", Double.valueOf(binding.tvProductPrice.getText().toString().replace(",", "")) * Double.valueOf(binding.etProductQty.getText().toString())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.btnSelectProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.etProductQty.getText().toString().equals("")) {
                    SelectedProductHelper selectedProduct = new SelectedProductHelper(
                            String.valueOf(productsItem.getId()),
                            productsItem.getName(),
                            binding.etProductQty.getText().toString(),
                            productsItem.getPrice(),
                            binding.tvTotalPrice.getText().toString(),
                            productsItem.getVariantRow() == null ? "0" : productsItem.getVariantRow()
                    );

                    orderProductRecyclerAdapter.onSetSelectedProduct(selectedProduct);
                    alertDialog.dismiss();
                    Toast.makeText(context, "Product added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    View view = binding.etProductQty;
                    binding.etProductQty.setError(context.getString(R.string.error_field_required));
                    view.requestFocus();
                }
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(binding.getRoot());
        alertDialog.show();
    }
}
