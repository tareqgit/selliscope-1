package com.easyopstech.easyops.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyopstech.easyops.R;
import com.easyopstech.easyops.activity.PurchasedProductListActivity;
import com.easyopstech.easyops.databinding.ItemPurchaseHistoryBinding;
import com.easyopstech.easyops.model.Outlets;
import com.easyopstech.easyops.model.purchase_history.PurchaseHistoryItem;
import com.easyopstech.easyops.sales_return.model.get.DataItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by leon on 6/3/18.
 */

public class PurchaseHistoryRecyclerAdapter extends RecyclerView.Adapter<PurchaseHistoryRecyclerAdapter.PurchaseHistoryViewHolder> {
    private Context context;
    private List<PurchaseHistoryItem> purchaseHistoryItemList;
    private Outlets.Outlet outlet;
    private List<DataItem> mSalesReturnItems;

    public PurchaseHistoryRecyclerAdapter(Context context, List<PurchaseHistoryItem> purchaseHistoryItems, List<DataItem> salesReturnItems, Outlets.Outlet outlet) {
        this.context = context;
        this.purchaseHistoryItemList = purchaseHistoryItems;
        mSalesReturnItems = salesReturnItems;
        this.outlet = outlet;
    }

    public void updateData(List<PurchaseHistoryItem> purchaseHistoryItemList,List<DataItem> mSalesReturnItems){
        this.purchaseHistoryItemList.clear();
        this.purchaseHistoryItemList.addAll(purchaseHistoryItemList);
        this.mSalesReturnItems.clear();
        this.mSalesReturnItems.addAll(mSalesReturnItems);
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
        holder.getBinding().setPurchaseHistory( purchaseHistory);
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
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PurchasedProductListActivity.class);

                intent.putExtra("product_list", purchaseHistoryItemList.get(getAdapterPosition()));
                intent.putExtra("outletDetails", outlet);
                intent.putExtra("salesReturnItems",(Serializable) mSalesReturnItems);
                context.startActivity(intent);
            });
        }

        public ItemPurchaseHistoryBinding getBinding() {
            return binding;
        }
    }
}
