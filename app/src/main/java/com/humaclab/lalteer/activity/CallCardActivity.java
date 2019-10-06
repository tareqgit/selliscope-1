package com.humaclab.lalteer.activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.humaclab.lalteer.R;
import com.humaclab.lalteer.databinding.ActivityCallCardBinding;
import com.humaclab.lalteer.model.outlets.Outlets;

public class CallCardActivity extends AppCompatActivity {
    private ActivityCallCardBinding binding;
    private Outlets.Outlet outlet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_card);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText("Call Card");
        setSupportActionBar(toolbar);

        outlet = (Outlets.Outlet) getIntent().getSerializableExtra("outletDetails");

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
        /*binding.cvDueCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CallCardActivity.this, DueActivity.class);
                intent.putExtra("outletName", outlet.outletName);
                intent.putExtra("outletID", outlet.outletId);
                startActivity(intent);
            }
        });*/
        binding.cvInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CallCardActivity.this, InspectionActivity.class);
                intent.putExtra("outletName", outlet.outletName);
                intent.putExtra("outletID", outlet.outletId);
                startActivity(intent);
            }
        });
        binding.cvSellsReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CallCardActivity.this, SalesReturnActivity.class);
                intent.putExtra("outletName", outlet.outletName);
                intent.putExtra("outletID", outlet.outletId);
                startActivity(intent);
            }
        });
    }
}
