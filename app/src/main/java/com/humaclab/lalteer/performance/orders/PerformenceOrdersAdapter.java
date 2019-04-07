/*
 * Created by Tareq Islam on 4/3/19 2:35 PM
 *
 *  Last modified 3/28/19 10:52 AM
 */

/*
 * Created by Tareq Islam on 3/27/19 2:39 PM
 *
 *  Last modified 3/27/19 2:39 PM
 */

package com.humaclab.lalteer.performance.orders;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humaclab.lalteer.BR;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.databinding.ActivityPerformenceOrdersModelBinding;
import com.humaclab.lalteer.model.performance.orders_model.Order;

import java.util.List;

/***
 * Created by mtita on 27,March,2019.
 */
public class PerformenceOrdersAdapter extends RecyclerView.Adapter<PerformenceOrdersAdapter.PerformanceHistoryViewHolder> {
    private Context mContext;
    private List<Order> mOrderList;
    private OnOrderItemClickListener mOnOrderItemClickListener;

    public PerformenceOrdersAdapter(Context context, List<Order> orderList) {
        mContext = context;
        mOrderList = orderList;
    }

    public PerformenceOrdersAdapter(Context context, List<Order> orderList, OnOrderItemClickListener onOrderItemClickListener) {
        mContext = context;
        mOrderList = orderList;
        mOnOrderItemClickListener = onOrderItemClickListener;
    }

    @NonNull
    @Override
    public PerformanceHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.activity_performence_orders_model,parent,false);
            return new PerformanceHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PerformanceHistoryViewHolder holder, int position) {
            Order order=mOrderList.get(position);
        holder.getBinding().setVariable(BR.Order,order);
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public class PerformanceHistoryViewHolder extends RecyclerView.ViewHolder{
        private ActivityPerformenceOrdersModelBinding mBinding;

        public PerformanceHistoryViewHolder(View itemView) {
            super(itemView);
            mBinding= DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnOrderItemClickListener.onOrderClick(mOrderList.get(getAdapterPosition()));
                }
            });

        }

        public ActivityPerformenceOrdersModelBinding getBinding() {
            return mBinding;
        }
    }

    public interface OnOrderItemClickListener{
        void onOrderClick(Order order);
    }

}
