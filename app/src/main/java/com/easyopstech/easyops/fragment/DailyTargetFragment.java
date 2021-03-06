package com.easyopstech.easyops.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.easyopstech.easyops.RootApiEndpointInterface;
import com.google.gson.Gson;
import com.easyopstech.easyops.R;
import com.easyopstech.easyops.RootApplication;
import com.easyopstech.easyops.adapters.TargetRecyclerViewAdapter;
import com.easyopstech.easyops.dbmodel.Target;
import com.easyopstech.easyops.model.target.OutletTarget;
import com.easyopstech.easyops.model.TargetItem;
import com.easyopstech.easyops.model.Targets;
import com.easyopstech.easyops.utils.DatabaseHandler;
import com.easyopstech.easyops.utils.NetworkUtility;
import com.easyopstech.easyops.utils.SessionManager;
import com.liulishuo.magicprogresswidget.MagicProgressCircle;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Miaki on 2/27/17.
 */

public class DailyTargetFragment extends Fragment {
    private static final String KEY_POSITION = "position";
    RootApiEndpointInterface apiService;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    DatabaseHandler dbHandler;
    List<Target> targets;
    private TargetRecyclerViewAdapter targetRecyclerViewAdapter;
    private List<TargetItem> targetItems;
    private TextView tv_total,tv_target_achieved,tv_target_remaining,tv_target_label,tv_date,percentView;

    private MagicProgressCircle circle_progress_view;
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
        tv_total = dailyTargetView.findViewById(R.id.tv_target_total);
        tv_date = dailyTargetView.findViewById(R.id.tv_date);
        tv_target_achieved = dailyTargetView.findViewById(R.id.tv_target_achieved);
        tv_target_remaining = dailyTargetView.findViewById(R.id.tv_target_remaining);
        tv_target_label = dailyTargetView.findViewById(R.id.tv_target_label);

        circle_progress_view = dailyTargetView.findViewById(R.id.circle_progress_view);
        percentView=dailyTargetView.findViewById(R.id.percentView);
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
        apiService = RootApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(RootApiEndpointInterface.class);
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
    private void loadTargetOutlet(){
        SessionManager sessionManager = new SessionManager(getContext());
        apiService = RootApplication.getRetrofitInstance(sessionManager.getUserEmail(),sessionManager.getUserPassword(),false).create(RootApiEndpointInterface.class);
        Call<OutletTarget> call = apiService.getTarget();
        call.enqueue(new Callback<OutletTarget>() {
            @Override
            public void onResponse(Call<OutletTarget> call, Response<OutletTarget> response) {
                if(response.code() == 200) {
                    if (swipeRefreshLayout.isRefreshing())
                        swipeRefreshLayout.setRefreshing(false);
                    System.out.println("Response " + new Gson().toJson(response.body()));

                    String sales_types = response.body().getResult().getSalesTypes().replace("Quantity","qt");
                    Double total = Double.valueOf(response.body().getResult().getSalesTarget().replace(",",""));
                    Double achieved = Double.valueOf(response.body().getResult().getAchieved().replace(",",""));
                    Double remaining = total-achieved;
                    int completePersentage = (int) ((achieved * 100)/total);

                    tv_date.setText(response.body().getResult().getDate());
                    tv_target_label.setText(response.body().getResult().getTargetType());
                    tv_target_achieved.setText(String.format(Locale.ENGLISH,"%,.2f %s",achieved,sales_types));
                    tv_total.setText(String.format(Locale.ENGLISH,"%,.2f %s",total,sales_types));
                    //tv_visited.setText(response.body().getResult().getVisited());
                    tv_target_remaining.setText(String.format(Locale.ENGLISH,"%,.2f %s",remaining, sales_types));
                 //   circle_progress_view.setTextEnabled(true);
                 //   circle_progress_view.setInterpolator(new AccelerateDecelerateInterpolator());
                 //   circle_progress_view.setStartAngle(90);
                  //  circle_progress_view.setProgressWithAnimation(completePersentage,2000);
                   circle_progress_view.setSmoothPercent(completePersentage*.01f);
                   percentView.setText(completePersentage+"%");



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
