package com.humaclab.lalteer.adapters;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import androidx.cardview.widget.CardView;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humaclab.lalteer.R;
import com.humaclab.lalteer.activity.ProductListActivity;
import com.humaclab.lalteer.databinding.ItemProductListBinding;
import com.humaclab.lalteer.helper.SelectedProductHelper;
import com.humaclab.lalteer.model.variant_product.ProductsItem;

import java.util.List;

/**
 * Created by leon on 14/3/18.
 */

public class ProductListRecyclerAdapter extends RecyclerView.Adapter<ProductListRecyclerAdapter.OrderProductViewHolder> {
    private ProductListActivity productListActivity;
    private Context context;
    private List<ProductsItem> productsItemList;
    private OrderProductViewHolder holder;
    private List<SelectedProductHelper> selectedProductList;

    public ProductListRecyclerAdapter(Context context, ProductListActivity productListActivity, List<ProductsItem> productsItemList, List<SelectedProductHelper> selectedProductList) {
        this.productListActivity = productListActivity;
        this.context = context;
        this.productsItemList = productsItemList;
        this.selectedProductList = selectedProductList;
    }

    @Override
    public OrderProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_list, parent, false);
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


    public class OrderProductViewHolder extends RecyclerView.ViewHolder  {
        private ItemProductListBinding binding;
        private CardView cv_product_background;

        public OrderProductViewHolder(View itemView) {
            super(itemView);
            this.setIsRecyclable(false);


            binding = DataBindingUtil.bind(itemView);
        }

        public ItemProductListBinding getBinding() {
            return binding;
        }


    }
}
