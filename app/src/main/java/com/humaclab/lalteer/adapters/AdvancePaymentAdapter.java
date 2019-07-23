/*
 * Created by Tareq Islam on 6/23/19 2:02 PM
 *
 *  Last modified 6/23/19 2:02 PM
 */

package com.humaclab.lalteer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.humaclab.lalteer.R;
import com.humaclab.lalteer.databinding.ActivityOutletDetailsAdvanceCardBinding;
import com.humaclab.lalteer.model.advance_payment.AdvancePaymentsItem;

import java.util.ArrayList;
import java.util.List;

/***
 * Created by mtita on 23,June,2019.
 */
public class AdvancePaymentAdapter  extends RecyclerView.Adapter<AdvancePaymentAdapter.RecyclerHolder> {

    Context mContext;
    List<AdvancePaymentsItem> mAdvancePaymentResponseList = new ArrayList<>();

    public AdvancePaymentAdapter(Context context, List<AdvancePaymentsItem> advancePaymentResponseList) {
        mContext = context;
        mAdvancePaymentResponseList = advancePaymentResponseList;
    }


    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_outlet_details_advance_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        AdvancePaymentsItem advancePaymentResponse = mAdvancePaymentResponseList.get(position);

        holder.getCardBinding().setAdvancePaymentVar(advancePaymentResponse);
        holder.getCardBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mAdvancePaymentResponseList.size();
    }

    public class RecyclerHolder extends RecyclerView.ViewHolder {
        ActivityOutletDetailsAdvanceCardBinding mCardBinding;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);
            mCardBinding = DataBindingUtil.bind(itemView);
        }

        public ActivityOutletDetailsAdvanceCardBinding getCardBinding() {
            return mCardBinding;
        }
    }
}
