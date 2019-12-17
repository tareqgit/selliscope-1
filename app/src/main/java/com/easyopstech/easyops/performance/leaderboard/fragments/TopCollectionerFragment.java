/*
 * Created by Tareq Islam on 4/30/19 11:12 AM
 *
 *  Last modified 4/30/19 11:12 AM
 */

package com.easyopstech.easyops.performance.leaderboard.fragments;

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
import com.easyopstech.easyops.R;
import com.easyopstech.easyops.RootApiEndpointInterface;
import com.easyopstech.easyops.RootApplication;
import com.easyopstech.easyops.databinding.PerformenceLeaderboardTopCollecterFragmentBinding;
import com.easyopstech.easyops.model.performance.leaderboard_model.TopCollectionerModel;
import com.easyopstech.easyops.performance.leaderboard.adapters.TopCollectionerAdapter;
import com.easyopstech.easyops.performance.leaderboard.db_model.LeaderboardTotalPerticipatesResponse;
import com.easyopstech.easyops.performance.leaderboard.db_model.ranking.RankingResponse;
import com.easyopstech.easyops.performance.leaderboard.db_model.top_user.LeaderboardTopUserPositionResponse;
import com.easyopstech.easyops.utils.MyMath;
import com.easyopstech.easyops.utils.SessionManager;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/***
 * Created by mtita on 30,April,2019.
 */
public class TopCollectionerFragment extends Fragment {

    private RootApiEndpointInterface apiService;

    private RecyclerView mRecyclerView;

    private static String sortTime = "";

    private List<TopCollectionerModel> datum = new ArrayList<>(/*Arrays.asList(
            new TopCollectionerModel("blah", 13,"Tareq", 200000),
            new TopCollectionerModel("blah", 15,"Rakib", 200000),
            new TopCollectionerModel("blah", 16,"Islam", 200000),
            new TopCollectionerModel("blah", 17,"Tareq", 200000),
            new TopCollectionerModel("blah", 18,"Rakib", 200000),
            new TopCollectionerModel("blah", 19,"Islam", 200000),
            new TopCollectionerModel("blah", 20,"Tareq", 200000),
            new TopCollectionerModel("blah", 21,"Rakib", 200000),
            new TopCollectionerModel("blah", 22,"Islam", 200000)

    )*/);

    public TopCollectionerFragment() {
    }

    public static TopCollectionerFragment newInstance() {
        return new TopCollectionerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager sessionManager = new SessionManager(getActivity());
        apiService = RootApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(RootApiEndpointInterface.class);

        setHasOptionsMenu(true);
    }

