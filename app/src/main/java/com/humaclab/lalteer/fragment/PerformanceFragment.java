/*
 * Created by Tareq Islam on 4/3/19 2:12 PM
 *
 *  Last modified 3/28/19 10:52 AM
 */

package com.humaclab.lalteer.fragment;

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


import com.humaclab.lalteer.R;
import com.humaclab.lalteer.activity.RouteActivity;
import com.humaclab.lalteer.adapters.DashboardRecyclerViewAdapter;
import com.humaclab.lalteer.adapters.PerformanceRecyclerViewAdapter;
import com.humaclab.lalteer.model.DashboardItem;
import com.humaclab.lalteer.performance.orders.PerformanceOrdersActivity;
import com.humaclab.lalteer.performance.payments.PerformancePaymentsActivity;
import com.humaclab.lalteer.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nahid on 3/1/2017.
 */

public class PerformanceFragment extends Fragment {
    List<DashboardItem> performanceItems;
    private GridLayoutManager gridLayoutManager;
    private PerformanceRecyclerViewAdapter performanceRecyclerViewAdapter;
    private int itemNumber = 1;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View dashboardView = inflater.inflate(R.layout.fragment_performance, container, false);
        RecyclerView recyclerView = dashboardView.findViewById(R.id.rv_performance);
        performanceItems = new ArrayList<>();
        performanceItems.add(new DashboardItem("Target", R.drawable.ic_target_sales));
        performanceItems.add(new DashboardItem("Visited Outlet", R.drawable.ic_visited));
        performanceItems.add(new DashboardItem("Orders", R.drawable.ic_order_new));
        performanceItems.add(new DashboardItem("Payment", R.drawable.ic_payments));
        performanceItems.add(new DashboardItem("Sales Return Request", R.drawable.ic_return));
        performanceItems.add(new DashboardItem("Commission", R.drawable.ic_action_taka));
        performanceItems.add(new DashboardItem("New Outlet", R.drawable.ic_outlet));
        performanceItems.add(new DashboardItem("Attendance", R.drawable.ic_attendance));

        gridLayoutManager = new GridLayoutManager(activity, itemNumber);
        recyclerView.setLayoutManager(gridLayoutManager);
        performanceRecyclerViewAdapter = new PerformanceRecyclerViewAdapter(activity, performanceItems);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_layout_margin);
        recyclerView.addItemDecoration(new DashboardRecyclerViewAdapter
                .GridSpacingItemDecoration(2, spacingInPixels, true, 0));
        recyclerView.setAdapter(performanceRecyclerViewAdapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(activity, recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                switch (position) {
                                    case 0: {
                                        getActivity().startActivity(new Intent(getActivity(),
                                                RouteActivity.class));
                                        break;
                                    }
                                    case 2: {
                                       getActivity().startActivity(new Intent(getActivity(), PerformanceOrdersActivity.class));
                                        break;
                                    }
                                    case 3: {
                                        getActivity().startActivity(new Intent(getActivity(), PerformancePaymentsActivity.class));
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