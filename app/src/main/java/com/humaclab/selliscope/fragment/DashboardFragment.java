package com.humaclab.selliscope.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.humaclab.selliscope.R;
import com.humaclab.selliscope.activity.DeliveryListActivity;
import com.humaclab.selliscope.activity.InspectionActivity;
import com.humaclab.selliscope.activity.OutletActivity;
import com.humaclab.selliscope.activity.RouteActivity;
import com.humaclab.selliscope.activity.activity_salesReturnOld.SalesReturnActivityOld;
import com.humaclab.selliscope.adapters.DashboardRecyclerViewAdapter;
import com.humaclab.selliscope.model.DashboardItem;
import com.humaclab.selliscope.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nahid on 3/1/2017.
 */

public class DashboardFragment extends Fragment {
    List<DashboardItem> dashboadItems;
    private GridLayoutManager gridLayoutManager;
    private DashboardRecyclerViewAdapter dashboardRecyclerViewAdapter;
    private int itemNumber = 2;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View dashboardView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        RecyclerView recyclerView = dashboardView.findViewById(R.id.rv_dashboard);
        dashboadItems = new ArrayList<>();
        dashboadItems.add(new DashboardItem(getString(R.string.route), R.drawable.ic_route));
        dashboadItems.add(new DashboardItem(getString(R.string.outlet), R.drawable.ic_outlet));
//        dashboadItems.add(new DashboardItem("Product", R.drawable.ic_products));
//        dashboadItems.add(new DashboardItem("Order", R.drawable.ic_order));
        dashboadItems.add(new DashboardItem(getString(R.string.deliver), R.drawable.ic_view_orders));
//        dashboadItems.add(new DashboardItem("Payment", R.drawable.ic_payments));
        dashboadItems.add(new DashboardItem(getString(R.string.inspection), R.drawable.ic_inspection));
        dashboadItems.add(new DashboardItem(getString(R.string.sales_return), R.drawable.ic_sales_return));
//        dashboadItems.add(new DashboardItem("Insights", R.drawable.ic_insights));
        gridLayoutManager = new GridLayoutManager(activity, itemNumber);
        recyclerView.setLayoutManager(gridLayoutManager);
        dashboardRecyclerViewAdapter = new DashboardRecyclerViewAdapter(activity, dashboadItems);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_layout_margin);
        recyclerView.addItemDecoration(new DashboardRecyclerViewAdapter
                .GridSpacingItemDecoration(2, spacingInPixels, true, 0));
        recyclerView.setAdapter(dashboardRecyclerViewAdapter);
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
                                    case 1: {
                                        Log.d("tareq_test" , "Clicked");
                                        getActivity().startActivity(new Intent(getActivity(),
                                                OutletActivity.class));
                                        break;
                                    }
                                    /*case 2: {
                                        getActivity().startActivity(new Intent(getActivity(),
                                                ProductActivity.class));
                                        break;
                                    }
                                    case 3: {
                                        getActivity().startActivity(new Intent(getActivity(),
                                                OrderActivity.class));
                                        break;
                                    }*/
                                    case 2: {
                                        getActivity().startActivity(new Intent(getActivity(),
                                                DeliveryListActivity.class));
                                        break;
                                    }
/*                                    case 3: {
                                        getActivity().startActivity(new Intent(getActivity(),
                                                PaymentActivity.class));
                                        break;
                                    }*/
                                    case 3: {
                                        getActivity().startActivity(new Intent(getActivity(),
                                                InspectionActivity.class));
                                        break;
                                    }
                                    /*
                                    //new Sales Return
                                    case 4: {
                                        getActivity().startActivity(new Intent(getActivity(),
                                                SalesReturnActivity.class));
                                        break;
                                    }*/

                                    //old Sales Return
                                    case 4: {
                                        getActivity().startActivity(new Intent(getActivity(),
                                                SalesReturnActivityOld.class));
                                        break;
                                    }
/*                                    case 5: {
                                        Toast.makeText(getActivity(), "This feature is under development.",
                                                Toast.LENGTH_SHORT).show();
                                        break;
                                    }*/
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