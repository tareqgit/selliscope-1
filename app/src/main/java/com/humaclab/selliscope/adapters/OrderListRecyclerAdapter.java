package com.humaclab.selliscope.adapters;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humaclab.selliscope.BR;
import com.humaclab.selliscope.OrderDetailsActivity;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.model.OrderResponse;

import java.util.List;

/**
 * Created by tonmoy on 5/16/17.
 */

public class OrderListRecyclerAdapter extends RecyclerView.Adapter<OrderListRecyclerAdapter.OrderListViewHolder> {
    private Context context;
    private List<OrderResponse.OrderList> orderLists;

    public OrderListRecyclerAdapter(Context context, List<OrderResponse.OrderList> orderLists) {
        this.context = context;
        this.orderLists = orderLists;
    }

    @Override
    public OrderListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item, parent, false);
        return new OrderListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderListViewHolder holder, int position) {
        OrderResponse.OrderList order = orderLists.get(position);
        holder.getBinding().setVariable(BR.orderDetails, order);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return orderLists.size();
    }

    public class OrderListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ViewDataBinding binding;

        public OrderListViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(this);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("orderList", orderLists.get(getLayoutPosition()));
            context.startActivity(intent);
        }
    }
}
