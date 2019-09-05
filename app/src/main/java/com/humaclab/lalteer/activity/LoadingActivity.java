/*
 * Created by Tareq Islam on 9/4/19 3:38 PM
 *
 *  Last modified 9/4/19 3:38 PM
 */

package com.humaclab.lalteer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.utils.LoadLocalIntoBackground;
import com.humaclab.lalteer.utils.SessionManager;

public class LoadingActivity extends AppCompatActivity {
    TextView relaod;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        SessionManager sessionManager = new SessionManager(this);
        SelliscopeApiEndpointInterface apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);

        LoadLocalIntoBackground loadLocalIntoBackground = new LoadLocalIntoBackground(getApplicationContext());
        relaod = (TextView) findViewById(R.id.textViewReload);
        relaod.setOnClickListener(v -> {
            loadData(loadLocalIntoBackground);
            relaod.setVisibility(View.GONE);
            findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
            findViewById(R.id.textView_Loading_info).setVisibility(View.VISIBLE);
        });

        loadData(loadLocalIntoBackground);

        relaod.setVisibility(View.GONE);
        findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
        findViewById(R.id.textView_Loading_info).setVisibility(View.VISIBLE);
    }


    private void loadData(LoadLocalIntoBackground loadLocalIntoBackground) {
        AsyncTask.execute(() -> loadLocalIntoBackground.loadAll(new LoadLocalIntoBackground.LoadCompleteListener() {
            @Override
            public void onLoadComplete() {

                Log.d("tareq_test", "Data load all complete");

                Intent i = new Intent(LoadingActivity.this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(i);
                finish();

            }

            @Override
            public void onLoadFailed(String msg) {
                Log.d("tareq_test", "Data load all failed: " + msg);
                Toast.makeText(LoadingActivity.this, "Loading Failed: " + msg, Toast.LENGTH_SHORT).show();
                findViewById(R.id.progressBar2).setVisibility(View.GONE);
                relaod.setVisibility(View.VISIBLE);
                findViewById(R.id.textView_Loading_info).setVisibility(View.GONE);

            }
        }));
    }
}
