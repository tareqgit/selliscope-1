package com.humaclab.selliscope.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.humaclab.selliscope.utils.SessionManager;

public class SplashScreenActivity extends AppCompatActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        finish();
    }
}
