package com.humaclab.lalteer.adapters;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humaclab.lalteer.BR;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.databinding.ItemPurchaseHistoryProductListBinding;
import com.humaclab.lalteer.model.purchase_history.OrderDetailsItem;

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
        //return orderDetails.size();
        return (orderDetails == null) ? 0 : orderDetails.size();
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
