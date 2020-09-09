package com.sokrio.sokrio_classic.performance.daily_activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sokrio.sokrio_classic.R;
import com.sokrio.sokrio_classic.databinding.ActivityRegularPerformanceOutletsBinding;
import com.sokrio.sokrio_classic.performance.daily_activities.model.OutletWithCheckInTime;
import com.sokrio.sokrio_classic.utility_db.model.RegularPerformanceEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegularPerformanceOutlets extends AppCompatActivity {
    ActivityRegularPerformanceOutletsBinding mBinding;
    RegularPerformanceOutletsAdapter mRegularPerformanceOutletsAdapter;

    List<OutletWithCheckInTime> mOutletWithCheckInTimeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_regular_performance_outlets);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Outlets");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mBinding.outletRecycler.setLayoutManager(new LinearLayoutManager(this));

        RegularPerformanceEntity performanceEntity = (RegularPerformanceEntity) getIntent().getSerializableExtra("outlets");
        Log.d("tareq_test", "Entity: " + new Gson().toJson(performanceEntity));
        List<String> outlets = new ArrayList<>();
        if (performanceEntity.outlets_checked_in != null)
            outlets = Arrays.asList(performanceEntity.outlets_checked_in.split("~;~"));
        Log.d("tareq_test", "Entitities" + new Gson().toJson(outlets));

        if (outlets.size() > 0) {
            for (String outlet : outlets) {
                if (!outlet.equals("")) {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonObject object = (JsonObject) parser.parse(outlet);// response will be the json String
                    OutletWithCheckInTime outletWithCheckInTime = gson.fromJson(object, OutletWithCheckInTime.class);

                    mOutletWithCheckInTimeList.add(outletWithCheckInTime);
                }
            }
        }
        Log.d("tareq_test", "Check in time list " + mOutletWithCheckInTimeList.size());

        mRegularPerformanceOutletsAdapter = new RegularPerformanceOutletsAdapter(this, mOutletWithCheckInTimeList);
        mBinding.outletRecycler.setAdapter(mRegularPerformanceOutletsAdapter);
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
