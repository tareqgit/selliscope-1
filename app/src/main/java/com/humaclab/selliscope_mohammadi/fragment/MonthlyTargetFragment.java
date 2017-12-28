package com.humaclab.selliscope_mohammadi.fragment;

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
import com.humaclab.selliscope_mohammadi.R;
import com.humaclab.selliscope_mohammadi.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_mohammadi.SelliscopeApplication;
import com.humaclab.selliscope_mohammadi.adapters.TargetRecyclerViewAdapter;
import com.humaclab.selliscope_mohammadi.model.TargetItem;
import com.humaclab.selliscope_mohammadi.model.Targets;
import com.humaclab.selliscope_mohammadi.utils.NetworkUtility;
import com.humaclab.selliscope_mohammadi.utils.SessionManager;
import com.humaclab.selliscope_mohammadi.utils.VerticalSpaceItemDecoration;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Miaki on 2/27/17.
 */

public class MonthlyTargetFragment extends Fragment {
    private static final String KEY_POSITION = "position";
    SelliscopeApiEndpointInterface apiService;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    private TargetRecyclerViewAdapter targetRecyclerViewAdapter;
    private List<TargetItem> targetItems;

    public static MonthlyTargetFragment newInstance(int position) {
        MonthlyTargetFragment frag = new MonthlyTargetFragment();
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
        View monthlyTargetView = inflater.inflate(R.layout.target_layout, container, false);
        recyclerView = (RecyclerView) monthlyTargetView.findViewById(R.id.rv_target);
        swipeRefreshLayout = (SwipeRefreshLayout) monthlyTargetView.findViewById(R.id.srl_target);
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
        return monthlyTargetView;
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
                        targetRecyclerViewAdapter = new TargetRecyclerViewAdapter(getContext(),
                                getTargetListSuccessful.targetResult.monthlyTarget);

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
