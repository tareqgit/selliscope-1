package com.humaclab.selliscope.adapters;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humaclab.selliscope.BR;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.activity.PurchasedProductListActivity;
import com.humaclab.selliscope.databinding.ItemPurchaseHistoryBinding;
import com.humaclab.selliscope.model.Outlets;
import com.humaclab.selliscope.model.purchase_history.PurchaseHistoryItem;

import java.util.List;

/**
 * Created by leon on 6/3/18.
 */

public class PurchaseHistoryRecyclerAdapter extends RecyclerView.Adapter<PurchaseHistoryRecyclerAdapter.PurchaseHistoryViewHolder> {
    private Context context;
    private List<PurchaseHistoryItem> purchaseHistoryItemList;
    private Outlets.Outlet outlet;

    public PurchaseHistoryRecyclerAdapter(Context context, List<PurchaseHistoryItem> purchaseHistoryItems, Outlets.Outlet outlet) {
        this.context = context;
        this.purchaseHistoryItemList = purchaseHistoryItems;
        this.outlet = outlet;
    }

    @Override
    public PurchaseHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_purchase_history, parent, false);
        return new PurchaseHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PurchaseHistoryViewHolder holder, int position) {
        PurchaseHistoryItem purchaseHistory = purchaseHistoryItemList.get(position);
        holder.getBinding().setVariable(BR.purchaseHistory, purchaseHistory);
    }

    @Override
    public int getItemCount() {
        return purchaseHistoryItemList.size();
    }

    public class PurchaseHistoryViewHolder extends RecyclerView.ViewHolder {
        private ItemPurchaseHistoryBinding binding;

        public PurchaseHistoryViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PurchasedProductListActivity.class);
                    intent.putExtra("product_list", purchaseHistoryItemList.get(getAdapterPosition()));
                    intent.putExtra("outletDetails", outlet);
                    context.startActivity(intent);
                }
            });
        }

        public ItemPurchaseHistoryBinding getBinding() {
            return binding;
        }
    }
}
