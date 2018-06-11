package com.humaclab.lalteer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.humaclab.lalteer.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private WebView privacyPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        privacyPolicy = (WebView) findViewById(R.id.wv_privacy_policy);
        privacyPolicy.getSettings().setJavaScriptEnabled(true);
        privacyPolicy.loadUrl("file:///android_asset/htmlPages/privacyPolicy.html");
    }
}
