package com.easyopstech.easyops.adapters;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.eralp.circleprogressview.CircleProgressView;
import com.easyopstech.easyops.R;
import com.easyopstech.easyops.model.Targets;

import java.util.List;

/**
 * Created by Miaki on 3/6/17.
 */

public class TargetRecyclerViewAdapter extends
        RecyclerView.Adapter<TargetRecyclerViewAdapter.TargetViewHolder> {

    private List<Targets.Successful.Target> salesTargets;
    private Context context;

    public TargetRecyclerViewAdapter(Context context,
                                     List<Targets.Successful.Target> salesTargets) {
        this.salesTargets = salesTargets;
        this.context = context;
    }

    @Override
    public TargetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.target_item, null);
        TargetViewHolder targetViewHolder = new TargetViewHolder(layoutView);
        return targetViewHolder;
    }

    @Override
    public void onBindViewHolder(TargetViewHolder holder, int position) {
        holder.mCircleProgressView.setTextEnabled(true);
        holder.mCircleProgressView.setTextSuffix("%");
        holder.mCircleProgressView.setInterpolator(new AccelerateDecelerateInterpolator());
        holder.mCircleProgressView.setStartAngle(45);
        holder.mCircleProgressView.setCirclerWidth(25);
        Targets.Successful.Target salesTarget = salesTargets.get(position);
        int remainingTarget;
        switch (position) {
            case 0: {
                holder.targetLabel.setText("Must Sales Target");
                remainingTarget = salesTarget.mustSales.target - salesTarget.mustSales.achieved;
                holder.targetTotal.setText(salesTarget.mustSales.target + "");
                holder.targetAchieved.setText(salesTarget.mustSales.achieved + "");
                holder.targetRemaining.setText(remainingTarget + "");
                holder.mCircleProgressView.setProgressWithAnimation(percentageOfCompletion(
                        salesTarget.mustSales.achieved, salesTarget.mustSales.target
                ), 2000);

                break;
            }
            case 1: {
                holder.targetLabel.setText("Priority Sales Target");
                remainingTarget = salesTarget.prioritySales.target - salesTarget.prioritySales.achieved;
                holder.targetTotal.setText(salesTarget.prioritySales.target + "");
                holder.targetAchieved.setText(salesTarget.prioritySales.achieved + "");
                holder.targetRemaining.setText(remainingTarget + "");
                holder.mCircleProgressView.setProgressWithAnimation(percentageOfCompletion(
                        salesTarget.prioritySales.achieved, salesTarget.prioritySales.target
                ), 2000);
                break;
            }
            case 2: {
                holder.targetLabel.setText("Regular Sales Target");
                remainingTarget = salesTarget.regularSales.target - salesTarget.regularSales.achieved;
                holder.targetTotal.setText(salesTarget.regularSales.target + "");
                holder.targetAchieved.setText(salesTarget.regularSales.achieved + "");
                holder.targetRemaining.setText(remainingTarget + "");
                holder.mCircleProgressView.setProgressWithAnimation(percentageOfCompletion(
                        salesTarget.regularSales.achieved, salesTarget.regularSales.target
                ), 2000);
                break;
            }
        }


    }

    private int percentageOfCompletion(int completed, int total) {
        if (total == 0)
            return 0;
        return completed * 100 / total;
    }

    @Override
    public int getItemCount() {
        return 3;
    }


    public class TargetViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        CircleProgressView mCircleProgressView;
        TextView targetLabel, targetRemaining, targetTotal, targetAchieved;

        public TargetViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cv_target_item);
            mCircleProgressView = (CircleProgressView) itemView
                    .findViewById(R.id.circle_progress_view);
            targetLabel = (TextView) itemView.findViewById(R.id.tv_target_label);
            targetAchieved = (TextView) itemView.findViewById(R.id.tv_target_achieved);
            targetRemaining = (TextView) itemView.findViewById(R.id.tv_target_remaining);
            targetTotal = (TextView) itemView.findViewById(R.id.tv_target_total);

        }
    }
}

