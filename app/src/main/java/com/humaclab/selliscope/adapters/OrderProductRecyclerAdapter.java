package com.humaclab.selliscope.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humaclab.selliscope.BR;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.activity.OrderActivity1;
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
    private OrderActivity1 orderActivity1;
    private Context context;
    private List<ProductsItem> productsItemList;
    private OrderProductViewHolder holder;
    private List<SelectedProductHelper> selectedProductList;

    public OrderProductRecyclerAdapter(Context context, OrderActivity1 orderActivity1, List<ProductsItem> productsItemList, List<SelectedProductHelper> selectedProductList) {
        this.orderActivity1 = orderActivity1;
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
    public void onBindViewHolder(OrderProductViewHolder holder, int position) {
        this.holder = holder;
        ProductsItem products = productsItemList.get(position);
        if (!selectedProductList.isEmpty()) {
            for (SelectedProductHelper selectedProductHelper : selectedProductList) {
                if (selectedProductHelper.getProductID().equals(String.valueOf(products.getId())))
                    if (selectedProductHelper.getProductRow().equals(products.getVariantRow()))
                        holder.cv_product_background.setCardBackgroundColor(Color.GREEN);
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
            cv_product_background = itemView.findViewById(R.id.cv_product_background);

            binding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowProductSelectionDialog showProductSelectionDialog = new ShowProductSelectionDialog(OrderProductRecyclerAdapter.OrderProductViewHolder.this, context, productsItemList.get(getAdapterPosition()));
                    showProductSelectionDialog.showDialog();
                }
            });
        }

        public ItemOrderProductBinding getBinding() {
            return binding;
        }

        @Override
        public void setSelectedProduct(SelectedProductHelper selectedProduct) {
            orderActivity1.setSelectedProduct(selectedProduct);
            cv_product_background.setCardBackgroundColor(Color.GREEN);
            getBinding().llQuantity.setVisibility(View.VISIBLE);
            getBinding().tvProductQuantity.setText(selectedProduct.getProductQuantity());
        }
    }
}
