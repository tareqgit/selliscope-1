package com.humaclab.selliscope.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.humaclab.selliscope.Service.SendLocationDataService;

/**
 * Created by leon on 8/23/17.
 */

public class StartBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent activityIntent = new Intent(context, SendLocationDataService.class);
            context.startService(activityIntent);
            Log.d("BroadCast Receiver", "Service start command sent.");
        }
    }
}
