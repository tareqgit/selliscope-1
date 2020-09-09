/*
 * Created by Tareq Islam on 3/28/19 10:19 AM
 *
 *  Last modified 3/28/19 10:19 AM
 */

package com.sokrio.sokrio_classic.performance.payments;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sokrio.sokrio_classic.R;
import com.sokrio.sokrio_classic.databinding.ActivityPerformancePaymentsModelBinding;
import com.sokrio.sokrio_classic.model.performance.payments_model.Datum;

import java.util.List;

/***
 * Created by mtita on 28,March,2019.
 */
public class PerformancePaymentsAdapter extends RecyclerView.Adapter<PerformancePaymentsAdapter.TViewHolder>{

    private Context mContext;
    private List<Datum> mDatumList;

    public PerformancePaymentsAdapter(Context context, List<Datum> datumList) {
        mContext = context;
        mDatumList = datumList;
    }

    @NonNull
    @Override
    public TViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(mContext).inflate(R.layout.activity_performance_payments_model,parent,false);


        return new TViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TViewHolder holder, int position) {
        Datum datum=mDatumList.get(position);
        holder.getBinding().setData(datum);
    }

    @Override
    public int getItemCount() {
        return mDatumList.size();
    }

    public class TViewHolder extends RecyclerView.ViewHolder{
        public ActivityPerformancePaymentsModelBinding getBinding() {
            return mBinding;
        }

        private ActivityPerformancePaymentsModelBinding mBinding;
        public TViewHolder(View itemView) {
            super(itemView);
            mBinding= DataBindingUtil.bind(itemView);

        }
    }
}
