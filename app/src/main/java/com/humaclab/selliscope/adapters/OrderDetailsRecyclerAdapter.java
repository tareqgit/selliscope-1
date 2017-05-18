package com.humaclab.selliscope.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.humaclab.selliscope.BR;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.model.OrderResponse;

import java.util.List;

/**
 * Created by tonmoy on 5/18/17.
 */

public class OrderDetailsRecyclerAdapter extends RecyclerView.Adapter<OrderDetailsRecyclerAdapter.OrderDetailsViewHolder> {
    private Context context;
    private List<OrderResponse.Product> products;

    public OrderDetailsRecyclerAdapter(Context context, List<OrderResponse.Product> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public OrderDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_details_item, parent, false);
        return new OrderDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderDetailsViewHolder holder, int position) {
        OrderResponse.Product product = products.get(position);
        holder.getBinding().setVariable(BR.product, product);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class OrderDetailsViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;

        public OrderDetailsViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }
}
