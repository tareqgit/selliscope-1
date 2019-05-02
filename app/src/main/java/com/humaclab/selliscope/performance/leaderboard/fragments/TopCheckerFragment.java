/*
 * Created by Tareq Islam on 4/30/19 11:12 AM
 *
 *  Last modified 4/30/19 11:12 AM
 */

package com.humaclab.selliscope.performance.leaderboard.fragments;

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

import com.humaclab.selliscope.R;
import com.humaclab.selliscope.model.performance.leaderboard_model.TopCheckerModel;
import com.humaclab.selliscope.performance.leaderboard.adapters.TopCheckerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/***
 * Created by mtita on 30,April,2019.
 */
public class TopCheckerFragment extends Fragment {
    public SwipeRefreshLayout mSwipeRefreshLayout;
    public RecyclerView mRecyclerView;
    public static List<TopCheckerModel> datum = new ArrayList<>(Arrays.asList(
            new TopCheckerModel("blah","Tareq" , 200000),
            new TopCheckerModel("blah","Rakib" , 200000),
            new TopCheckerModel("blah","Islam" , 200000),
            new TopCheckerModel("blah","Tareq" , 200000),
            new TopCheckerModel("blah","Rakib" , 200000),
            new TopCheckerModel("blah","Islam" , 200000),
            new TopCheckerModel("blah","Tareq" , 200000),
            new TopCheckerModel("blah","Rakib" , 200000),
            new TopCheckerModel("blah","Islam" , 200000),
            new TopCheckerModel("blah","Tareq" , 200000),
            new TopCheckerModel("blah","Rakib" , 200000),
            new TopCheckerModel("blah","Islam" , 200000),
            new TopCheckerModel("blah","Tareq" , 200000),
            new TopCheckerModel("blah","Rakib" , 200000),
            new TopCheckerModel("blah","Islam" , 200000),
            new TopCheckerModel("blah","Tareq" , 200000),
            new TopCheckerModel("blah","Rakib" , 200000),
            new TopCheckerModel("blah","Islam" , 200000)
    ));
    public TopCheckerFragment() {
    }

    public  static TopCheckerFragment newInstance(){
        return new TopCheckerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.performance_leaderboard_top_checker_fragment, container, false);
        onInit(view);
        return view;
    }

    private void onInit(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.recycler_loader);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        Collections.sort(datum, (o1, o2) -> (int) (o1.getNo_outlet()-o2.getNo_outlet()));
        mRecyclerView.setAdapter( new TopCheckerAdapter(getActivity(), datum, sort_icon));

        mSwipeRefreshLayout.setRefreshing(true);
    }

    private   static boolean sort_icon = true;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort:
                if (!sort_icon) {
                    item.setIcon(R.drawable.ic_sort_white_24dp);

                    sort_icon = true;
                    Collections.sort(datum, (o1, o2) -> (int) (o1.getNo_outlet()-o2.getNo_outlet()));
                    Collections.reverse(datum);
                    updateAdapter();

                    //do your task right here
                } else {
                    item.setIcon(R.drawable.ic_sort_white_rev_24);

                    sort_icon = false;
                    Collections.sort(datum, (o1, o2) -> (int) (o1.getNo_outlet()-o2.getNo_outlet()));

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
        mRecyclerView.setAdapter( new TopCheckerAdapter(getActivity(), datum, sort_icon));
    }

    private void loadWeekData() {

        //here should load the data from api and update datum list
        mRecyclerView.setAdapter( new TopCheckerAdapter(getActivity(), datum, sort_icon));
    }

    private void loadTodayData() {

        //here should load the data from api and update datum list
        mRecyclerView.setAdapter( new TopCheckerAdapter(getActivity(), datum, sort_icon));
    }

    private void updateAdapter() {


        mRecyclerView.setAdapter( new TopCheckerAdapter(getActivity(), datum, sort_icon));

    }
}
