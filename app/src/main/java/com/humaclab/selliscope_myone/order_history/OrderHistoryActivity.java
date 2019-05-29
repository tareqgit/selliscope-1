/*
 * Created by Tareq Islam on 5/28/19 9:44 AM
 *
 *  Last modified 5/28/19 9:44 AM
 */

package com.humaclab.selliscope_myone.order_history;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope_myone.R;
import com.humaclab.selliscope_myone.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_myone.SelliscopeApplication;
import com.humaclab.selliscope_myone.activity.OrderActivity;
import com.humaclab.selliscope_myone.databinding.ActivityOrderHistoryBinding;
import com.humaclab.selliscope_myone.order_history.api.response_model.OrderHistoryResponse;
import com.humaclab.selliscope_myone.order_history.api.response_model.OrdersItem;
import com.humaclab.selliscope_myone.order_history.api.response_model.ProductsItem;
import com.humaclab.selliscope_myone.utils.CurrentTimeUtilityClass;
import com.humaclab.selliscope_myone.utils.SessionManager;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity implements OrderHistoryAdapter.OnOrderItemClickListener {

    private String outletName;
    private String outletID;
    private SessionManager sessionManager;
    private SelliscopeApiEndpointInterface apiService;

    private OrderHistoryAdapter mOrderHistoryAdapter;
    private ActivityOrderHistoryBinding mBinding;

    private Context mContext;

    private static String startDate="";
    private static String endDate="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_order_history);
        //Get String  Extra from prev intent
        outletName = getIntent().getStringExtra("outletName");
        outletID = getIntent().getStringExtra("outletID");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(outletName+ " - Order History");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mContext = this;

        startDate= CurrentTimeUtilityClass.getCurrentTimeStampDate();
        endDate=CurrentTimeUtilityClass.getCurrentTimeStampDate();



        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(this));

        sessionManager = new SessionManager(this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);






        mBinding.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), OrderActivity.class);
                intent.putExtra("outletName", outletName);
                intent.putExtra("outletID", outletID);
                v.getContext().startActivity(intent);
            }

        });


        mBinding.recyclerLoader.setRefreshing(true);
        mBinding.recyclerLoader.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrders( startDate,endDate,mBinding.tvSearchOutlet.getText().toString()); //for Initial value in recyclerview get the data of today

            }
        });

      getOrders(startDate,endDate,""); //for Initial value in recyclerview get the data of today



        mBinding.startDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                final int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog= new DatePickerDialog(OrderHistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                DatePickerDialog datePickerDialog= new DatePickerDialog(OrderHistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
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




                getOrders(mBinding.startDateText.getText().toString(), mBinding.endDateText.getText().toString(),mBinding.tvSearchOutlet.getText().toString());

            }



        });

        mBinding.tvSearchOutlet.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput();
                return true;
            } else {
                return false;
            }
        });

        mBinding.tvSearchOutlet.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput();
                return true;
            } else {
                return false;
            }
        });




    }

    private void updateRepoListFromInput() {
        String queryEntered = mBinding.tvSearchOutlet.getText().toString().trim();
        if (!TextUtils.isEmpty(queryEntered)) {
            mBinding.recyclerview.scrollToPosition(0);
            //Posts the query to be searched
            getOrders(mBinding.startDateText.getText().toString(), mBinding.endDateText.getText().toString(),queryEntered.toString());
        }
    }

    private void getOrders(String date_from, String date_to,String query) {
        mBinding.recyclerLoader.setRefreshing(true);
        Log.d("tareq_test", "" + outletID);
        apiService.getOrderHistory(outletID, date_from,date_to,query)
                .enqueue(new Callback<OrderHistoryResponse>() {
                    @Override
                    public void onResponse(Call<OrderHistoryResponse> call, Response<OrderHistoryResponse> response) {
                        if(response.code()==200) {
                            if(response.body().getResult().getOrders().size()==0)
                                Toast.makeText(mContext, "No Data Found.... \nPlease set the date range ", Toast.LENGTH_SHORT).show();

                            mOrderHistoryAdapter = new OrderHistoryAdapter(mContext, (List<OrdersItem>) response.body().getResult().getOrders(), OrderHistoryActivity.this::onOrderClick);
                            mBinding.recyclerview.setAdapter(mOrderHistoryAdapter);
                            mBinding.recyclerLoader.setRefreshing(false);
                        }else{
                            mBinding.recyclerLoader.setRefreshing(false);
                            Log.d("tareq_test" , ""+response.code());
                            Toast.makeText(OrderHistoryActivity.this, response.code()+": Server Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderHistoryResponse> call, Throwable t) {

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

    @Override
    public void onOrderClick(OrdersItem order) {
        Intent intent = new Intent(this, OrderHistoryProductActivity.class);
        //   Toast.makeText(this, ""+order.getProducts().get(0).getName(), Toast.LENGTH_SHORT).show();
        List<ProductsItem> mlist = order.getProducts();
        intent.putExtra("products", (Serializable) mlist);
        startActivity(intent);
    }
}
