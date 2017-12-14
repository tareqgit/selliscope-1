package com.humaclab.selliscope_myone.adapters;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humaclab.selliscope_myone.BR;
import com.humaclab.selliscope_myone.DeliveryDetailsActivity;
import com.humaclab.selliscope_myone.R;
import com.humaclab.selliscope_myone.model.DeliveryResponse;

import java.util.List;

/**
 * Created by tonmoy on 5/16/17.
 */

public class DeliveryListRecyclerAdapter extends RecyclerView.Adapter<DeliveryListRecyclerAdapter.DeliveryListViewHolder> {
    private Context context;
    private List<DeliveryResponse.DeliveryList> deliveryLists;

    public DeliveryListRecyclerAdapter(Context context, List<DeliveryResponse.DeliveryList> deliveryLists) {
        this.context = context;
        this.deliveryLists = deliveryLists;
    }

    @Override
    public DeliveryListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_list_item, parent, false);
        return new DeliveryListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeliveryListViewHolder holder, int position) {
        DeliveryResponse.DeliveryList delivery = deliveryLists.get(position);
        holder.getBinding().setVariable(BR.deliveryDetails, delivery);
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
            Intent intent = new Intent(context, DeliveryDetailsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("deliveryList", deliveryLists.get(getLayoutPosition()));
            context.startActivity(intent);
        }
    }
}
