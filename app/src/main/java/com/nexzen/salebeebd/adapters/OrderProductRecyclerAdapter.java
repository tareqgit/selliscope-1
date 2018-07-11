package com.nexzen.salebeebd.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nexzen.salebeebd.BR;
import com.nexzen.salebeebd.R;
import com.nexzen.salebeebd.activity.OrderActivity;
import com.nexzen.salebeebd.databinding.ItemOrderProductBinding;
import com.nexzen.salebeebd.helper.SelectedProductHelper;
import com.nexzen.salebeebd.helper.ShowProductSelectionDialog;
import com.nexzen.salebeebd.interfaces.OnSelectProduct;
import com.nexzen.salebeebd.model.VariantProduct.ProductsItem;

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

    public OrderProductRecyclerAdapter(Context context, OrderActivity orderActivity, List<ProductsItem> productsItemList, List<SelectedProductHelper> selectedProductList) {
        this.orderActivity = orderActivity;
        this.context = context;
        this.productsItemList = productsItemList;
        this.selectedProductList = selectedProductList;
    }

    @Override
    public OrderProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_product, parent, false);
        return new OrderProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderProductViewHolder holder, final int position) {
        this.holder = holder;
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

        public OrderProductViewHolder(View itemView) {
            super(itemView);
            this.setIsRecyclable(false);

            cv_product_background = itemView.findViewById(R.id.cv_product_background);

            binding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowProductSelectionDialog showProductSelectionDialog = new ShowProductSelectionDialog(
                            OrderProductRecyclerAdapter.OrderProductViewHolder.this,
                            context,
                            productsItemList.get(getAdapterPosition()));
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

        @Override
        public void onSetSelectedProduct(SelectedProductHelper selectedProduct) {
            orderActivity.onSetSelectedProduct(selectedProduct);
            cv_product_background.setCardBackgroundColor(Color.GREEN);
            getBinding().llQuantity.setVisibility(View.VISIBLE);
            getBinding().tvProductQuantity.setText(selectedProduct.getProductQuantity());
            getBinding().ivRemoveProduct.setVisibility(View.VISIBLE);
        }

        @Override
        public void onRemoveSelectedProduct(SelectedProductHelper selectedProduct) {

        }
    }
}
