package com.easyopstech.easyops.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.easyopstech.easyops.R;
import com.easyopstech.easyops.RootApplication;
import com.easyopstech.easyops.adapters.DashboardRecyclerViewAdapter;
import com.easyopstech.easyops.adapters.PerformanceRecyclerViewAdapter;
import com.easyopstech.easyops.model.DashboardItem;
import com.easyopstech.easyops.my_test.MyTestActivity;
import com.easyopstech.easyops.performance.daily_activities.RegularPerformanceActivity;
import com.easyopstech.easyops.performance.leaderboard.LeaderBoardActivity;
import com.easyopstech.easyops.performance.orders.PerformanceOrdersActivity;
import com.easyopstech.easyops.performance.payments.PerformancePaymentsActivity;
import com.easyopstech.easyops.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nahid on 3/1/2017.
 */

public class PerformanceFragment extends Fragment {
    List<DashboardItem> performanceItems;
    private GridLayoutManager gridLayoutManager;
    private PerformanceRecyclerViewAdapter performanceRecyclerViewAdapter;
    private int itemNumber = 2;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View dashboardView = inflater.inflate(R.layout.fragment_performance, container, false);
        RecyclerView recyclerView = dashboardView.findViewById(R.id.rv_performance);
        performanceItems = new ArrayList<>();
     /*   performanceItems.add(new DashboardItem("Target", R.drawable.ic_target_performance));
        performanceItems.add(new DashboardItem("Visited Outlet", R.drawable.ic_visited_outlet));
     */
        performanceItems.add(new DashboardItem(getString(R.string.leaderboard), R.drawable.ic_trophy));
        performanceItems.add(new DashboardItem(getString(R.string.orders), R.drawable.ic_order_list));
        performanceItems.add(new DashboardItem(getString(R.string.payments), R.drawable.ic_payments));
        // performanceItems.add(new DashboardItem("Sales Return Request", R.drawable.ic_return));
        //   performanceItems.add(new DashboardItem("Commission", R.drawable.ic_commission));
        //  performanceItems.add(new DashboardItem("New Outlet", R.drawable.ic_outlet));
        performanceItems.add(new DashboardItem("Daily Activities", R.drawable.ic_attendence));
        if(RootApplication.developer)
        performanceItems.add(new DashboardItem("My Test", R.drawable.ic_outlet));


        gridLayoutManager = new GridLayoutManager(activity, itemNumber);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_layout_margin);

        recyclerView.addItemDecoration(new DashboardRecyclerViewAdapter
                .GridSpacingItemDecoration(2, spacingInPixels, true, 0));
        recyclerView.setLayoutManager(gridLayoutManager);
        performanceRecyclerViewAdapter = new PerformanceRecyclerViewAdapter(activity, performanceItems);

        recyclerView.setAdapter(performanceRecyclerViewAdapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(activity, recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                switch (position) {

                                    case 0:
                                        getActivity().startActivity(new Intent(getActivity(), LeaderBoardActivity.class));
                                        break;


                                    case 1: {
                                        getActivity().startActivity(new Intent(getActivity(), PerformanceOrdersActivity.class));
                                        break;
                                    }
                                    case 2: {
                                        getActivity().startActivity(new Intent(getActivity(), PerformancePaymentsActivity.class));
                                        break;
                                    }
                                    case 3: {
                                        getActivity().startActivity(new Intent(getActivity(), RegularPerformanceActivity.class));
                                        break;
                                    }

                                        case 4: {
                                            getActivity().startActivity(new Intent(getActivity(), MyTestActivity.class));
                                            break;
                                        }



                                    default: {
                                        Toast.makeText(getActivity(), "Please upgrade your package" +
                                                        " to use this feature",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                // do whatever
                            }
                        })
        );
        return dashboardView;
    }
}