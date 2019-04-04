/*
 * Created by Tareq Islam on 4/3/19 2:35 PM
 *
 *  Last modified 4/3/19 12:32 PM
 */

/*
 * Created by Tareq Islam on 3/28/19 9:43 AM
 *
 *  Last modified 3/28/19 9:43 AM
 */

package com.humaclab.lalteer.performance.payments;

import android.app.DatePickerDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;


import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.databinding.ActivityPerformancePaymentsBinding;
import com.humaclab.lalteer.model.performance.paymentsModel.PaymentsResponse;
import com.humaclab.lalteer.utils.CurrentTimeUtilityClass;
import com.humaclab.lalteer.utils.SessionManager;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerformancePaymentsActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private SelliscopeApiEndpointInterface apiService;
    private ActivityPerformancePaymentsBinding mBinding;
    private static String startDate="";
    private static String endDate="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      mBinding= DataBindingUtil.setContentView(this, R.layout.activity_performance_payments);

        sessionManager = new SessionManager(this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), true).create(SelliscopeApiEndpointInterface.class);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Payments");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


       mBinding.ordersRecycler.setLayoutManager(new LinearLayoutManager(this));

        startDate= CurrentTimeUtilityClass.getCurrentTimeStampDate();
        endDate=CurrentTimeUtilityClass.getCurrentTimeStampDate();

        mBinding.recyclerLoader.setRefreshing(true);
       // mBinding.recyclerLoader.setEnabled(false);
        mBinding.recyclerLoader.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                performanceOrderAction(startDate,endDate); //for Initial value in recyclerview get the data of today

            }
        });

        performanceOrderAction(startDate,endDate);

        mBinding.startDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                final int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog= new DatePickerDialog(PerformancePaymentsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mBinding.startDateText.setText(year + "-"+ (month+1) + "-"+dayOfMonth);
                        startDate=mBinding.startDateText.getText().toString();
                    }
                },year,month,day);
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        mBinding.endDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog= new DatePickerDialog(PerformancePaymentsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mBinding.endDateText.setText(year + "-"+ (month+1) + "-"+dayOfMonth);
                        endDate=mBinding.endDateText.getText().toString();

                    }
                },year,month,day);
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        mBinding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mBinding.recyclerLoader.setRefreshing(true);

                performanceOrderAction(mBinding.startDateText.getText().toString(), mBinding.endDateText.getText().toString());

            }



        });

    }

    private void performanceOrderAction(String date_from, String date_to) {
        apiService.performancePayments(date_from  , date_to)
                .enqueue(new Callback<PaymentsResponse>() {
                    @Override
                    public void onResponse(Call<PaymentsResponse> call, Response<PaymentsResponse> response) {

                        if(response.code()==200) {
                            Log.d("" + getClass().getName(), "" + response.code());
                           mBinding.ordersRecycler.setAdapter(new PerformancePaymentsAdapter(PerformancePaymentsActivity.this, response.body().getResult().getData()));
                            mBinding.totalAmountTextView.setText("Total: "+ response.body().getGrandAmount());
                            mBinding.recyclerLoader.setRefreshing(false);
                        }else{
                            mBinding.recyclerLoader.setRefreshing(false);
                            Toast.makeText(PerformancePaymentsActivity.this, response.code()+": Server Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PaymentsResponse> call, Throwable t) {
                        Log.d("" +getClass().getName(), ""+t.getMessage());
                        mBinding.recyclerLoader.setRefreshing(false);
                        t.printStackTrace();

                    }
                });
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
