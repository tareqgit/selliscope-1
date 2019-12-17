package com.easyopstech.easyops.performance.leaderboard.adapters;

import android.content.Context;

import androidx.databinding.DataBindingUtil;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import com.easyopstech.easyops.R;
import com.easyopstech.easyops.databinding.PerformenceLeaderboardTopSellerFragmentModelBinding;
import com.easyopstech.easyops.model.performance.leaderboard_model.TopSellerModel;
import com.easyopstech.easyops.utils.MyMath;
import com.easyopstech.easyops.utils.SessionManager;

import java.text.MessageFormat;
import java.util.List;

/***
 * Created by mtita on 30,April,2019.
 */
public class TopSellerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<TopSellerModel> mDummies;
    private boolean sorted = false;

    private static final  int VIEW_TYPE_DATA=0;
    private static final int VIEW_TYPE_EMPTY=2;

    public TopSellerAdapter(Context context, List<TopSellerModel> dummies) {
        mContext = context;
        mDummies = dummies;
    }

    public TopSellerAdapter(Context context, List<TopSellerModel> dummies, boolean sorted) {
        mContext = context;
        mDummies = dummies;
        this.sorted = sorted;
    }

    @Override
    public int getItemViewType(int position) {
        if(mDummies.size()==0){
            return VIEW_TYPE_EMPTY;
        }else{
            return VIEW_TYPE_DATA;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        RecyclerView.ViewHolder vh;
        if(viewType==VIEW_TYPE_DATA) {
                view=LayoutInflater.from(mContext).inflate(R.layout.performence_leaderboard_top_seller_fragment_model, viewGroup, false);
                vh=new TViewHolder(view);
        }else{
            view=LayoutInflater.from(mContext).inflate(R.layout.empty_view_for_recycler, viewGroup, false);
            vh= new EmptyViewHolder(view);
        }
        return vh;
    }

    int pos;

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if(getItemViewType(i)==VIEW_TYPE_DATA) {
            TViewHolder tViewHolder = (TViewHolder) viewHolder;

            TopSellerModel topSellerModel = mDummies.get(i);

            topSellerModel.setAmount(MyMath.round(topSellerModel.getAmount(), 2));


            if (!sorted)
                pos = mDummies.size() - i;
            else
                pos = i + 1;
            tViewHolder.mBinding.textViewPos.setText(MessageFormat.format("{0}.", topSellerModel.getPos()));

            //color every even card background and user background
            if (topSellerModel.getName().equals(new SessionManager(mContext).getUserDetails().get("userName"))) {
                tViewHolder.mBinding.body.setBackgroundColor(Color.parseColor("#E1F5FE"));
            } else if (i % 2 == 0) {
                tViewHolder.mBinding.body.setBackgroundColor(Color.parseColor("#F5F5F5"));
            } else {
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
                    .load(topSellerModel.getImage_url()) //topSellerModel.getImage_url()
                    .placeholder(R.drawable.ic_employee)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(tViewHolder.mBinding.imageView6);


            tViewHolder.getBinding().setData(topSellerModel);
        }
    }

    @Override
    public int getItemCount() {
        if (mDummies.size() == 0)
            return 1;
        else
            return mDummies.size();
    }

    public class TViewHolder extends RecyclerView.ViewHolder {


        public PerformenceLeaderboardTopSellerFragmentModelBinding getBinding() {
            return mBinding;
        }

        private PerformenceLeaderboardTopSellerFragmentModelBinding mBinding;

        public TViewHolder(View itemView) {
            super(itemView);

            mBinding = DataBindingUtil.bind(itemView);

        }

    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
