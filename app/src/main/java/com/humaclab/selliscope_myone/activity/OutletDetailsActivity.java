package com.humaclab.selliscope_myone.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.android.databinding.library.baseAdapters.BR;
import com.bumptech.glide.Glide;
import com.humaclab.selliscope_myone.R;
import com.humaclab.selliscope_myone.databinding.ActivityOutletDetailsBinding;
import com.humaclab.selliscope_myone.model.Outlets;
import com.humaclab.selliscope_myone.outlet_paging.api.OutletItem;

import java.io.Serializable;

public class OutletDetailsActivity extends AppCompatActivity {
    private ActivityOutletDetailsBinding binding;
    private OutletItem outlet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_outlet_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Outlet Information");
        setSupportActionBar(toolbar);

        outlet = (OutletItem) getIntent().getSerializableExtra("outletDetails");

        binding.setVariable(BR.outletDetails, outlet);

//        if (Float.parseFloat(outlet.outletDue.replace(",", "")) < 0) {
        binding.tvDueAmount.setVisibility(View.GONE);
//        }

        //as in myone outlets doesn't have images
   /*     Glide.with(getApplicationContext()).load(outlet.outletImgUrl)
                .thumbnail(0.5f)
                .into(binding.ivAddOutletImage);*/

        binding.btnEditOutlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditOutletActivity.class);
                intent.putExtra("outletDetails", (Serializable) outlet);
                startActivity(intent);
            }
        });
        binding.btnCallCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CallCardActivity.class);
                intent.putExtra("outletDetails", (Serializable) outlet);
                startActivity(intent);
            }
        });
    }
}