    PerformenceLeaderboardTopCollecterFragmentBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.performence_leaderboard_top_collecter_fragment, container, false);
        View view = mBinding.getRoot();
        onInit(view);
        return view;
    }

    private void onInit(View view) {
        mRecyclerView = mBinding.recyclerView;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        Collections.sort(datum, (o1, o2) -> (int) (o1.getAmount() - o2.getAmount()));

        mRecyclerView.setAdapter(new TopCollectionerAdapter(getActivity(), datum, sort_icon));


        loadData();
    }


    private static boolean sort_icon = true;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort:
                if (!sort_icon) {
                    item.setIcon(R.drawable.ic_sort_white_24dp);

                    sort_icon = true;
                    Collections.sort(datum, (o1, o2) -> (int) (o1.getAmount() - o2.getAmount()));

                    updateAdapter();

                    //do your task right here
                } else {
                    item.setIcon(R.drawable.ic_sort_white_rev_24);

                    sort_icon = false;
                    Collections.sort(datum, (o1, o2) -> (int) (o1.getAmount() - o2.getAmount()));
                    Collections.reverse(datum);
                    updateAdapter();

                    //do your task right here
                }
                return true;
            case R.id.sort_day:
                sortTime = "";
                loadData();

                return true;

            case R.id.sort_week:
                sortTime = "week";
                loadData();

                return true;

            case R.id.sort_month:
                sortTime = "month";
                loadData();

                return true;

            case R.id.sort_quarterly:
                sortTime = "quarter";
                loadData();

                return true;

        }
        return false;
    }

    private void loadData() {
        //here should load the data from api and update datum list
        mBinding.progressBarRecycler.setVisibility(View.VISIBLE);
        mBinding.progressBarRecycler.setEnabled(true);

        apiService.getCollectorUserRanking(sortTime).enqueue(new Callback<RankingResponse>() {
            @Override
            public void onResponse(Call<RankingResponse> call, Response<RankingResponse> response) {
                if (response.isSuccessful()) {

                    if (response.body() != null) {
                        datum.clear();
                        mBinding.progressBarRecycler.setEnabled(false);
                        mBinding.progressBarRecycler.setVisibility(View.GONE);
                        if (response.body().getPrevious2nd() != null) {
                            datum.add(new TopCollectionerModel(response.body().getPrevious2nd().getImageUrl(),
                                    response.body().getPrevious2nd().getPosition(),
                                    response.body().getPrevious2nd().getName(),
                                    response.body().getPrevious2nd().getAmount()));
                        }
                        if (response.body().getPrevious1st() != null) {
                            datum.add(new TopCollectionerModel(response.body().getPrevious1st().getImageUrl(),
                                    response.body().getPrevious1st().getPosition(),
                                    response.body().getPrevious1st().getName(),
                                    response.body().getPrevious1st().getAmount()));
                        }

                        if (response.body().getAuthenticUser() != null) {
                            datum.add(new TopCollectionerModel(response.body().getAuthenticUser().getImageUrl(),
                                    response.body().getAuthenticUser().getPosition(),
                                    response.body().getAuthenticUser().getName(),
                                    response.body().getAuthenticUser().getAmount()));
                        }
                        if (response.body().getNext1st() != null) {
                            datum.add(new TopCollectionerModel(response.body().getNext1st().getImageUrl(),
                                    response.body().getNext1st().getPosition(),
                                    response.body().getNext1st().getName(),
                                    response.body().getNext1st().getAmount()));
                        }

                        if (response.body().getNext2nd() != null) {
                            datum.add(new TopCollectionerModel(response.body().getNext2nd().getImageUrl(),
                                    response.body().getNext2nd().getPosition(),
                                    response.body().getNext2nd().getName(),
                                    response.body().getNext2nd().getAmount()));
                        }

                        mRecyclerView.setAdapter(new TopCollectionerAdapter(getActivity(), datum, sort_icon));

                    } else {
                        Log.d("tareq_test", "TopCollectionerFragment #209: onResponse:  null response body found");
                    }
                }
            }

            @Override
            public void onFailure(Call<RankingResponse> call, Throwable t) {
                Log.e("tareq_test", "TopCollectionerFragment #239: onFailure:  " + t.getMessage());
            }
        });

        apiService.getTopCollectorUserPosition(sortTime).enqueue(new Callback<LeaderboardTopUserPositionResponse>() {
            @Override
            public void onResponse(Call<LeaderboardTopUserPositionResponse> call, Response<LeaderboardTopUserPositionResponse> response) {
                if (response.isSuccessful()) {

                    if (response.body() != null && response.body().getData() != null) {

                        Glide.with(getContext().getApplicationContext())

                                .load(response.body().getData().size() > 0 ? response.body().getData().get(0).getImageUrl() : "")
                                .placeholder(R.drawable.ic_man)
                                .transform(new CircleCrop())

                                .into(mBinding.imageViewFirst);

                        mBinding.firstName.setText(response.body().getData().size() > 0 ? response.body().getData().get(0).getName() : "None");
                        mBinding.firstTotal.setText(String.format("1st\n%s", MyMath.round(response.body().getData().size() > 0 ? response.body().getData().get(0).getAmount() : 0, 2)));

                        Glide.with(getContext().getApplicationContext())
                                .load(response.body().getData().size() > 1 ? response.body().getData().get(1).getImageUrl() : "")
                                .placeholder(R.drawable.ic_girl)
                                .transform(new CircleCrop())
                                .into(mBinding.imageViewSecond);

                        mBinding.secondName.setText(response.body().getData().size() > 1 ? response.body().getData().get(1).getName() : "None");
                        mBinding.secondTotal.setText(String.format("2nd\n%s", String.valueOf(MyMath.round(response.body().getData().size() > 1 ? response.body().getData().get(1).getAmount() : 0, 2))));

                        Glide.with(getContext().getApplicationContext())
                                .load(response.body().getData().size() > 2 ? response.body().getData().get(2).getImageUrl() : "")
                                .placeholder(R.drawable.ic_old_man)
                                .transform(new CircleCrop())
                                .into(mBinding.imageViewThird);

                        mBinding.thirdName.setText(response.body().getData().size() > 2 ? response.body().getData().get(2).getName() : "None");
                        mBinding.thirdTotal.setText(String.format("3rd\n%s", String.valueOf(MyMath.round(response.body().getData().size() > 2 ? response.body().getData().get(2).getAmount() : 0, 2))));
                    }
                }
            }


            @Override
            public void onFailure(Call<LeaderboardTopUserPositionResponse> call, Throwable t) {
                Log.d("tareq_test", "TopCollectionerFragment #282: onFailure:  " + t.getMessage());
            }
        });

        apiService.getTotalCollectorPerticipants(sortTime).enqueue(new Callback<LeaderboardTotalPerticipatesResponse>() {
            @Override
            public void onResponse(Call<LeaderboardTotalPerticipatesResponse> call, Response<LeaderboardTotalPerticipatesResponse> response) {
                if (response.isSuccessful()) {
                    mBinding.textViewParticipants.setText(String.valueOf(response.body().getParticipants()) + "\n Participants");
                    mBinding.textViewTotalAvg.setText(String.format("%s\n Collected", String.valueOf(MyMath.round(response.body().getCollected(), 2))));
                } else {
                    Log.d("tareq_test", "TopSellerFragment #131: onResponse:  Server Error");
                }
            }

            @Override
            public void onFailure(Call<LeaderboardTotalPerticipatesResponse> call, Throwable t) {
                Log.d("tareq_test", "TopCollectionerFragment #300: onFailure:  " + t.getMessage());
            }
        });
    }



    private void updateAdapter() {
        mRecyclerView.setAdapter(new TopCollectionerAdapter(getActivity(), datum, sort_icon));

    }

}
