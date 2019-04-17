package com.humaclab.selliscope.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.humaclab.selliscope.BR;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.activity.OrderActivity;
import com.humaclab.selliscope.databinding.ItemOrderProductBinding;
import com.humaclab.selliscope.helper.SelectedProductHelper;
import com.humaclab.selliscope.helper.ShowProductSelectionDialog;
import com.humaclab.selliscope.interfaces.OnSelectProduct;
import com.humaclab.selliscope.model.VariantProduct.ProductsItem;


import java.util.List;

/**
 * Created by leon on 14/3/18.
 */

public class OrderProductRecyclerAdapter extends RecyclerView.Adapter<OrderProductRecyclerAdapter.OrderProductViewHolder> {
    private OrderActivity orderActivity;
    private Context context;
    private List<ProductsItem> productsItemList;
    private OrderProductViewHolder holder;
    private List<SelectedProductHelper> selectedProductList;
    private String outletType;

    public OrderProductRecyclerAdapter(Context context, OrderActivity orderActivity, List<ProductsItem> productsItemList, List<SelectedProductHelper> selectedProductList, String outletType) {
        this.orderActivity = orderActivity;
        this.context = context;
        this.productsItemList = productsItemList;
        this.selectedProductList = selectedProductList;
        this.outletType = outletType;
    }

    @Override
    public OrderProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_product, parent, false);
        return new OrderProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderProductViewHolder holder, final int position) {
        this.holder = holder;
        holder.setIsRecyclable(false);
        ProductsItem products = productsItemList.get(position);
        if (!selectedProductList.isEmpty()) {
            for (final SelectedProductHelper selectedProductHelper : selectedProductList) {
                if (selectedProductHelper.getProductID().equals(String.valueOf(products.getId())))
                    if (selectedProductHelper.getProductRow().equals(products.getVariantRow())) {
                        holder.cv_product_background.setCardBackgroundColor(Color.GREEN);
                        holder.getBinding().llQuantity.setVisibility(View.VISIBLE);
                        holder.getBinding().tvProductQuantity.setText(selectedProductHelper.getProductQuantity());
                        holder.getBinding().ivRemoveProduct.setVisibility(View.VISIBLE);

                        holder.getBinding().ivRemoveProduct.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                orderActivity.onRemoveSelectedProduct(selectedProductHelper);
                                holder.getBinding().ivRemoveProduct.setVisibility(View.GONE);
                                holder.getBinding().llQuantity.setVisibility(View.GONE);
                                holder.cv_product_background.setCardBackgroundColor(Color.WHITE);
                            }
                        });
                    }
            }
        }

        Glide.with(holder.imageView)
                .load(products.getImg())
                .thumbnail(0.1f)
                .into(holder.imageView);
        holder.getBinding().setVariable(BR.product, products);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return productsItemList.size();
    }


    public class OrderProductViewHolder extends RecyclerView.ViewHolder implements OnSelectProduct {
        private ItemOrderProductBinding binding;
        private CardView cv_product_background;
        ImageView imageView;

        public OrderProductViewHolder(View itemView) {
            super(itemView);
            this.setIsRecyclable(false);
            imageView = itemView.findViewById(R.id.iv_product_image);
            cv_product_background = itemView.findViewById(R.id.cv_product_background);

            binding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowProductSelectionDialog showProductSelectionDialog = new ShowProductSelectionDialog(
                            OrderProductRecyclerAdapter.OrderProductViewHolder.this,
                            context,
                            productsItemList.get(getAdapterPosition()),outletType);
                    if (!selectedProductList.isEmpty()) {
                        for (SelectedProductHelper selectedProductHelper : selectedProductList) {
                            if (selectedProductHelper.getProductID().equals(String.valueOf(productsItemList.get(getAdapterPosition()).getId())))
                                if (selectedProductHelper.getProductRow().equals(productsItemList.get(getAdapterPosition()).getVariantRow())) {
                                    showProductSelectionDialog.setSelectedProduct(selectedProductHelper);
                                    break;
                                }
                        }
                    }
                    showProductSelectionDialog.showDialog();
                }
            });
        }

        public ItemOrderProductBinding getBinding() {
            return binding;
        }

        //on selected product show the price and quenty

        @Override
        public void onSetSelectedProduct(SelectedProductHelper selectedProduct) {
            orderActivity.onSetSelectedProduct(selectedProduct);
            cv_product_background.setCardBackgroundColor(Color.GREEN);
            getBinding().llQuantity.setVisibility(View.VISIBLE);
            getBinding().tvProductQuantity.setText(selectedProduct.getProductQuantity());
            getBinding().tvProductPrice.setText(selectedProduct.getProductPrice());
            getBinding().ivRemoveProduct.setVisibility(View.VISIBLE);
        }

        @Override
        public void onRemoveSelectedProduct(SelectedProductHelper selectedProduct) {

        }
    }
}
