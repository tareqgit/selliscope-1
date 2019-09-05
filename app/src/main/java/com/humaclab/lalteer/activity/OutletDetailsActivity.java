package com.humaclab.lalteer.activity;

import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import com.google.gson.Gson;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.adapters.AdvancePaymentAdapter;
import com.humaclab.lalteer.databinding.ActivityOutletDetailsBinding;
import com.humaclab.lalteer.model.Outlets;
import com.humaclab.lalteer.model.Target.OutletTarget;
import com.humaclab.lalteer.model.advance_payment.AdvancePaymentsItem;
import com.humaclab.lalteer.model.advance_payment.AdvancedPaymentResponse;
import com.humaclab.lalteer.utils.Constants;
import com.humaclab.lalteer.utils.MyDialog;
import com.humaclab.lalteer.utils.SessionManager;
import com.mti.pushdown_ext_onclick_single.PushDownAnim;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutletDetailsActivity extends AppCompatActivity {
    private SelliscopeApiEndpointInterface apiService;
    protected ActivityOutletDetailsBinding binding;
    private Outlets.Outlet outlet;
    private List<AdvancePaymentsItem> mAdvancePaymentResponseList = new ArrayList<>();
    AdvancePaymentAdapter mAdvancePaymentAdapter;
    DialogFragment mDialogFragment;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_outlet_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(getString(R.string.Dealer_Information));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SessionManager sessionManager = new SessionManager(OutletDetailsActivity.this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);


        outlet = (Outlets.Outlet) getIntent().getSerializableExtra("outletDetails");
        Log.d("tareq_test", "ou" + new Gson().toJson(outlet));
        binding.setOutletDetailsVar(outlet); //setting binding variable

        if (Float.parseFloat(outlet.outletDue.replace(",", "")) < 0) {
            binding.tvDueAmount.setVisibility(View.GONE);
        }

        Glide.with(getApplicationContext())
                .load(Constants.BASE_URL.substring(0, Constants.BASE_URL.length()-4) + outlet.outletImgUrl)
                .thumbnail(0.5f)
                .placeholder(R.drawable.ic_map)
                .centerCrop()
                .transform(new CircleCrop())
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


        binding.recyclerPayments.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        mAdvancePaymentAdapter = new AdvancePaymentAdapter(this, mAdvancePaymentResponseList);

        binding.recyclerPayments.setAdapter(mAdvancePaymentAdapter);


        loadAdvanceMoney();
        loadTargetOutlet();

        mDialogFragment = MyDialog.newInstance(outlet.outletId, apiService);
        ((MyDialog) mDialogFragment).setOnDismissListener(() -> {
            loadAdvanceMoney();
        });

        PushDownAnim.setPushDownAnimTo(binding.addAdvanceMoney).setOnSingleClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); //for onclick vibration
           /*AlertDialog.Builder  builder=new AlertDialog.Builder(OutletDetailsActivity.this);
           View layout=getLayoutInflater().inflate(R.layout.cus_dialog,null);
           builder.setView(layout);
           AlertDialog dialog = builder.create();
           dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
           dialog.show();*/
            loadDialog();


        });


    }

    private void loadDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.alert_present, R.anim.alert_dismiss);
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        mDialogFragment.show(ft, "dialog");

    }

    private void loadTargetOutlet() {
   apiService.getOutletTarget(outlet.outletId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
           .subscribe(new SingleObserver<Response<OutletTarget>>() {
               @Override
               public void onSubscribe(Disposable d) {
                   if(mCompositeDisposable!=null) mCompositeDisposable.add(d);
               }

               @Override
               public void onSuccess(Response<OutletTarget> response) {
                   if (response.code() == 200) {
                       binding.progressBar.setVisibility(View.GONE);
                       Log.d("tareq_test", "outlet" + new Gson().toJson(response.body()));

                       String sales_types = response.body().getResult().getSalesTypes();
                       Double total = Double.valueOf(response.body().getResult().getSalesTarget().replace(",", ""));
                       Double achieved = Double.valueOf(response.body().getResult().getAchieved().replace(",", ""));
                       Double remaining = total - achieved;
                       int completePersentage = (int) ((achieved * 100) / total);

                       binding.tvTargetLabel.setText(response.body().getResult().getTargetType());
                       binding.tvTargetAchieved.setText(response.body().getResult().getAchieved() + " " + sales_types);
                       binding.tvTargetTotal.setText(response.body().getResult().getSalesTarget() + " " + sales_types);
                       binding.tvVisited.setText(response.body().getResult().getVisited().toString());
                       binding.tvTargetRemaining.setText(remaining.toString() + " " + sales_types);
                       binding.circleProgressView.setTextEnabled(false);
                       binding.circleProgressView.setInterpolator(new AccelerateDecelerateInterpolator());
                       binding.circleProgressView.setStartAngle(10);
                       binding.circleProgressView.setProgressWithAnimation(completePersentage, 2000);


                   } else if (response.code() == 401) {
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
               public void onError(Throwable e) {
                   binding.progressBar.setVisibility(View.GONE);
               }
           });

    }


    void loadAdvanceMoney() {
        binding.progressBar.setVisibility(View.VISIBLE);
        apiService.getAdvancePayments(outlet.outletId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<AdvancedPaymentResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if(mCompositeDisposable!=null) mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<AdvancedPaymentResponse> response) {
                        binding.progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            Log.d("tareq_test", "" + new Gson().toJson(response.body().getAdvancePayments()));
                            assert response.body() != null;
                            mAdvancePaymentResponseList.clear();
                            mAdvancePaymentResponseList.addAll(response.body().getAdvancePayments());
                            mAdvancePaymentAdapter.notifyDataSetChanged();

                            binding.textInputLayouTotalAmount.getEditText().setText(String.valueOf(response.body().getTotalPaid()));

                        } else if (response.code() == 401) {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(OutletDetailsActivity.this,
                                    "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OutletDetailsActivity.this, "Internal server error.", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(OutletDetailsActivity.this,
                                "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    protected void onResume() {
        super.onResume();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed())
            mCompositeDisposable.dispose();
    }
}
