package com.humaclab.selliscope_dimension_bd.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.humaclab.selliscope_dimension_bd.utils.SessionManager;

/**
 * Created by leon on 8/28/17.
 */

public class FcmInstanceIdServices extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Firebase token", "Refreshed token: " + refreshedToken);
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        sessionManager.setFcmToken(refreshedToken);
    }
}
