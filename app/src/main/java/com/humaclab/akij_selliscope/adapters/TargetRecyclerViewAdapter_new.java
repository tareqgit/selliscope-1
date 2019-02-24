package com.humaclab.akij_selliscope.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.eralp.circleprogressview.CircleProgressView;
import com.humaclab.akij_selliscope.R;
import com.humaclab.akij_selliscope.model.Target.OutletTarget;
import com.humaclab.akij_selliscope.model.Target.Target;
import com.humaclab.akij_selliscope.model.Targets;

import java.util.List;

/**
 * Created by Miaki on 3/6/17.
 */

public class TargetRecyclerViewAdapter_new extends
        RecyclerView.Adapter<TargetRecyclerViewAdapter_new.TargetViewHolder> {

    private List<OutletTarget.Result> targetList;
    private Context context;

    public TargetRecyclerViewAdapter_new(Context context,
                                         List<OutletTarget.Result> targetList) {
        this.targetList = targetList;
        this.context = context;
    }

    @Override
    public TargetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.target_item_new, null);
        TargetViewHolder targetViewHolder = new TargetViewHolder(layoutView);
        return targetViewHolder;
    }

    @Override
    public void onBindViewHolder(TargetViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        holder.tv_slab_name.setText(targetList.get(position).getSlab());
        holder.tv_target.setText(targetList.get(position).getTarget());
        holder.tv_target_completed.setText(targetList.get(position).getAchieved());
        holder.tv_target_remaining.setText(String.valueOf(Integer.parseInt(targetList.get(position).getTarget())-Integer.parseInt(targetList.get(position).getAchieved())));
        holder.circleProgressView.setProgressWithAnimation(percentageOfCompletion(Integer.parseInt(targetList.get(position).getAchieved()),Integer.parseInt(targetList.get(position).getTarget())),2000);
        holder.circleProgressView.setInterpolator(new AccelerateDecelerateInterpolator());
        holder.circleProgressView.setStartAngle(10);
    }

    private int percentageOfCompletion(int completed, int total) {
        if (total == 0)
            return 0;
        return completed * 100 / total;
    }

    @Override
    public int getItemCount() {
        return targetList.size();
    }


    public class TargetViewHolder extends RecyclerView.ViewHolder {

        TextView tv_slab_name,tv_target,tv_target_completed,tv_target_remaining;
        CircleProgressView circleProgressView;

        public TargetViewHolder(View itemView) {
            super(itemView);

            tv_slab_name = itemView.findViewById(R.id.tv_slab_name);
            tv_target = itemView.findViewById(R.id.tv_target);
            tv_target_completed = itemView.findViewById(R.id.tv_target_completed);
            tv_target_remaining = itemView.findViewById(R.id.tv_target_remaining);
            circleProgressView = itemView.findViewById(R.id.circle_progress_view);

        }
    }
}

