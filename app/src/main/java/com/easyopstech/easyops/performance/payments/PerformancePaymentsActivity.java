/*
 * Created by Tareq Islam on 3/28/19 9:43 AM
 *
 *  Last modified 3/28/19 9:43 AM
 */

package com.easyopstech.easyops.performance.payments;

import android.app.DatePickerDialog;
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

import com.easyopstech.easyops.R;
import com.easyopstech.easyops.RootApplication;
import com.easyopstech.easyops.RootApiEndpointInterface;
import com.easyopstech.easyops.databinding.ActivityPerformancePaymentsBinding;
import com.easyopstech.easyops.model.performance.payments_model.PaymentsResponse;
import com.easyopstech.easyops.utils.CurrentTimeUtilityClass;
import com.easyopstech.easyops.utils.SessionManager;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerformancePaymentsActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private RootApiEndpointInterface apiService;
    private ActivityPerformancePaymentsBinding mBinding;
    private static String startDate="";
    private static String endDate="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      mBinding= DataBindingUtil.setContentView(this,R.layout.activity_performance_payments);

        sessionManager = new SessionManager(this);
        apiService = RootApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), true).create(RootApiEndpointInterface.class);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getString(R.string.payment));
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
                            mBinding.totalAmountTextView.setText(getString(R.string.total)+ response.body().getGrandAmount());
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
