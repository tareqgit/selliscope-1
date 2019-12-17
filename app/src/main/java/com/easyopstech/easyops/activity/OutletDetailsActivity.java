package com.easyopstech.easyops.activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.easyopstech.easyops.RootApiEndpointInterface;
import com.easyopstech.easyops.RootApplication;
import com.google.gson.Gson;
import com.easyopstech.easyops.R;
import com.easyopstech.easyops.databinding.ActivityOutletDetailsBinding;
import com.easyopstech.easyops.model.Outlets;
import com.easyopstech.easyops.model.target.OutletTarget;
import com.easyopstech.easyops.utils.LoadLocalIntoBackground;
import com.easyopstech.easyops.utils.NetworkUtility;
import com.easyopstech.easyops.utils.SessionManager;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutletDetailsActivity extends AppCompatActivity {
    private RootApiEndpointInterface apiService;
    private ActivityOutletDetailsBinding binding;
    private Outlets.Outlet outlet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_outlet_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getString(R.string.outlet_information));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        outlet = (Outlets.Outlet) getIntent().getSerializableExtra("outletDetails");
        binding.setVariable(com.easyopstech.easyops.BR.outletDetails, outlet);
        loadTargetOutlet();

        if (Float.parseFloat(outlet.outletDue.replace(",", "")) < 0) {
            binding.tvDueAmount.setVisibility(View.GONE);
        }

        Glide.with(getApplicationContext()).load(outlet.outletImgUrl)
                .thumbnail(0.5f)
                .into(binding.ivAddOutletImage);

        binding.btnEditOutlet.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EditOutletActivity.class);
            intent.putExtra("outletDetails", outlet);
            startActivity(intent);
        });
        binding.btnCallCard.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CallCardActivity.class);
            intent.putExtra("outletDetails", outlet);
            startActivity(intent);
        });

        //if network is Available then update the data again
        if (NetworkUtility.isNetworkAvailable(OutletDetailsActivity.this)) {
            LoadLocalIntoBackground loadLocalIntoBackground = new LoadLocalIntoBackground(OutletDetailsActivity.this);
            loadLocalIntoBackground.loadThana(null  );
            loadLocalIntoBackground.loadOutletType(null);
            loadLocalIntoBackground.loadDistrict(null);
        }

    }

    private void loadTargetOutlet(){
        SessionManager sessionManager = new SessionManager(OutletDetailsActivity.this);
        apiService = RootApplication.getRetrofitInstance(sessionManager.getUserEmail(),sessionManager.getUserPassword(),false).create(RootApiEndpointInterface.class);
        Call<OutletTarget> call = apiService.getPutletTarget(outlet.outletId);
        call.enqueue(new Callback<OutletTarget>() {
            @Override
            public void onResponse(Call<OutletTarget> call, Response<OutletTarget> response) {
                if(response.code() == 200) {
                    binding.progressBar.setVisibility(View.GONE);
                    System.out.println("Response " + new Gson().toJson(response.body()));

                    String sales_types = response.body().getResult().getSalesTypes();
                    Double total = Double.valueOf(response.body().getResult().getSalesTarget().replace(",",""));
                    Double achieved = Double.valueOf(response.body().getResult().getAchieved().replace(",",""));
                    Double remaining = total-achieved;
                    int completePersentage = (int) ((achieved * 100)/total);

                binding.tvTargetLabel.setText(response.body().getResult().getTargetType());
                binding.tvTargetAchieved.setText(response.body().getResult().getAchieved()+" "+sales_types);
                binding.tvTargetTotal.setText(response.body().getResult().getSalesTarget()+" "+sales_types);
                binding.tvVisited.setText(response.body().getResult().getVisited().toString());
                binding.tvTargetRemaining.setText(remaining.toString()+" "+sales_types);
                binding.circleProgressView.setTextEnabled(false);
                binding.circleProgressView.setInterpolator(new AccelerateDecelerateInterpolator());
                binding.circleProgressView.setStartAngle(10);
                binding.circleProgressView.setProgressWithAnimation(completePersentage,2000);


                }else if (response.code() == 401) {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(OutletDetailsActivity.this,
                            "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(OutletDetailsActivity.this,
                            "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OutletTarget> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
