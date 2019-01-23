package com.humaclab.akij_selliscope.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.humaclab.akij_selliscope.R;
import com.humaclab.akij_selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.akij_selliscope.SelliscopeApplication;
import com.humaclab.akij_selliscope.utils.SessionManager;

public class ActivityLineB extends AppCompatActivity {

    private SelliscopeApiEndpointInterface apiService;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_b);

        sessionManager = new SessionManager(this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), true).create(SelliscopeApiEndpointInterface.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Line A -Order");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



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
