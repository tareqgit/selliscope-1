package com.humaclab.selliscope.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

import com.humaclab.selliscope.Utils.SendUserLocationData;
import com.humaclab.selliscope.adapters.LocationSyncAdapter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class SendLocationDataService extends Service {
    private static final Object sSyncAdapterLock = new Object();

    private static volatile PowerManager.WakeLock wakeLock;
    private static LocationSyncAdapter locationSyncAdapter = null;
    private ScheduledExecutorService scheduler;

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
        /* Wake up */
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);

			/* we don't need the screen on */
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "locationtracker");
            wakeLock.setReferenceCounted(true);
        }

        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
        }

        synchronized (sSyncAdapterLock) {
            if (locationSyncAdapter == null) {
                locationSyncAdapter = new LocationSyncAdapter(getApplicationContext(), true);
            }
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        SendUserLocationData sendUserLocationData = new SendUserLocationData(getApplicationContext());
                        sendUserLocationData.getLocation();
                    }
                }, 0, 15, TimeUnit.MINUTES);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Timber.d("SendLocation service is stopped.");
        startService(new Intent(SendLocationDataService.this, SendLocationDataService.class));
        sendBroadcast(new Intent(getApplicationContext(), SendLocationDataService.class));
    }
}
