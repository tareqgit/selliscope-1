package com.humaclab.lalteer.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.model.PromotionalAds.Result;
import com.humaclab.lalteer.utils.Constants;


import java.util.List;

public class PromotionalAdsAdapter extends RecyclerView.Adapter<PromotionalAdsAdapter.PromotionViewHolder> {
    private Context context;
    private List<Result> resultList;
    public PromotionalAdsAdapter(Context context,List<Result> resultList) {
        this.context = context;
        this.resultList = resultList;
    }

    @Override
    public PromotionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.promotional_ads_item, parent, false);
        return new PromotionalAdsAdapter.PromotionViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(PromotionViewHolder holder, int position) {

        holder.txtName.setText(resultList.get(position).getTitle());
        Glide.with(context)
                .load(Constants.baseUrl+resultList.get(position).getImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView);
    }


    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class PromotionViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        ImageView imageView;
        public PromotionViewHolder(View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.tv_banner_name);
            imageView = itemView.findViewById(R.id.iv_banner);
        }
    }
}
