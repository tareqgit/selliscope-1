/*
 * Created by Tareq Islam on 9/30/19 2:44 PM
 *
 *  Last modified 9/30/19 2:44 PM
 */

package com.humaclab.lalteer.performance.claim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.humaclab.lalteer.R;
import com.humaclab.lalteer.activity.OutletActivity;
import com.humaclab.lalteer.databinding.ActivityClaimBinding;
import com.humaclab.lalteer.performance.claim.model.Claim;
import com.humaclab.lalteer.performance.claim.model.ReasonItem;
import com.humaclab.lalteer.performance.claim.repository.ClaimRepository;
import com.humaclab.lalteer.performance.claim.ui.ClaimViewModel;
import com.humaclab.lalteer.utils.CurrentTimeUtilityClass;
import com.humaclab.lalteer.utils.DateDiffUtils;

import java.text.ParseException;
import java.util.List;

public class ClaimActivity extends AppCompatActivity {


    ActivityClaimBinding mBinding;
    private ClaimViewModel mClaimViewModel;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_claim);

        mContext = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarTitle.setText("Claim");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //initialize ViewModel
        mClaimViewModel = ViewModelProviders.of(this).get(ClaimViewModel.class);

        mClaimViewModel.getClaimResponseLiveData().observe(this, new Observer<List<ReasonItem>>() {
            @Override
            public void onChanged(List<ReasonItem> reasonItems) {
                mBinding.radioGroup.removeAllViews();

                for (ReasonItem reasonItem : reasonItems) {
                    AppCompatRadioButton radioButton = new AppCompatRadioButton(mContext);
                    radioButton.setId(reasonItem.getId());
                    radioButton.setText(reasonItem.getName());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                    radioButton.setLayoutParams(params);
                    mBinding.radioGroup.addView(radioButton);

                }
                AppCompatRadioButton radioButton = new AppCompatRadioButton(mContext);
                radioButton.setId(0);
                radioButton.setText("Others");
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                radioButton.setLayoutParams(params);
                mBinding.radioGroup.addView(radioButton);

            }
        });

        mBinding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == 0) {
                mBinding.opinionTx.setVisibility(View.VISIBLE);
            } else {
                mBinding.opinionTx.setVisibility(View.GONE);
            }
        });

        mBinding.sendTxt.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("SelliscopePref", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            String last_claim_time = prefs.getString("last_claim_time", "2018-01-01");
            try {
                if (DateDiffUtils.getDifferenceDays(last_claim_time, CurrentTimeUtilityClass.getCurrentTimeStampDate(), "yyyy-MM-dd") > 0) {
                    Intent intent = new Intent(ClaimActivity.this, OutletActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if (mBinding.radioGroup.getCheckedRadioButtonId() == 0) {
                        mClaimViewModel.sendClaim(
                                new Claim(mBinding.opinionTx.getEditText().getText().toString(), CurrentTimeUtilityClass.getCurrentTimeStampDate(), 0), () -> {

                                    editor.putString("last_claim_time", CurrentTimeUtilityClass.getCurrentTimeStamp());
                                    editor.apply();
                                    finish();

                                });
                    } else {
                        mClaimViewModel.sendClaim(
                                new Claim("", CurrentTimeUtilityClass.getCurrentTimeStampDate(), mBinding.radioGroup.getCheckedRadioButtonId()), () -> {

                                    editor.putString("last_claim_time", CurrentTimeUtilityClass.getCurrentTimeStamp());
                                    editor.apply();

                                    mContext.startActivity(intent);
                                    finish();
                                });
                    }
                } else {
                    Toast.makeText(mContext, "You have already claimed Today", Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
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
