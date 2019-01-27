package com.humaclab.akij_selliscope.adapters;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humaclab.akij_selliscope.BR;
import com.humaclab.akij_selliscope.R;
import com.humaclab.akij_selliscope.activity.PurchasedProductListActivity;
import com.humaclab.akij_selliscope.databinding.ItemPurchaseHistoryBinding;
import com.humaclab.akij_selliscope.model.Outlets;
import com.humaclab.akij_selliscope.model.PurchaseHistory.PurchaseHistoryItem;
import com.humaclab.akij_selliscope.utils.SessionManager;

import java.util.List;

/**
 * Created by leon on 6/3/18.
 */

public class PurchaseHistoryRecyclerAdapter extends RecyclerView.Adapter<PurchaseHistoryRecyclerAdapter.PurchaseHistoryViewHolder> {
    private Context context;
    private List<PurchaseHistoryItem> purchaseHistoryItemList;
    private Outlets.Outlet outlet;
    private SessionManager sessionManager;

    public PurchaseHistoryRecyclerAdapter(Context context, List<PurchaseHistoryItem> purchaseHistoryItems, Outlets.Outlet outlet) {
        this.context = context;
        this.purchaseHistoryItemList = purchaseHistoryItems;
        this.outlet = outlet;
        sessionManager = new SessionManager(context);
    }

    @Override
    public PurchaseHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_purchase_history, parent, false);
        return new PurchaseHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PurchaseHistoryViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        PurchaseHistoryItem purchaseHistory = purchaseHistoryItemList.get(position);
        holder.getBinding().setVariable(BR.purchaseHistory, purchaseHistory);

        //1 == Auditor
        //0 == user
        if(Integer.parseInt(sessionManager.getKeyAudit())==1){
            holder.binding.btnAudit.setVisibility(View.VISIBLE);
        }

        holder.binding.btnAudit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PurchasedProductListActivity.class);
                intent.putExtra("product_list", purchaseHistoryItemList.get(position));
                intent.putExtra("outletDetails", outlet);
                context.startActivity(intent);

            }
        });


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

                }
            });
        }

        public ItemPurchaseHistoryBinding getBinding() {
            return binding;
        }
    }
}
