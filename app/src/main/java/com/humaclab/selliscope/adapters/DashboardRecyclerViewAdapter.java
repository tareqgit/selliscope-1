package com.humaclab.selliscope.adapters;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.humaclab.selliscope.R;
import com.humaclab.selliscope.model.DashboardItem;

import java.util.List;

/**
 * Created by Nahid on 3/1/2017.
 */
public class DashboardRecyclerViewAdapter extends RecyclerView.Adapter<DashboardRecyclerViewAdapter.DashboardViewHolder> {

    private List<DashboardItem> dashboardItems;
    private Context context;

    public DashboardRecyclerViewAdapter(Context context, List<DashboardItem> dashboardItems) {
        this.dashboardItems = dashboardItems;
        this.context = context;
    }

    @Override
    public DashboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_item, null);
        DashboardViewHolder dashboardViewHolder = new DashboardViewHolder(layoutView);
        return dashboardViewHolder;
    }


    @Override
    public void onBindViewHolder(DashboardViewHolder holder, final int position) {
        holder.itemImage.setImageResource(dashboardItems.get(position).getItemImageId());
        holder.itemName.setText(dashboardItems.get(position).getItemName());
    }

    @Override
    public int getItemCount() {
        return 8;
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

    public class DashboardViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView itemName;
        ImageView itemImage;

        public DashboardViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cv_dashboard_item);
            itemImage = (ImageView) itemView.findViewById(R.id.iv_dashboard_item);
            itemName = (TextView) itemView.findViewById(R.id.tv_dashboard_item_name);
        }
    }
}
