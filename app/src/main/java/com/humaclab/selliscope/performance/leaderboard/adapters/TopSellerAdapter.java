package com.humaclab.selliscope.performance.leaderboard.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.humaclab.selliscope.BR;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.databinding.PerformenceLeaderboardTopSellerFragmentModelBinding;
import com.humaclab.selliscope.model.performance.leaderboard_model.TopSellerModel;

import java.text.MessageFormat;
import java.util.List;

/***
 * Created by mtita on 30,April,2019.
 */
public class TopSellerAdapter  extends  RecyclerView.Adapter<TopSellerAdapter.TViewHolder>{
private Context mContext;
private List<TopSellerModel> mDummies;
private   boolean sorted=false;

    public TopSellerAdapter(Context context, List<TopSellerModel> dummies) {
        mContext = context;
        mDummies = dummies;
    }

    public TopSellerAdapter(Context context, List<TopSellerModel> dummies, boolean sorted) {
        mContext = context;
        mDummies = dummies;
        this.sorted = sorted;
    }

    @NonNull
    @Override
    public TViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View  view = LayoutInflater.from(mContext).inflate(R.layout.performence_leaderboard_top_seller_fragment_model,viewGroup,false);
        return new TViewHolder(view);
    }
int pos;
    @Override
    public void onBindViewHolder(@NonNull TViewHolder tViewHolder, int i) {
        TopSellerModel topSellerModel = mDummies.get(i);
        tViewHolder.getBinding().setVariable(BR.Data, topSellerModel);

        if(!sorted)
            pos=mDummies.size()-i ;
        else
            pos=i+1;
        tViewHolder.mBinding.textViewPos.setText(MessageFormat.format("{0}.",  pos));

        //color every even card background and user background
        if(i==2){
            tViewHolder.mBinding.body.setBackgroundColor(Color.parseColor("#E1F5FE"));
        }else if(i%2==0){
            tViewHolder.mBinding.body.setBackgroundColor(Color.parseColor("#F5F5F5"));
        }
        else{
            tViewHolder.mBinding.body.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

      /*  switch (pos){
            case 1:
                tViewHolder.mBinding.imageView7.setImageResource(R.drawable.ic_gold_medal_1);
                tViewHolder.mBinding.imageView7.setVisibility(View.VISIBLE);
                break;
            case 2:

                tViewHolder.mBinding.imageView7.setImageResource(R.drawable.ic_gold_medal_2);
                tViewHolder.mBinding.imageView7.setVisibility(View.VISIBLE);
                break;
            case 3:

                tViewHolder.mBinding.imageView7.setImageResource(R.drawable.ic_gold_medal_3);
                tViewHolder.mBinding.imageView7.setVisibility(View.VISIBLE);
                break;

            default:
                tViewHolder.mBinding.imageView7.setVisibility(View.GONE);
                break;

        }*/
        Glide.with(mContext)
                .load(R.drawable.default_profile_pic) //topSellerModel.getImage_url()
                .placeholder(R.drawable.default_profile_pic)
                .centerCrop()
                .transform(new CircleCrop())
                .into(tViewHolder.mBinding.imageView6);

    }

    @Override
    public int getItemCount() {
        return mDummies.size();
    }

    public class TViewHolder extends RecyclerView.ViewHolder{


        public PerformenceLeaderboardTopSellerFragmentModelBinding getBinding() {
            return mBinding;
        }

        private PerformenceLeaderboardTopSellerFragmentModelBinding mBinding;

        public TViewHolder(View itemView) {
            super(itemView);

            mBinding= DataBindingUtil.bind(itemView);

        }

    }
}
