package com.humaclab.selliscope.performance.daily_activities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.humaclab.selliscope.R;
import com.humaclab.selliscope.databinding.ActivityRegularPerformennceModelBinding;

import com.humaclab.selliscope.model.performance.orders_model.Order;
import com.humaclab.selliscope.model.performance.payments_model.Datum;
import com.humaclab.selliscope.utility_db.db.RegularPerformanceEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/***
 * Created by mtita on 22,August,2019.
 */
public class RegularPerformanceAdapter extends RecyclerView.Adapter<RegularPerformanceAdapter.TViewHolder> {


    private Context mContext;
    private List<RegularPerformanceEntity> mDatumList;
    private OnOutletItemClickListener mOnOutletItemClickListener;



    public RegularPerformanceAdapter(Context context, List<RegularPerformanceEntity> datumList, OnOutletItemClickListener onOutletItemClickListener) {
        mContext = context;
        mDatumList = datumList;
        mOnOutletItemClickListener = onOutletItemClickListener;
    }

    @NonNull
    @Override
    public TViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(mContext).inflate(R.layout.activity_regular_performennce_model, parent, false);


        return new TViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TViewHolder holder, int position) {

            RegularPerformanceEntity datum = mDatumList.get(position);
        List<String> outlets = new ArrayList<>();
          if(datum.outlets_checked_in!=null) {
               outlets = Arrays.asList(datum.outlets_checked_in.split("~;~"));
          }
          if(outlets.size()>0){
              if(!outlets.get(0).equals("")){
                  holder.getBinding().textViewTotalCheckin.setText(String.format(Locale.ENGLISH, "No of check-in: %d", outlets.size()));

              }
          }
         //     holder.getBinding().textViewTotalCheckin.setText(String.format(Locale.ENGLISH, "No of check-in: %d", outlets.size()));
            holder.getBinding().setActivities(datum);

    }

    @Override
    public int getItemCount() {
        return mDatumList == null ? 0 : mDatumList.size();

    }

    public class TViewHolder extends RecyclerView.ViewHolder {
        public ActivityRegularPerformennceModelBinding getBinding() {
            return mBinding;
        }

        private ActivityRegularPerformennceModelBinding mBinding;

        public TViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 if(mOnOutletItemClickListener!=null)   mOnOutletItemClickListener.onOutletClick(mDatumList.get(getAdapterPosition()));
                }
            });
        }
    }


    public interface OnOutletItemClickListener{
        void onOutletClick(RegularPerformanceEntity performanceEntity);
    }
}
