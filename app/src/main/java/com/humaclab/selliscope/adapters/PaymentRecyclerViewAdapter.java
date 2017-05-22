package com.humaclab.selliscope.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humaclab.selliscope.BR;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.model.OrderResponse;

import java.util.List;

/**
 * Created by tonmoy on 5/22/17.
 */

public class PaymentRecyclerViewAdapter extends RecyclerView.Adapter<PaymentRecyclerViewAdapter.PaymentViewHolder> {
    private Context context;
    private List<OrderResponse.OrderList> orderLists;

    public PaymentRecyclerViewAdapter(Context context, List<OrderResponse.OrderList> orderLists) {
        this.context = context;
        this.orderLists = orderLists;
    }

    @Override
    public PaymentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_item, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PaymentViewHolder holder, int position) {
        OrderResponse.OrderList orderList = orderLists.get(position);
        holder.getBinding().setVariable(BR.orders, orderList);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return orderLists.size();
    }

    public class PaymentViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;

        public PaymentViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }
}
