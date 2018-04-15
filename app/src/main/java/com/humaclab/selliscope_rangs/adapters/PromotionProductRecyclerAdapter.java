package com.humaclab.selliscope_rangs.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.humaclab.selliscope_rangs.R;

/**
 * Created by leon on 8/4/18.
 */

public class PromotionProductRecyclerAdapter extends RecyclerView.Adapter<PromotionProductRecyclerAdapter.PromotionProductViewHolder> {
    private Context context;

    @Override
    public PromotionProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_promotion_product, parent, false);
        return new PromotionProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PromotionProductViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class PromotionProductViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_product_name;

        public PromotionProductViewHolder(View itemView) {
            super(itemView);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
        }
    }
}
