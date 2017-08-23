package com.humaclab.selliscope.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class SendLocationDataService extends Service {
    ScheduledExecutorService scheduler;

    public SendLocationDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.i("Send Location Data Service onStartCommand");
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        startService(new Intent(SendLocationDataService.this,
                                UserTrackingService.class));
                    }
                }, 0, 1, TimeUnit.MINUTES);
        return super.onStartCommand(intent, flags, startId);
    }
}
