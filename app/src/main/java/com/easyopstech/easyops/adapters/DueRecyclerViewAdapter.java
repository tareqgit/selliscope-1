package com.easyopstech.easyops.adapters;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyopstech.easyops.R;
import com.easyopstech.easyops.databinding.DueItemBinding;
import com.easyopstech.easyops.model.Payment;

import java.util.List;

/**
 * Created by tonmoy on 5/22/17.
 */

public class DueRecyclerViewAdapter extends RecyclerView.Adapter<DueRecyclerViewAdapter.DueViewHolder> {
    private Context context;
    private List<Payment.OrderList> orderLists;

    public DueRecyclerViewAdapter(Context context, List<Payment.OrderList> orderLists) {
        this.context = context;
        this.orderLists = orderLists;
    }

    @Override
    public DueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.due_item, parent, false);
        return new DueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DueViewHolder holder, int position) {
        Payment.OrderList orderList = orderLists.get(position);
        holder.getBinding().setPayments( orderList);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return orderLists.size();
    }

    public class DueViewHolder extends RecyclerView.ViewHolder {
        private DueItemBinding binding;

        public DueViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public DueItemBinding getBinding() {
            return binding;
        }
    }
}
