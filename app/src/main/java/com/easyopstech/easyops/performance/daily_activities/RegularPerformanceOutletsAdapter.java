package com.easyopstech.easyops.performance.daily_activities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.easyopstech.easyops.R;
import com.easyopstech.easyops.databinding.ActivityRegularPerformanceOutletModelBinding;
import com.easyopstech.easyops.performance.daily_activities.model.OutletWithCheckInTime;

import java.util.List;

/***
 * Created by mtita on 24,August,2019.
 */
public class RegularPerformanceOutletsAdapter  extends RecyclerView.Adapter<RegularPerformanceOutletsAdapter.TViewHolder> {

    private Context mContext;
    private List<OutletWithCheckInTime> mOutletWithCheckInTimeList;

    public RegularPerformanceOutletsAdapter(Context context, List<OutletWithCheckInTime> outletWithCheckInTimeList) {
        mContext = context;
        mOutletWithCheckInTimeList = outletWithCheckInTimeList;
    }

    @NonNull
    @Override
    public TViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.activity_regular_performance_outlet_model,parent,false);
        return new TViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TViewHolder holder, int position) {
        OutletWithCheckInTime outlet=mOutletWithCheckInTimeList.get(position);

        holder.getBinding().setOutlet(outlet);

        Log.d("tareq_test", "RegularPerformanceOutletsAdapter #44: onBindViewHolder:  "+ new Gson().toJson(outlet));
       if( outlet.getOutlet()!=null) {
            if (outlet.getOutlet().isSelfie()) {
                holder.getBinding().textViewSelfie.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mOutletWithCheckInTimeList==null ?0: mOutletWithCheckInTimeList.size();
    }

    public class TViewHolder extends RecyclerView.ViewHolder{
        private ActivityRegularPerformanceOutletModelBinding mBinding;

        public TViewHolder(View itemView) {
            super(itemView);
            mBinding= DataBindingUtil.bind(itemView);
        }

        public ActivityRegularPerformanceOutletModelBinding getBinding() {
            return mBinding;
        }
    }
}
