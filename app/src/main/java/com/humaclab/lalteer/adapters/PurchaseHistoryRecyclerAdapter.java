package com.humaclab.lalteer.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humaclab.lalteer.R;
import com.humaclab.lalteer.activity.PurchasedProductListActivity;
import com.humaclab.lalteer.databinding.ItemPurchaseHistoryBinding;
import com.humaclab.lalteer.model.Outlets;
import com.humaclab.lalteer.model.purchase_history.PurchaseHistoryItem;

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

    public void updateData(List<PurchaseHistoryItem> purchaseHistoryItemList){
        this.purchaseHistoryItemList.clear();
        this.purchaseHistoryItemList.addAll(purchaseHistoryItemList);
        this.notifyDataSetChanged();

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
        return (purchaseHistoryItemList == null) ? 0 : purchaseHistoryItemList.size();
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
