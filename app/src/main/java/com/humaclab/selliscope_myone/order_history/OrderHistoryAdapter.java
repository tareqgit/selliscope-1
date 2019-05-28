/*
 * Created by Tareq Islam on 5/28/19 2:55 PM
 *
 *  Last modified 5/28/19 2:55 PM
 */

package com.humaclab.selliscope_myone.order_history;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;
import com.humaclab.selliscope_myone.R;
import com.humaclab.selliscope_myone.databinding.ActivityOrderHistoryOrderModelBinding;
import com.humaclab.selliscope_myone.order_history.api.response_model.OrdersItem;

import java.util.List;

/***
 * Created by mtita on 28,May,2019.
 */
public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.TViewHolder>{
    private Context mContext;
    private List<OrdersItem> mOrderList;
    private OnOrderItemClickListener mOnOrderItemClickListener;

    public OrderHistoryAdapter(Context context, List<OrdersItem> orderList, OnOrderItemClickListener onOrderItemClickListener) {
        mContext = context;
        mOrderList = orderList;
        mOnOrderItemClickListener = onOrderItemClickListener;
    }

    @NonNull
    @Override
    public TViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.activity_order_history_order_model,viewGroup,false);
        return new TViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TViewHolder tViewHolder, int i) {
        OrdersItem ordersItem = mOrderList.get(i);
        tViewHolder.getBinding().setVariable(BR.Data, ordersItem);
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public class TViewHolder extends RecyclerView.ViewHolder{
        public ActivityOrderHistoryOrderModelBinding getBinding()
        {
            return mBinding;
        }

        private ActivityOrderHistoryOrderModelBinding mBinding;
        public TViewHolder(View itemView) {
            super(itemView);
            mBinding= DataBindingUtil.bind(itemView);
            
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnOrderItemClickListener.onOrderClick(mOrderList.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnOrderItemClickListener{
        void onOrderClick(OrdersItem order);
    }

}
