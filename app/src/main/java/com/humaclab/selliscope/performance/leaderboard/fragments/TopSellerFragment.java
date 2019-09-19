/*
 * Created by Tareq Islam on 4/30/19 11:12 AM
 *
 *  Last modified 4/30/19 11:12 AM
 */

package com.humaclab.selliscope.performance.leaderboard.fragments;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.gson.Gson;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.databinding.PerformenceLeaderboardTopSellerFragmentBinding;
import com.humaclab.selliscope.model.performance.leaderboard_model.TopSellerModel;
import com.humaclab.selliscope.performance.leaderboard.adapters.TopSellerAdapter;
import com.humaclab.selliscope.performance.leaderboard.db_model.LeaderboardTotalPerticipatesResponse;
import com.humaclab.selliscope.performance.leaderboard.db_model.top_user.LeaderboardTopUserPositionResponse;
import com.humaclab.selliscope.utils.SessionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/***
 * Created by mtita on 30,April,2019.
 */
public class TopSellerFragment extends Fragment {

    private SelliscopeApiEndpointInterface apiService;
    private SessionManager sessionManager;

    public RecyclerView mRecyclerView;

    public static String sortTime="month";

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
        sessionManager = new SessionManager(getActivity());
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);

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


        //first second Third

        apiService.getTopUserPosition(sortTime).enqueue(new Callback<LeaderboardTopUserPositionResponse>() {
            @Override
            public void onResponse(Call<LeaderboardTopUserPositionResponse> call, Response<LeaderboardTopUserPositionResponse> response) {
                if(response.isSuccessful()){
                    Glide.with(mBinding.imageViewFirst)

                            .load(response.body().getData().get(0).getImageUrl())
                            .placeholder( R.drawable.default_profile_pic)
                            .transform(new CircleCrop())

                            .into(mBinding.imageViewFirst);

                    mBinding.firstName.setText(response.body().getData().get(0).getName());
                    mBinding.firstTotal.setText("1st\n"+ String.valueOf(response.body().getData().get(0).getGrandTotal()));

                    Glide.with(mBinding.imageViewSecond)
                            .load(response.body().getData().get(1).getImageUrl())
                            .placeholder( R.drawable.default_profile_pic)
                            .transform(new CircleCrop())

                            .into(mBinding.imageViewSecond);

                    mBinding.secondName.setText(response.body().getData().get(1).getName());
                    mBinding.secondTotal.setText("2nd\n"+ String.valueOf(response.body().getData().get(1).getGrandTotal()));

                    Glide.with(mBinding.imageViewThird)
                            .load(response.body().getData().get(2).getImageUrl())
                            .placeholder( R.drawable.default_profile_pic)
                            .transform(new CircleCrop())

                            .into(mBinding.imageViewThird);

                    mBinding.thirdName.setText(response.body().getData().get(2).getName());
                    mBinding.thirdTotal.setText("3rd\n"+ String.valueOf(response.body().getData().get(2).getGrandTotal()));
                }
            }

            @Override
            public void onFailure(Call<LeaderboardTopUserPositionResponse> call, Throwable t) {
                Log.e("tareq_test", "TopSellerFragment #116: onFailure:  " + t.getMessage());
            }
        });




        apiService.getTotalPerticipants(sortTime).enqueue(new Callback<LeaderboardTotalPerticipatesResponse>() {
            @Override
            public void onResponse(Call<LeaderboardTotalPerticipatesResponse> call, Response<LeaderboardTotalPerticipatesResponse> response) {
                if(response.isSuccessful()){
                    mBinding.textViewParticipants.setText( String.valueOf(response.body().getParticipants()) +"\n Participants");
                    mBinding.textViewTotalAvg.setText( String.valueOf(response.body().getCollected()) +"\n Collected");
                }else{
                    Log.d("tareq_test", "TopSellerFragment #131: onResponse:  Server Error");
                }
            }

            @Override
            public void onFailure(Call<LeaderboardTotalPerticipatesResponse> call, Throwable t) {
                Log.e("tareq_test", "TopSellerFragment #133: onFailure:  " + t.getMessage());
            }
        });

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
