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
import com.humaclab.selliscope.model.SalesReturn.SalesReturnHistory;

import java.util.List;

/**
 * Created by anam on 10/10/2018.
 */


public class SalesReturnHistoryRecyclerAdapter  extends RecyclerView.Adapter<SalesReturnHistoryRecyclerAdapter.HistoryListViewHolder> {
    private Context context;
    private List<SalesReturnHistory.Result> results;
    public SalesReturnHistoryRecyclerAdapter(Context context,List<SalesReturnHistory.Result> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public HistoryListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sells_return_history_item,parent,false);
        return new HistoryListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryListViewHolder holder, int position) {
        SalesReturnHistory.Result result = results.get(position);
        holder.getBinding().setVariable(BR.sellsReturnHistory,result);
        holder.getBinding().executePendingBindings();

    }


    @Override
    public int getItemCount() {
        return results.size();
    }

    public class HistoryListViewHolder extends RecyclerView.ViewHolder{
        private ViewDataBinding binding;
        public HistoryListViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
        public ViewDataBinding getBinding(){
            return binding;
        }
    }
}
