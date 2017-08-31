package com.humaclab.selliscope.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.humaclab.selliscope.Utils.SendUserLocationData;

import timber.log.Timber;

public class UserTrackingService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("User Tracking Service OnStartCommand");
        SendUserLocationData sendUserLocationData = new SendUserLocationData(getApplicationContext());
        sendUserLocationData.getLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
