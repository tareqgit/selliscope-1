package com.humaclab.selliscope_dimension_bd.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope_dimension_bd.R;
import com.humaclab.selliscope_dimension_bd.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_dimension_bd.SelliscopeApplication;
import com.humaclab.selliscope_dimension_bd.adapters.TargetRecyclerViewAdapter;
import com.humaclab.selliscope_dimension_bd.dbmodel.Target;
import com.humaclab.selliscope_dimension_bd.model.TargetItem;
import com.humaclab.selliscope_dimension_bd.model.Targets;
import com.humaclab.selliscope_dimension_bd.utils.DatabaseHandler;
import com.humaclab.selliscope_dimension_bd.utils.NetworkUtility;
import com.humaclab.selliscope_dimension_bd.utils.SessionManager;
import com.humaclab.selliscope_dimension_bd.utils.VerticalSpaceItemDecoration;

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
    List<Target> targets;
    private TargetRecyclerViewAdapter targetRecyclerViewAdapter;
    private List<TargetItem> targetItems;

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
        dbHandler = new DatabaseHandler(getContext());
        targets = new ArrayList<>();
        recyclerView = (RecyclerView) dailyTargetView.findViewById(R.id.rv_target);
        swipeRefreshLayout = (SwipeRefreshLayout) dailyTargetView.findViewById(R.id.srl_target);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtility.isNetworkAvailable(getActivity()))
                    getTargets();
                else
                    Toast.makeText(getActivity(), "Connect to Wifi or Mobile Data",
                            Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
        });
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(20));
        if (NetworkUtility.isNetworkAvailable(getActivity()))
            getTargets();
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
                        targetRecyclerViewAdapter = new TargetRecyclerViewAdapter(getContext(),
                                getTargetListSuccessful.targetResult.dailyTarget);

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
}
