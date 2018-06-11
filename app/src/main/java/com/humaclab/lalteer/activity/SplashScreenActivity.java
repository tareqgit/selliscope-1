package com.humaclab.lalteer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.humaclab.lalteer.utils.SessionManager;

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
