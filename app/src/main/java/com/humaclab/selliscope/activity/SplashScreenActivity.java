package com.humaclab.selliscope.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.humaclab.selliscope.utils.ReloadDataService;
import com.humaclab.selliscope.utils.SessionManager;

import java.util.Objects;

public class SplashScreenActivity extends AppCompatActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {

            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("FCM", "Key: " + key + " Value: " + value);
            }

            if(getIntent().getExtras().get("action")!=null){
                if(Objects.requireNonNull(getIntent().getExtras().get("action")).toString().equals("actionReload")){
                    new ReloadDataService(this).reloadData(new ReloadDataService.ReloadDataListener() {
                        @Override
                        public void onComplete() {
                            Toast.makeText(SplashScreenActivity.this, "Reload data Complete", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailed(String reason) {
                            Toast.makeText(SplashScreenActivity.this, "Reload data Failed; "+ reason , Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        }

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        finish();
    }
}
