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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.databinding.PerformenceLeaderboardTopCollecterFragmentBinding;
import com.humaclab.selliscope.model.performance.leaderboard_model.TopCollectionerModel;
import com.humaclab.selliscope.performance.leaderboard.adapters.TopCollectionerAdapter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/***
 * Created by mtita on 30,April,2019.
 */
public class TopCollectionerFragment extends Fragment {
    public SwipeRefreshLayout mSwipeRefreshLayout;
    public RecyclerView mRecyclerView;

    public static List<TopCollectionerModel> datum = new ArrayList<>(Arrays.asList(
            new TopCollectionerModel("blah", "Tareq", 200000),
            new TopCollectionerModel("blah", "Rakib", 200000),
            new TopCollectionerModel("blah", "Islam", 200000),
            new TopCollectionerModel("blah", "Tareq", 200000),
            new TopCollectionerModel("blah", "Rakib", 200000),
            new TopCollectionerModel("blah", "Islam", 200000),
            new TopCollectionerModel("blah", "Tareq", 200000),
            new TopCollectionerModel("blah", "Rakib", 200000),
            new TopCollectionerModel("blah", "Islam", 200000),
            new TopCollectionerModel("blah", "Tareq", 200000),
            new TopCollectionerModel("blah", "Rakib", 200000),
            new TopCollectionerModel("blah", "Islam", 200000),
            new TopCollectionerModel("blah", "Tareq", 200000),
            new TopCollectionerModel("blah", "Rakib", 200000),
            new TopCollectionerModel("blah", "Islam", 200000),
            new TopCollectionerModel("blah", "Tareq", 200000),
            new TopCollectionerModel("blah", "Rakib", 200000),
            new TopCollectionerModel("blah", "Islam", 200000)
    ));

    public TopCollectionerFragment() {
    }

    public static TopCollectionerFragment newInstance() {
        return new TopCollectionerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    PerformenceLeaderboardTopCollecterFragmentBinding mBinding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       mBinding = DataBindingUtil.inflate(inflater,R.layout.performence_leaderboard_top_collecter_fragment, container, false);
        View view = mBinding.getRoot();
        onInit(view);
        return view;
    }

    private void onInit(View view) {

         mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.recyclerView.setHasFixedSize(true);
        Collections.sort(datum, (o1, o2) -> (int) (o1.getAmount()-o2.getAmount()));

        mBinding.recyclerView.setAdapter(new TopCollectionerAdapter(getActivity(), datum, sort_icon));

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


    private static boolean sort_icon = true;

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
        mRecyclerView.setAdapter(new TopCollectionerAdapter(getActivity(), datum, sort_icon));
    }

    private void loadWeekData() {

        //here should load the data from api and update datum list
        mRecyclerView.setAdapter(new TopCollectionerAdapter(getActivity(), datum, sort_icon));
    }

    private void loadTodayData() {

        //here should load the data from api and update datum list
        mRecyclerView.setAdapter(new TopCollectionerAdapter(getActivity(), datum, sort_icon));
    }

    private void updateAdapter() {
        mRecyclerView.setAdapter(new TopCollectionerAdapter(getActivity(), datum, sort_icon));

    }

}
