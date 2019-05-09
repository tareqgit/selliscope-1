/*
 * Created by Tareq Islam on 4/30/19 11:12 AM
 *
 *  Last modified 4/30/19 11:12 AM
 */

package com.humaclab.selliscope.performance.leaderboard.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.databinding.PerformenceLeaderboardTopSellerFragmentBinding;
import com.humaclab.selliscope.model.performance.leaderboard_model.TopSellerModel;
import com.humaclab.selliscope.performance.leaderboard.LeaderBoardActivity;
import com.humaclab.selliscope.performance.leaderboard.adapters.TopSellerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/***
 * Created by mtita on 30,April,2019.
 */
public class TopSellerFragment extends Fragment {



    public RecyclerView mRecyclerView;


    public static List<TopSellerModel> datum = new ArrayList<>(Arrays.asList(
            new TopSellerModel( "blah", 13,"Tareq" , 200000),
            new TopSellerModel("blah", 14,"Rakib" , 190000),
            new TopSellerModel("blah",15,"Islam" , 180000),
            new TopSellerModel("blah",16,"Tareq" , 100000),
            new TopSellerModel("blah",17,"Rakib" , 110000)

    ));
    public TopSellerFragment() {
    }

    public static TopSellerFragment newInstance(){
        return new TopSellerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    PerformenceLeaderboardTopSellerFragmentBinding mBinding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.performence_leaderboard_top_seller_fragment, container, false);
        View view = mBinding.getRoot();
        onInit(view);

        return view;
    }

    private void onInit(View view) {

    mRecyclerView=  mBinding.recyclerView;
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        Collections.sort(datum, (o1, o2) -> (int) (o1.getAmount()-o2.getAmount())); //sorting array by amount

        mRecyclerView.setAdapter( new TopSellerAdapter(getActivity(), datum, sort_icon));





        Glide.with(mBinding.imageViewFirst)
                .load(R.drawable.default_profile_pic)
                .transform(new CircleCrop())

                .into(mBinding.imageViewFirst);

        Glide.with(mBinding.imageViewSecond)
                .load(R.drawable.default_profile_pic)
                .transform(new CircleCrop())

                .into(mBinding.imageViewSecond);

        Glide.with(mBinding.imageViewThird)
                .load(R.drawable.default_profile_pic)
                .transform(new CircleCrop())

                .into(mBinding.imageViewThird);



    }


    private   static boolean sort_icon = true;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort:
                if (!sort_icon) {
                    item.setIcon(R.drawable.ic_sort_white_24dp);

                    sort_icon = true;
                    Collections.sort(datum, (o1, o2) -> (int) (o1.getAmount()-o2.getAmount()));

                    updateAdapter();

                    //do your task right here
                } else {
                    item.setIcon(R.drawable.ic_sort_white_rev_24);

                    sort_icon = false;

                    Collections.sort(datum, (o1, o2) -> (int) (o1.getAmount()-o2.getAmount()));
                    Collections.reverse(datum);
                    updateAdapter();

                    //do your task right here
                }
              return true;
            case R.id.sort_day:
                loadTodayData();
                return  true;

            case R.id.sort_week:
                loadWeekData();
                return true;

            case R.id.sort_month:
                loadMonthData();
                return true;

        }
        return false;
    }

    private void loadMonthData() {
        //here should load the data from api and update datum list
        mRecyclerView.setAdapter( new TopSellerAdapter(getActivity(), datum, sort_icon));
    }

    private void loadWeekData() {

        //here should load the data from api and update datum list
        mRecyclerView.setAdapter( new TopSellerAdapter(getActivity(), datum, sort_icon));
    }

    private void loadTodayData() {

        //here should load the data from api and update datum list
        mRecyclerView.setAdapter( new TopSellerAdapter(getActivity(), datum, sort_icon));
    }


    private void updateAdapter() {

        mRecyclerView.setAdapter( new TopSellerAdapter(getActivity(), datum, sort_icon));

    }



}
