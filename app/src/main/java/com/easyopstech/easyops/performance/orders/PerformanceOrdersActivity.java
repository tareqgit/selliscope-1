/*
 * Created by Tareq Islam on 3/27/19 11:12 AM
 *
 *  Last modified 3/27/19 11:12 AM
 */

package com.easyopstech.easyops.performance.orders;

import android.app.DatePickerDialog;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.easyopstech.easyops.RootApiEndpointInterface;
import com.easyopstech.easyops.RootApplication;
import com.google.gson.Gson;
import com.easyopstech.easyops.R;
import com.easyopstech.easyops.databinding.ActivityPerformanceOrdersBinding;
import com.easyopstech.easyops.model.performance.orders_model.Order;
import com.easyopstech.easyops.model.performance.orders_model.PerformanceOrderResponse;
import com.easyopstech.easyops.model.performance.orders_model.Product;
import com.easyopstech.easyops.utils.CurrentTimeUtilityClass;
import com.easyopstech.easyops.utils.SessionManager;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerformanceOrdersActivity extends AppCompatActivity implements PerformenceOrdersAdapter.OnOrderItemClickListener {

    private SessionManager sessionManager;
    private RootApiEndpointInterface apiService;
   private static String startDate="";
   private static String endDate="";

    private     ActivityPerformanceOrdersBinding mActivityPerformanceOrdersBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       mActivityPerformanceOrdersBinding= DataBindingUtil.setContentView(this,R.layout.activity_performance_orders);

        sessionManager = new SessionManager(this);
        apiService = RootApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), true).create(RootApiEndpointInterface.class);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getString(R.string.order));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivityPerformanceOrdersBinding.ordersRecycler.setLayoutManager(new LinearLayoutManager(this));

       startDate= CurrentTimeUtilityClass.getCurrentTimeStampDate();
       endDate=CurrentTimeUtilityClass.getCurrentTimeStampDate();

        mActivityPerformanceOrdersBinding.recyclerLoader.setRefreshing(true);
        mActivityPerformanceOrdersBinding.recyclerLoader.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                performanceOrderAction(startDate,endDate); //for Initial value in recyclerview get the data of today

            }
        });

        performanceOrderAction(startDate,endDate); //for Initial value in recyclerview get the data of today

        mActivityPerformanceOrdersBinding.startDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                final int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog= new DatePickerDialog(PerformanceOrdersActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mActivityPerformanceOrdersBinding.startDateText.setText(year + "-"+ (month+1) + "-"+dayOfMonth);
                        startDate=mActivityPerformanceOrdersBinding.startDateText.getText().toString();

                    }
                },year,month,day);
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                datePickerDialog.show();
                }
        });

        mActivityPerformanceOrdersBinding.endDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog= new DatePickerDialog(PerformanceOrdersActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mActivityPerformanceOrdersBinding.endDateText.setText(year + "-"+ (month+1) + "-"+dayOfMonth);
                        endDate=mActivityPerformanceOrdersBinding.endDateText.getText().toString();

                    }
                },year,month,day);
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                datePickerDialog.show();
                          }
        });

        mActivityPerformanceOrdersBinding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mActivityPerformanceOrdersBinding.recyclerLoader.setRefreshing(true);


                  performanceOrderAction(mActivityPerformanceOrdersBinding.startDateText.getText().toString(), mActivityPerformanceOrdersBinding.endDateText.getText().toString());

            }



        });


    }
    private void performanceOrderAction(String date_from, String date_to) {
        apiService.performanceOrder(date_from  , date_to)
                .enqueue(new Callback<PerformanceOrderResponse>() {
                    @Override
                    public void onResponse(Call<PerformanceOrderResponse> call, Response<PerformanceOrderResponse> response) {
                        Log.d("tareq_test" , ""+response.code());

                        if(response.code()==200) {
                            Log.d("tareq_test", "" + new Gson().toJson(response.body()));


                            mActivityPerformanceOrdersBinding.ordersRecycler.setAdapter(new PerformenceOrdersAdapter(PerformanceOrdersActivity.this, response.body().getResult().getOrders(), PerformanceOrdersActivity.this));
                            mActivityPerformanceOrdersBinding.totalAmountTextView.setText(getString(R.string.toatal)+ response.body().getTotalAmount());
                            mActivityPerformanceOrdersBinding.recyclerLoader.setRefreshing(false);
                        }else{
                            mActivityPerformanceOrdersBinding.recyclerLoader.setRefreshing(false);
                            Log.d("tareq_test" , ""+response.code());
                            Toast.makeText(PerformanceOrdersActivity.this, response.code()+": Server Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PerformanceOrderResponse> call, Throwable t) {
                        Log.d("tareq_test" , ""+t.getMessage());


                        t.printStackTrace();

                    }
                });
    }

    @Override
    public void onOrderClick(Order order) {
        Intent intent=new Intent(PerformanceOrdersActivity.this,PerformanceOrdersProductsActivity.class);
     //   Toast.makeText(this, ""+order.getProducts().get(0).getName(), Toast.LENGTH_SHORT).show();
        List<Product> mlist=order.getProducts();
        intent.putExtra("products", (Serializable) mlist);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
