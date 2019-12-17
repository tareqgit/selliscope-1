package com.easyopstech.easyops.adapters;


import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyopstech.easyops.R;
import com.easyopstech.easyops.databinding.SellsReturnHistoryItemBinding;
import com.easyopstech.easyops.model.sales_return.SalesReturnHistory;

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
        holder.getBinding().setSellsReturnHistory(result);
        holder.getBinding().executePendingBindings();

    }


    @Override
    public int getItemCount() {
        return results.size();
    }

    public class HistoryListViewHolder extends RecyclerView.ViewHolder{
        private SellsReturnHistoryItemBinding binding;
        public HistoryListViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
        public SellsReturnHistoryItemBinding getBinding(){
            return binding;
        }
    }
}
