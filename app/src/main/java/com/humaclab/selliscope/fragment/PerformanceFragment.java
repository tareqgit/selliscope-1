package com.humaclab.selliscope.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.humaclab.selliscope.R;
import com.humaclab.selliscope.activity.ActivitySalesReturnOld.SalesReturnActivityOld;
import com.humaclab.selliscope.activity.DeliveryListActivity;
import com.humaclab.selliscope.activity.InspectionActivity;
import com.humaclab.selliscope.activity.OutletActivity;
import com.humaclab.selliscope.activity.RouteActivity;
import com.humaclab.selliscope.adapters.DashboardRecyclerViewAdapter;
import com.humaclab.selliscope.adapters.PerformanceRecyclerViewAdapter;
import com.humaclab.selliscope.model.DashboardItem;
import com.humaclab.selliscope.utils.RecyclerItemClickListener;

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