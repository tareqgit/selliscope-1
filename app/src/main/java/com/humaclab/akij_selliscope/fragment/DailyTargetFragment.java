package com.humaclab.akij_selliscope.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.eralp.circleprogressview.CircleProgressView;
import com.google.gson.Gson;
import com.humaclab.akij_selliscope.R;
import com.humaclab.akij_selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.akij_selliscope.SelliscopeApplication;
import com.humaclab.akij_selliscope.adapters.TargetRecyclerViewAdapter;
import com.humaclab.akij_selliscope.adapters.TargetRecyclerViewAdapter_new;
import com.humaclab.akij_selliscope.dbmodel.Target;
import com.humaclab.akij_selliscope.model.Target.OutletTarget;
import com.humaclab.akij_selliscope.model.TargetItem;
import com.humaclab.akij_selliscope.model.Targets;
import com.humaclab.akij_selliscope.utils.DatabaseHandler;
import com.humaclab.akij_selliscope.utils.NetworkUtility;
import com.humaclab.akij_selliscope.utils.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Miaki on 2/27/17.
 */

public class DailyTargetFragment extends Fragment {
    private static final String KEY_POSITION = "position";
    SelliscopeApiEndpointInterface apiService;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    DatabaseHandler dbHandler;
    List<OutletTarget.Result> targets;
    private TargetRecyclerViewAdapter_new targetRecyclerViewAdapter;
    private List<Target> targets_list;
    private List<TargetItem> targetItems;
    private TextView tv_total,tv_target_achieved,tv_target_remaining,tv_target_label,tv_date;
    private CircleProgressView circle_progress_view;
    public static DailyTargetFragment newInstance(int position) {
        DailyTargetFragment frag = new DailyTargetFragment();
        Bundle args = new Bundle();

        args.putInt(KEY_POSITION, position);
        frag.setArguments(args);
        return (frag);
    }

//    static String getTitle(Context ctxt, int position) {
//        return(String.format(ctxt.getString(R.string.hint), position + 1));
//    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View dailyTargetView = inflater.inflate(R.layout.target_layout, container, false);
        //tv_total = dailyTargetView.findViewById(R.id.tv_target_total);
        //tv_date = dailyTargetView.findViewById(R.id.tv_date);
        //tv_target_achieved = dailyTargetView.findViewById(R.id.tv_target_achieved);
        //tv_target_remaining = dailyTargetView.findViewById(R.id.tv_target_remaining);
        //tv_target_label = dailyTargetView.findViewById(R.id.tv_target_label);
        recyclerView = dailyTargetView.findViewById(R.id.rv_target);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        targets = new ArrayList<>();
        circle_progress_view = (CircleProgressView) dailyTargetView.findViewById(R.id.circle_progress_view);
//        dbHandler = new DatabaseHandler(getContext());
//        targets = new ArrayList<>();
//        recyclerView = (RecyclerView) dailyTargetView.findViewById(R.id.rv_target);
        swipeRefreshLayout = (SwipeRefreshLayout) dailyTargetView.findViewById(R.id.srl_target);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtility.isNetworkAvailable(getActivity()))
                    //getTargets();
                    loadTargetOutlet();
                else
                    Toast.makeText(getActivity(), "Connect to Wifi or Mobile Data",
                            Toast.LENGTH_SHORT).show();
            }
        });
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//        });
//        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(20));
        if (NetworkUtility.isNetworkAvailable(getActivity()))
           //getTargets();
            loadTargetOutlet();
        else
            Toast.makeText(getActivity(), "Connect to Wifi or Mobile Data",
                    Toast.LENGTH_SHORT).show();
        return dailyTargetView;
    }

    void getTargets() {
        SessionManager sessionManager = new SessionManager(getContext());
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);
        Call<ResponseBody> call = apiService.getTargets();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                if (response.code() == 200) {
                    try {
                        Targets.Successful getTargetListSuccessful = gson.fromJson(response.body().string()
                                , Targets.Successful.class);
                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);
//                        for (Targets.Successful.Target target :
//                                getTargetListSuccessful.targetResult.dailyTarget) {
//                            targets.add(new Target("Must Sales", "Daily", target.mustSales.target,
//                                    target.mustSales.achieved));
//                            targets.add(new Target("Priority Sales", "Daily", target.prioritySales.target,
//                                    target.prioritySales.achieved));
//                            targets.add(new Target("Regular Sales", "Daily", target.regularSales.target,
//                                    target.mustSales.achieved));
//                        }
//                        targetRecyclerViewAdapter = new TargetRecyclerViewAdapter(getContext(),
//                                getTargetListSuccessful.targetResult.dailyTarget);

                        recyclerView.setAdapter(targetRecyclerViewAdapter);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(getContext(),
                            "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(),
                            "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                Log.d("Response", t.toString());

            }
        });

    }
    private void loadTargetOutlet(){
        SessionManager sessionManager = new SessionManager(getContext());
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),sessionManager.getUserPassword(),false).create(SelliscopeApiEndpointInterface.class);
        Call<OutletTarget> call = apiService.getTarget();
        call.enqueue(new Callback<OutletTarget>() {
            @Override
            public void onResponse(Call<OutletTarget> call, Response<OutletTarget> response) {
                if(response.code() == 200) {
                    if (swipeRefreshLayout.isRefreshing())
                        swipeRefreshLayout.setRefreshing(false);
                    targets = response.body().getResult();
                    System.out.println("Response_target " + new Gson().toJson(response.body()));
                    targetRecyclerViewAdapter = new TargetRecyclerViewAdapter_new(getContext(),
                            targets);

                    recyclerView.setAdapter(targetRecyclerViewAdapter);

                    //String sales_types = response.body().getResult().getSalesTypes().replace("Quantity","qt");
                    //Double total = Double.valueOf(response.body().getResult().getSalesTarget().replace(",",""));
                    //Double achieved = Double.valueOf(response.body().getResult().getAchieved().replace(",",""));
                    //Double remaining = total-achieved;
                    //int completePersentage = (int) ((achieved * 100)/total);
//
                    //tv_date.setText(response.body().getResult().getDate());
                    //tv_target_label.setText(response.body().getResult().getTargetType());
                    //tv_target_achieved.setText(response.body().getResult().getAchieved()+" "+sales_types);
                    //tv_total.setText(total.toString()+" "+sales_types);
                    ////tv_visited.setText(response.body().getResult().getVisited());
                    //tv_target_remaining.setText(remaining.toString()+" "+sales_types);
                    //circle_progress_view.setTextEnabled(true);
                    //circle_progress_view.setInterpolator(new AccelerateDecelerateInterpolator());
                    //circle_progress_view.setStartAngle(10);
                    //circle_progress_view.setProgressWithAnimation(completePersentage,2000);


                }else if (response.code() == 401) {

                    if (swipeRefreshLayout.isRefreshing())
                        swipeRefreshLayout.setRefreshing(false);


                } else {

                    if (swipeRefreshLayout.isRefreshing())
                        swipeRefreshLayout.setRefreshing(false);


                }

            }

            @Override
            public void onFailure(Call<OutletTarget> call, Throwable t) {
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });

    }
}
