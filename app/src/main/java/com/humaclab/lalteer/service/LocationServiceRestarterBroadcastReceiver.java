/*
 * Created by Tareq Islam on 8/19/19 11:32 AM
 *
 *  Last modified 4/24/19 12:34 PM
 */

package com.humaclab.lalteer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.humaclab.lalteer.LocationMonitoringService;


/***
 * Created by mtita on 24,April,2019.
 */
public class LocationServiceRestarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("tareq_test" , "Service Stoooop......");
        try {
            context.startService(new Intent(context, LocationMonitoringService.class));
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, LocationMonitoringService.class));
            }
        }
    }
}
