package com.humaclab.selliscope.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.humaclab.selliscope.R;

import java.util.Observable;

import static com.humaclab.selliscope.LocationMonitoringService.sMediaPlayerService;

/***
 * Created by mtita on 03,July,2019.
 */
public class GpsLocationBroadcastReceiver extends BroadcastReceiver {
    String locationMode;
    public static boolean sGpsFound=false; //this the trigger for not playing restored again and again


    private static GpsLocationBroadcastReceiver mGpsLocationBroadcastReceiver;

   public static GpsLocationBroadcastReceiver getInstance(){
        if(mGpsLocationBroadcastReceiver==null)
            return new GpsLocationBroadcastReceiver();
        else
            return mGpsLocationBroadcastReceiver;
    }



    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {

            //LocalBroadCastManager for sharing data from service to activity
            Intent s_intent = new Intent("GpsState");

            ContentResolver contentResolver = context.getContentResolver();
            // Find out what the settings say about which providers are enabled
            int mode = Settings.Secure.getInt(
                    contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);

            if (mode != Settings.Secure.LOCATION_MODE_OFF) {

                if(!sGpsFound) {
                    if (sMediaPlayerService != null && sMediaPlayerService.isPlaying())
                        sMediaPlayerService.stop();
                    sMediaPlayerService = MediaPlayer.create(context, R.raw.selliscope_gps_signal_restored);
                    sMediaPlayerService.start();
                    sGpsFound=true;

                    s_intent.putExtra("state", true );
                    LocalBroadcastManager.getInstance(context).sendBroadcast(s_intent);

                }

                if (mode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
                    locationMode = "High accuracy. Uses GPS, Wi-Fi, and mobile networks to determine location";
                } else if (mode == Settings.Secure.LOCATION_MODE_SENSORS_ONLY) {
                    locationMode = "Device only. Uses GPS to determine location";
                } else if (mode == Settings.Secure.LOCATION_MODE_BATTERY_SAVING) {
                    locationMode = "Battery saving. Uses Wi-Fi and mobile networks to determine location";
                }
            } else {
                locationMode = "Gps is off";
                if(sMediaPlayerService!=null && sMediaPlayerService.isPlaying())  sMediaPlayerService.stop();
                sMediaPlayerService = MediaPlayer.create(context, R.raw.selliscope_gps_signal_lost);
                sMediaPlayerService.start();
                sGpsFound = false;
                s_intent.putExtra("state", false );
                LocalBroadcastManager.getInstance(context).sendBroadcast(s_intent);
            }
            Log.d("tareq_test", "" + locationMode);
            //Toast.makeText(context, "" + locationMode, Toast.LENGTH_SHORT).show();


        }
    }




}
