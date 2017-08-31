package com.humaclab.selliscope.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.humaclab.selliscope.Service.SendLocationDataService;

import timber.log.Timber;

/**
 * Created by leon on 8/23/17.
 */

public class StartBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent activityIntent = new Intent(context, SendLocationDataService.class);
            context.startService(activityIntent);
            Timber.d("BroadCast Receiver: Service start command sent.");
        }
    }
}
