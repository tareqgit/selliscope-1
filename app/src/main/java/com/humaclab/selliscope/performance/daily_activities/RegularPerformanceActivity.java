package com.humaclab.selliscope.performance.daily_activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.gson.Gson;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.databinding.ActivityRegularPerformanceBinding;
import com.humaclab.selliscope.model.performance.orders_model.Product;
import com.humaclab.selliscope.performance.orders.PerformanceOrdersActivity;
import com.humaclab.selliscope.performance.orders.PerformanceOrdersProductsActivity;
import com.humaclab.selliscope.performance.payments.PerformancePaymentsActivity;
import com.humaclab.selliscope.utility_db.db.RegularPerformanceEntity;
import com.humaclab.selliscope.utility_db.db.UtilityDatabase;
import com.humaclab.selliscope.utils.CurrentTimeUtilityClass;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegularPerformanceActivity extends AppCompatActivity implements RegularPerformanceAdapter.OnOutletItemClickListener {
    ActivityRegularPerformanceBinding mBinding;
    private static String startDate="";
    private static String endDate="";
    private UtilityDatabase mUtilityDatabase;
    private RegularPerformanceAdapter mRegularPerformanceAdapter;

    private List<RegularPerformanceEntity> mDatumList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_regular_performance);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         mUtilityDatabase = (UtilityDatabase) UtilityDatabase.getInstance(getApplicationContext());

        mBinding.ordersRecycler.setLayoutManager(new LinearLayoutManager(this));

        mRegularPerformanceAdapter = new RegularPerformanceAdapter(this, mDatumList, this);
        mBinding.ordersRecycler.setAdapter(mRegularPerformanceAdapter);


        startDate= CurrentTimeUtilityClass.getCurrentTimeStampDateLocale();

        Log.d("tareq_test" , "Hey Date: " + startDate);
        endDate=CurrentTimeUtilityClass.getCurrentTimeStampDateLocale();

        mBinding.recyclerLoader.setRefreshing(true);

        mBinding.recyclerLoader.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDailyPerformances(startDate,endDate); //for Initial value in recyclerview get the data of today

            }
        });

        getDailyPerformances(startDate,endDate);

        mBinding.startDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                final int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog= new DatePickerDialog(RegularPerformanceActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        DecimalFormat fmt = new DecimalFormat("00");
                        mBinding.startDateText.setText(String.format(Locale.ENGLISH,"%d-%2s-%2s", year, fmt.format(month + 1), fmt.format(dayOfMonth)));
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
                DatePickerDialog datePickerDialog= new DatePickerDialog(RegularPerformanceActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        DecimalFormat fmt = new DecimalFormat("00");
                        mBinding.endDateText.setText(String.format(Locale.ENGLISH, "%d-%2s-%2s", year, fmt.format(month + 1), fmt.format(dayOfMonth)));
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

             getDailyPerformances(startDate, endDate);

            }



        });

    }

    private MyDate convertDateToMyDate(String date){


        int startDay,startMonth,startYear=0;

        startDay=Integer.parseInt(date.substring(8,10));


        startMonth=Integer.parseInt(date.substring(5,7));


        startYear=Integer.parseInt(date.substring(0,4));

        return new MyDate(startDay,startMonth,startYear);
    }

    private void getDailyPerformances(String startDate, String endDate) {

        MyDate myStartDate=convertDateToMyDate(startDate);
        MyDate myEndDate = convertDateToMyDate(endDate);



        Date d=Calendar.getInstance().getTime();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String date= formatDate.format(d);
        SimpleDateFormat formathour = new SimpleDateFormat("HH",Locale.ENGLISH);
        String hour= formathour.format(d);

        List<RegularPerformanceEntity> sortedItems=new ArrayList<>();
        getRegularPerformances(new DataLoadingListener() {
            @Override
            public void onLoadComplete(List<RegularPerformanceEntity> datas) {

                for (RegularPerformanceEntity regularPerformence :datas  ) {
                    MyDate regDate=convertDateToMyDate(regularPerformence.date);


               //     if((regDate.year <= myEndDate.year && regDate.year>=2019) && (regDate.year>=myStartDate.year && regDate.year<=2050)) {
                        if ((regDate.month <= myEndDate.month && regDate.month > 0) && (regDate.month >= myStartDate.month && regDate.month <= 12)) {
                            if(myEndDate.month-myStartDate.month>0) {
                                if ( regDate.day > 0 && (regDate.day >= myStartDate.day && regDate.day <= 31)) {
                                    sortedItems.add(regularPerformence);
                                }
                            }else{
                                if ((regDate.day <= myEndDate.day && regDate.day > 0) && (regDate.day >= myStartDate.day && regDate.day <= 31)) {
                                    sortedItems.add(regularPerformence);
                                }
                            }
                        }
                 //   }
                }

                Log.d("tareq_test" , "Sorted Items: "+ new Gson().toJson(sortedItems));

                mDatumList.clear();
                mDatumList.addAll(sortedItems);


                mBinding.recyclerLoader.setRefreshing(false);

                runOnUiThread(() -> {
                    mRegularPerformanceAdapter.notifyDataSetChanged();
                    updateTotalDistance();
                });

            }
        });

    }

    private void updateTotalDistance() {
        double totalDistance=0;
        for (RegularPerformanceEntity regularPerformanceEntity:  mDatumList   ) {
            totalDistance += regularPerformanceEntity.distance;
        }
        mBinding.totalAmountTextView.setText(String.format(Locale.ENGLISH,"%s : %.2fm","Total",totalDistance));
    }

    private void getRegularPerformances(DataLoadingListener callback){
        new Thread(()->{
          callback.onLoadComplete(  mUtilityDatabase.returnUtilityDao().getAllRegularPerformance());
        }).start();
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
    public void onOutletClick(RegularPerformanceEntity performanceEntity) {
        Intent intent=new Intent(RegularPerformanceActivity.this, RegularPerformanceOutlets.class);
        //   Toast.makeText(this, ""+order.getProducts().get(0).getName(), Toast.LENGTH_SHORT).show();

        intent.putExtra("outlets", (Serializable) performanceEntity);
        startActivity(intent);
    }

    public  interface DataLoadingListener{
        void onLoadComplete(List<RegularPerformanceEntity> datas);
    }


    public class MyDate{
        int day;
        int month;
        int year;

        public MyDate(int day, int month, int year) {
            this.day = day;
            this.month = month;
            this.year = year;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }
    }
}
