package com.humaclab.selliscope.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humaclab.selliscope.BR;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.activity.SalesReturnDetailsActivity;
import com.humaclab.selliscope.model.sales_return.SalesReturnResponse;

import java.util.List;

/**
 * Created by tonmoy on 5/16/17.
 * Updated by anam on 09/10/2018.
 */

public class SellsReturnRecyclerAdapter extends RecyclerView.Adapter<SellsReturnRecyclerAdapter.DeliveryListViewHolder> {
    private Context context;
    private List<SalesReturnResponse.DeliveryList> deliveryLists;

    public SellsReturnRecyclerAdapter(Context context, List<SalesReturnResponse.DeliveryList> deliveryLists) {
        this.context = context;
        this.deliveryLists = deliveryLists;
    }

    @Override
    public DeliveryListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sells_return_item, parent, false);
        return new DeliveryListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeliveryListViewHolder holder, int position) {
        SalesReturnResponse.DeliveryList delivery = deliveryLists.get(position);
        holder.getBinding().setVariable(BR.sellsReturnOrder, delivery);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return deliveryLists.size();
    }

    public class DeliveryListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ViewDataBinding binding;

        public DeliveryListViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(this);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SalesReturnDetailsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("deliveryList", deliveryLists.get(getLayoutPosition()));
            context.startActivity(intent);
        }
    }
}
