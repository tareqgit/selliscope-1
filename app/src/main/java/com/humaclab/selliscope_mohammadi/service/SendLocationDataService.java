package com.humaclab.selliscope_mohammadi.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import com.humaclab.selliscope_mohammadi.utils.SendUserLocationData;
import com.humaclab.selliscope_mohammadi.utils.SessionManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class SendLocationDataService extends Service {
    private static volatile PowerManager.WakeLock wakeLock;
    private ScheduledExecutorService scheduler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressLint("InvalidWakeLockTag")
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

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                SendUserLocationData sendUserLocationData = new SendUserLocationData(getApplicationContext());
                sendUserLocationData.getLocation();
            }
        }, 0, 900000, TimeUnit.MILLISECONDS);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        Timber.d("SendLocation service is stopped.");
        if (sessionManager.isLoggedIn()) {
            try {
                startService(new Intent(SendLocationDataService.this, SendLocationDataService.class));
            } catch (Exception e) {
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
