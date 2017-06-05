package com.humaclab.selliscope;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.humaclab.selliscope.databinding.ActivityCallCardBinding;
import com.humaclab.selliscope.model.Outlets;

public class CallCardActivity extends AppCompatActivity {
    private ActivityCallCardBinding binding;
    private Outlets.Successful.Outlet outlet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_card);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Call Card");
        setSupportActionBar(toolbar);

        outlet = (Outlets.Successful.Outlet) getIntent().getSerializableExtra("outletDetails");

        binding.cvProductPitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CallCardActivity.this, ProductActivity.class);
                startActivity(intent);
            }
        });
        binding.cvOrderCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CallCardActivity.this, OrderActivity.class);
                intent.putExtra("outletName", outlet.outletName);
                intent.putExtra("outletID", outlet.outletId);
                startActivity(intent);
            }
        });
    }
}