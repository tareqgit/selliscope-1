package com.humaclab.selliscope.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.humaclab.selliscope.Utils.SendUserLocationData;
import com.humaclab.selliscope.adapters.LocationSyncAdapter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class SendLocationDataService extends Service {
    private static final Object sSyncAdapterLock = new Object();

    private static LocationSyncAdapter locationSyncAdapter = null;
    private ScheduledExecutorService scheduler;

    public SendLocationDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return locationSyncAdapter.getSyncAdapterBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.i("Send Location Data Service onStartCommand");
        synchronized (sSyncAdapterLock) {
            if (locationSyncAdapter == null) {
                locationSyncAdapter = new LocationSyncAdapter(getApplicationContext(), true);
            }
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        /*startService(new Intent(SendLocationDataService.this,
                                UserTrackingService.class));*/
                        SendUserLocationData sendUserLocationData = new SendUserLocationData(getApplicationContext());
                        sendUserLocationData.getLocation();
                    }
                }, 0, 1, TimeUnit.MINUTES);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Timber.d("SendLocation service is stopped.");
        sendBroadcast(new Intent(getApplicationContext(), SendLocationDataService.class));
    }
}
