package com.humaclab.selliscope.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humaclab.selliscope.BR;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.databinding.ItemPurchaseHistoryProductListBinding;
import com.humaclab.selliscope.model.PurchaseHistory.OrderDetailsItem;

import java.util.List;

/**
 * Created by leon on 11/3/18.
 */

public class PurchasedProductRecyclerAdapter extends RecyclerView.Adapter<PurchasedProductRecyclerAdapter.PurchasedProductViewHolder> {
    private Context context;
    private List<OrderDetailsItem> orderDetails;

    public PurchasedProductRecyclerAdapter(Context context, List<OrderDetailsItem> orderDetails) {
        this.context = context;
        this.orderDetails = orderDetails;
    }

    @Override
    public PurchasedProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_purchase_history_product_list, parent, false);
        return new PurchasedProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PurchasedProductViewHolder holder, int position) {
        OrderDetailsItem orderDetailsItem = orderDetails.get(position);
        holder.getBinding().setVariable(BR.purchasedProduct, orderDetailsItem);
    }

    @Override
    public int getItemCount() {
        return orderDetails.size();
    }

    public class PurchasedProductViewHolder extends RecyclerView.ViewHolder {
        private ItemPurchaseHistoryProductListBinding binding;

        public PurchasedProductViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public ItemPurchaseHistoryProductListBinding getBinding() {
            return binding;
        }
    }
}