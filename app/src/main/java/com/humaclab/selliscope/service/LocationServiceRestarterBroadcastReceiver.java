package com.humaclab.selliscope.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.humaclab.selliscope.LocationMonitoringService;

/***
 * Created by mtita on 24,April,2019.
 */
public class LocationServiceRestarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("tareq_test" , "Service Stoooop......");
        try {
            context.startService(new Intent(context,LocationMonitoringService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
