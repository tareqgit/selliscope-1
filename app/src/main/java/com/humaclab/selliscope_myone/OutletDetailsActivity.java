package com.humaclab.selliscope_myone;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.humaclab.selliscope_myone.databinding.ActivityOutletDetailsBinding;
import com.humaclab.selliscope_myone.model.Outlets;

public class OutletDetailsActivity extends AppCompatActivity {
    private ActivityOutletDetailsBinding binding;
    private Outlets.Successful.Outlet outlet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_outlet_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Outlet Information");
        setSupportActionBar(toolbar);

        outlet = (Outlets.Successful.Outlet) getIntent().getSerializableExtra("outletDetails");
        binding.setVariable(com.humaclab.selliscope_myone.BR.outletDetails, outlet);

        if (Float.parseFloat(outlet.outletDue.replace(",", "")) < 0) {
            binding.tvDueAmount.setVisibility(View.GONE);
        }

        Glide.with(getApplicationContext()).load(outlet.outletImgUrl)
                .thumbnail(0.5f)
                .crossFade()
                .placeholder(R.drawable.ic_outlet_bnw)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivAddOutletImage);

        binding.btnEditOutlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditOutletActivity.class);
                intent.putExtra("outletDetails", outlet);
                startActivity(intent);
            }
        });
        binding.btnCallCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CallCardActivity.class);
                intent.putExtra("outletDetails", outlet);
                startActivity(intent);
            }
        });
    }
}
