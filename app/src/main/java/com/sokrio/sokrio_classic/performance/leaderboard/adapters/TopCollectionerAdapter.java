package com.sokrio.sokrio_classic.performance.leaderboard.adapters;

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

import com.sokrio.sokrio_classic.R;
import com.sokrio.sokrio_classic.databinding.PerformenceLeaderboardTopCollecterFragmentModelBinding;
import com.sokrio.sokrio_classic.model.performance.leaderboard_model.TopCollectionerModel;
import com.sokrio.sokrio_classic.utils.MyMath;
import com.sokrio.sokrio_classic.utils.SessionManager;

import java.text.MessageFormat;
import java.util.List;

/***
 * Created by mtita on 30,April,2019.
 */
public class TopCollectionerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<TopCollectionerModel> mDummies;
    private boolean sorted = false;

    private static final int VIEW_TYPE_DATA = 0;

    private static final int VIEW_TYPE_EMPTY = 2;

    public TopCollectionerAdapter(Context context, List<TopCollectionerModel> dummies) {
        mContext = context;
        mDummies = dummies;
    }

    public TopCollectionerAdapter(Context context, List<TopCollectionerModel> dummies, boolean sorted) {
        mContext = context;
        mDummies = dummies;
        this.sorted = sorted;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDummies.size() == 0) {
            return VIEW_TYPE_EMPTY;
        } else
            return VIEW_TYPE_DATA;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_TYPE_DATA) {
            view = LayoutInflater.from(mContext).inflate(R.layout.performence_leaderboard_top_collecter_fragment_model, viewGroup, false);
            vh = new TViewHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.empty_view_for_recycler, viewGroup, false);
            vh = new EmptyHolder(view);
        }
        return vh;
    }




    int pos;

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if(getItemViewType(i)==VIEW_TYPE_DATA) {
            TViewHolder tViewHolder= (TViewHolder) viewHolder;
            TopCollectionerModel topCollectionerModel = mDummies.get(i);

            topCollectionerModel.setAmount(MyMath.round(topCollectionerModel.getAmount(), 2));


            if (!sorted)
                pos = mDummies.size() - i;
            else
                pos = i + 1;
            tViewHolder.mBinding.textViewPos.setText(MessageFormat.format("Pos: {0}", topCollectionerModel.getPos()));

            //color every even card background and user background
            if (topCollectionerModel.getName().equals(new SessionManager(mContext).getUserDetails().get("userName"))) {
                tViewHolder.mBinding.body.setBackgroundColor(Color.parseColor("#E1F5FE"));
            } else if (i % 2 == 0) {
                tViewHolder.mBinding.body.setBackgroundColor(Color.parseColor("#F5F5F5"));
            } else {
                tViewHolder.mBinding.body.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }


            Glide.with(mContext)
                    .load(topCollectionerModel.getImage_url()) //topSellerModel.getImage_url()
                    .placeholder(R.drawable.ic_employee)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(tViewHolder.mBinding.imageView6);

            tViewHolder.mBinding.setData(topCollectionerModel);
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


        public PerformenceLeaderboardTopCollecterFragmentModelBinding getBinding() {
            return mBinding;
        }

        private PerformenceLeaderboardTopCollecterFragmentModelBinding mBinding;

        public TViewHolder(View itemView) {
            super(itemView);

            mBinding = DataBindingUtil.bind(itemView);

        }

    }

    public class EmptyHolder extends RecyclerView.ViewHolder {

        public EmptyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}
