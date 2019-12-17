package com.easyopstech.easyops.adapters;

import android.content.Context;
import android.graphics.Rect;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyopstech.easyops.R;
import com.easyopstech.easyops.model.DashboardItem;

import java.util.List;

/**
 * Created by Nahid on 3/1/2017.
 */
public class PerformanceRecyclerViewAdapter extends RecyclerView.Adapter<PerformanceRecyclerViewAdapter.PerformanceViewHolder> {

    private List<DashboardItem> performanceItems;
    private Context context;

    public PerformanceRecyclerViewAdapter(Context context, List<DashboardItem> performanceItems) {
        this.performanceItems = performanceItems;
        this.context = context;
    }

    @Override
    public PerformanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.performance_item, null);
        PerformanceViewHolder performanceViewHolder = new PerformanceViewHolder(layoutView);
        return performanceViewHolder;
    }


    @Override
    public void onBindViewHolder(PerformanceViewHolder holder, final int position) {
    /*   Glide.with(holder.itemImage)
                .load(performanceItems.get(position).getItemImageId())
                .centerCrop()
                .into(holder.itemImage);*/
        holder.itemImage.setImageResource(performanceItems.get(position).getItemImageId());
        holder.itemName.setText(performanceItems.get(position).getItemName());
    }

    @Override
    public int getItemCount() {
        return performanceItems.size();
    }

    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;
        private int headerNum;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge, int headerNum) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
            this.headerNum = headerNum;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view) - headerNum; // item position

            if (position >= 0) {
                int column = position % spanCount; // item column

                if (includeEdge) {
                    outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                    outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                    if (position < spanCount) { // top edge
                        outRect.top = spacing;
                    }
                    outRect.bottom = spacing; // item bottom
                } else {
                    outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                    outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                    if (position >= spanCount) {
                        outRect.top = spacing; // item top
                    }
                }
            } else {
                outRect.left = 0;
                outRect.right = 0;
                outRect.top = 0;
                outRect.bottom = 0;
            }
        }
    }

    public class PerformanceViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout cardView;
        TextView itemName;
        ImageView itemImage;

        public PerformanceViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cv_dashboard_item);
            itemImage = itemView.findViewById(R.id.iv_dashboard_item);
            itemName = itemView.findViewById(R.id.tv_dashboard_item_name);
        }
    }
}
