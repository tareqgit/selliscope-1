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
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.databinding.PerformenceLeaderboardTopSellerFragmentBinding;
import com.humaclab.selliscope.model.performance.leaderboard_model.TopSellerModel;
import com.humaclab.selliscope.performance.leaderboard.adapters.TopSellerAdapter;
import com.humaclab.selliscope.performance.leaderboard.db_model.LeaderboardTotalPerticipatesResponse;
import com.humaclab.selliscope.performance.leaderboard.db_model.ranking.RankingResponse;
import com.humaclab.selliscope.performance.leaderboard.db_model.top_user.LeaderboardTopUserPositionResponse;
import com.humaclab.selliscope.utils.MyMath;
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

    private RecyclerView mRecyclerView;

    private static String sortTime="";

    private  List<TopSellerModel> datum = new ArrayList<>(/*Arrays.asList(
            new TopSellerModel( "blah", 13,"Tareq" , 200000),
            new TopSellerModel("blah", 14,"Rakib" , 190000),
            new TopSellerModel("blah",15,"Islam" , 180000),
            new TopSellerModel("blah",16,"Tareq" , 100000),
            new TopSellerModel("blah",17,"Rakib" , 110000)

    )*/);
    public TopSellerFragment() {
    }

    public static TopSellerFragment newInstance(){
        return new TopSellerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager sessionManager = new SessionManager(getActivity());
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


        loadData();


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
                sortTime="";
                loadData();

                return  true;

            case R.id.sort_week:
                sortTime="week";
               loadData();

                return true;

            case R.id.sort_month:
                sortTime="month";
               loadData();

                return true;

            case R.id.sort_quarterly:
                sortTime="quarter";
                loadData();

                return true;

        }
        return false;
    }


    private void loadData() {
        mBinding.progressBarRecycler.setVisibility(View.VISIBLE);
        mBinding.progressBarRecycler.setEnabled(true);
        //here should load the data from api and update datum list
        apiService.getInvoicerUserRanking(sortTime).enqueue(new Callback<RankingResponse>() {
            @Override
            public void onResponse(Call<RankingResponse> call, Response<RankingResponse> response) {
                if(response.isSuccessful()){

                    if(response.body()!=null) {
                        datum.clear();
                        mBinding.progressBarRecycler.setEnabled(false);
                        mBinding.progressBarRecycler.setVisibility(View.GONE);
                        if (response.body().getPrevious2nd() != null) {
                            datum.add(new TopSellerModel(response.body().getPrevious2nd().getImageUrl(),
                                    response.body().getPrevious2nd().getPosition(),
                                    response.body().getPrevious2nd().getName(),
                                    response.body().getPrevious2nd().getGrand_total()));
                        }
                        if (response.body().getPrevious1st() != null) {
                            datum.add(new TopSellerModel(response.body().getPrevious1st().getImageUrl(),
                                    response.body().getPrevious1st().getPosition(),
                                    response.body().getPrevious1st().getName(),
                                    response.body().getPrevious1st().getGrand_total()));
                        }

                        if (response.body().getAuthenticUser() != null) {
                            datum.add(new TopSellerModel(response.body().getAuthenticUser().getImageUrl(),
                                    response.body().getAuthenticUser().getPosition(),
                                    response.body().getAuthenticUser().getName(),
                                    response.body().getAuthenticUser().getGrand_total()));
                        }
                        if (response.body().getNext1st() != null) {
                            datum.add(new TopSellerModel(response.body().getNext1st().getImageUrl(),
                                    response.body().getNext1st().getPosition(),
                                    response.body().getNext1st().getName(),
                                    response.body().getNext1st().getGrand_total()));
                        }

                        if(response.body().getNext2nd() !=null) {
                            datum.add(new TopSellerModel(response.body().getNext2nd().getImageUrl(),
                                    response.body().getNext2nd().getPosition(),
                                    response.body().getNext2nd().getName(),
                                    response.body().getNext2nd().getGrand_total()));
                        }

                        TopSellerAdapter adapter = new TopSellerAdapter(getActivity(), datum, sort_icon);
                        mRecyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }else {
                        Log.d("tareq_test", "TopSellerFragment #209: onResponse:  null response body found");
                    }
                }
            }

            @Override
            public void onFailure(Call<RankingResponse> call, Throwable t) {
                Log.e("tareq_test", "TopSellerFragment #214: onFailure:  "+ t.getMessage());
            }
        });


        apiService.getTopInvoicerUserPosition(sortTime).enqueue(new Callback<LeaderboardTopUserPositionResponse>() {
            @Override
            public void onResponse(Call<LeaderboardTopUserPositionResponse> call, Response<LeaderboardTopUserPositionResponse> response) {
                if(response.isSuccessful()){

                    if (response.body() != null && response.body().getData() != null) {
                        Glide.with(mBinding.imageViewFirst)

                                .load(response.body().getData().size() > 0 ? response.body().getData().get(0).getImageUrl() : "")
                                .placeholder(R.drawable.ic_man)
                                .transform(new CircleCrop())

                                .into(mBinding.imageViewFirst);

                        mBinding.firstName.setText(response.body().getData().size() > 0 ? response.body().getData().get(0).getName() : "None");

                        mBinding.firstTotal.setText(String.format("1st\n%s", MyMath.round(response.body().getData().size() > 0 ? response.body().getData().get(0).getGrandTotal() : 0, 2)));

                        Glide.with(mBinding.imageViewSecond)
                                .load(response.body().getData().size() > 1 ? response.body().getData().get(1).getImageUrl() : "")
                                .placeholder(R.drawable.ic_girl)
                                .transform(new CircleCrop())
                                .into(mBinding.imageViewSecond);

                        mBinding.secondName.setText(response.body().getData().size() > 1 ? response.body().getData().get(1).getName() : "None");
                        mBinding.secondTotal.setText(String.format("2nd\n%s", String.valueOf(MyMath.round(response.body().getData().size() > 1 ? response.body().getData().get(1).getGrandTotal() : 0, 2))));

                        Glide.with(mBinding.imageViewThird)
                                .load(response.body().getData().size() > 2 ? response.body().getData().get(2).getImageUrl() : "")
                                .placeholder(R.drawable.ic_old_man)
                                .transform(new CircleCrop())
                                .into(mBinding.imageViewThird);

                        mBinding.thirdName.setText(response.body().getData().size() > 2 ? response.body().getData().get(2).getName() : "None");
                        mBinding.thirdTotal.setText(String.format("3rd\n%s", String.valueOf(MyMath.round(response.body().getData().size() > 2 ? response.body().getData().get(2).getGrandTotal() : 0, 2))));
                    }
                }
            }

            @Override
            public void onFailure(Call<LeaderboardTopUserPositionResponse> call, Throwable t) {
                Log.e("tareq_test", "TopSellerFragment #116: onFailure:  " + t.getMessage());
            }
        });




        apiService.getTotalInvoicerPerticipants(sortTime).enqueue(new Callback<LeaderboardTotalPerticipatesResponse>() {
            @Override
            public void onResponse(Call<LeaderboardTotalPerticipatesResponse> call, Response<LeaderboardTotalPerticipatesResponse> response) {
                if(response.isSuccessful()){
                    mBinding.textViewParticipants.setText( String.valueOf(response.body().getParticipants()) +"\n Participants");

                    mBinding.textViewTotalAvg.setText(String.format("%s\n Collected", String.valueOf(MyMath.round(response.body().getCollected(), 2))));
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


    private void updateAdapter() {

        mRecyclerView.setAdapter( new TopSellerAdapter(getActivity(), datum, sort_icon));

    }



}
