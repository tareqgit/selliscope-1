package com.sokrio.sokrio_classic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
import com.google.gson.Gson;
import com.sokrio.sokrio_classic.activity.HomeActivity;
import com.sokrio.sokrio_classic.dbmodel.UserVisit;
import com.sokrio.sokrio_classic.model.UserLocation;
import com.sokrio.sokrio_classic.service.LocationServiceRestarterBroadcastReceiver;
import com.sokrio.sokrio_classic.utility_db.model.RegularPerformanceEntity;
import com.sokrio.sokrio_classic.utility_db.db.UtilityDatabase;
import com.sokrio.sokrio_classic.utils.BatteryUtils;
import com.sokrio.sokrio_classic.utils.CurrentTimeUtilityClass;
import com.sokrio.sokrio_classic.utils.DatabaseHandler;
import com.sokrio.sokrio_classic.utils.GetAddressFromLatLang;
import com.sokrio.sokrio_classic.utils.NetworkUtility;
import com.sokrio.sokrio_classic.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sokrio.sokrio_classic.SelliscopeApplication.CHANNEL_ID;

/**
 * Created by anam on 27-09-2018.
 */

public class LocationMonitoringService extends Service {

    private static final String TAG = LocationMonitoringService.class.getSimpleName();

    private SessionManager sessionManager;
    // public static final String ACTION_LOCATION_BROADCAST = LocationMonitoringService.class.getName() + "LocationBroadcast";
   // public static final String EXTRA_LATITUDE = "extra_latitude";
   // public static final String EXTRA_LONGITUDE = "extra_longitude";
    SharedPreferences prefs;

    public static MediaPlayer sMediaPlayerService;
    public static String lastTimeMediaPlayed = "";

    LocationCallback mLocationCallback;
    private static volatile PowerManager.WakeLock wakeLock;
    private DatabaseHandler mDbHandler;
    private LocationRequest mMLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    public LocationMonitoringService() {
    }

    public Timer myTimer;

    public static Location sLocation;

    Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        mDbHandler = new DatabaseHandler(this);

        sessionManager = new SessionManager(this);
        lastTimeMediaPlayed = Calendar.getInstance().getTime().toString(); //for Media player concurrency playing prblme resolve

        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = null;


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Hello " + sessionManager.getUserDetails().get("userName"))
                    .setSmallIcon(R.drawable.ic_sokrio_classic)
                    .setColor(ContextCompat.getColor(this, R.color.colorDefault))
                    .setContentIntent(pendingIntent)
                    .build();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.sokrio_icon)
                    .setContentTitle("Hello " + sessionManager.getUserDetails().get("userName"))
                    .setColor(ContextCompat.getColor(this, R.color.colorDefault))
                    .setTicker("TICKER")
                    .setContentIntent(pendingIntent);
            notification = builder.build();
        }




        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    // Update UI  and sound with location data
                    //   updateUIandSoundonAccuracyChanges(location); //turn on if you need location accuracy update
                    onLocationChanged(location);

                }
            }
        };
        requestLocationUpdates();

        prefs = getSharedPreferences("ServiceRunning", MODE_PRIVATE);
        startForeground(1, notification);
    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);

        /* Wake up */
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);

           /// we don't need the screen on
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Selliscope::MyWakeLockTag");
            wakeLock.setReferenceCounted(true);
        }

        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
        }

        //Declare the timer
        myTimer = new Timer();
        //Set the schedule function and rate
        myTimer.schedule(new TimerTask() {

                             @SuppressLint("MissingPermission")
                             @Override
                             public void run() {
                                 //Called each time when 1000 milliseconds (1 second) (the period parameter)

                                 displayLocationSettingsRequest(getApplicationContext());

                             }
                         },
//Set how long before to start calling the TimerTask (in milliseconds)
                3 * 60 * 1000,
//Set the amount of time between each execution (in milliseconds)
                60 *5 * 1000);

        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


  

    public void requestLocationUpdates(){
        mMLocationRequest = new LocationRequest();
        mMLocationRequest.setInterval(30 * 1000);
        mMLocationRequest.setFastestInterval(15 * 1000);
        mMLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.requestLocationUpdates(mMLocationRequest, mLocationCallback, null);// Looper.myLooper()
        }

    }

    private void updateUIandSoundonAccuracyChanges(Location location) {
        // Update UI  and sound with location data

        Log.d("tareq_test", "location accuracy: " + location.getAccuracy());

        //LocalBroadCastManager for sharing data from service to activity
        Intent intent = new Intent("GPS");
        intent.putExtra("accuracy", location.getAccuracy());
        intent.putExtra("obj", mMLocationRequest);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        if (CurrentTimeUtilityClass.getDiffBetween(lastTimeMediaPlayed) >= 1) {
            if (location.getAccuracy() > 60) {
                if (lastAccuracy <= 60) { //that means last time its
                    if (sMediaPlayerService != null && sMediaPlayerService.isPlaying())
                        sMediaPlayerService.stop();
                    sMediaPlayerService = MediaPlayer.create(getApplicationContext(), R.raw.selliscope_gps_signal_lost);

                    sMediaPlayerService.start();

                }
            } else {
                if (lastAccuracy > 60) {
                    if (sMediaPlayerService != null && sMediaPlayerService.isPlaying())
                        sMediaPlayerService.stop();
                    sMediaPlayerService = MediaPlayer.create(getApplicationContext(), R.raw.selliscope_gps_signal_restored);

                    sMediaPlayerService.start();

                }
            }
            lastTimeMediaPlayed = Calendar.getInstance().getTime().toString();
            lastAccuracy = location.getAccuracy();
        }
    }

    private static float lastAccuracy = 0; //this the trigger for not playing restored again and again



    UtilityDatabase utilityDatabase;
    //to get the location change


    public void onLocationChanged(Location location) {

        if (location != null && location.getAccuracy() < 350) {

            sLocation = location;

            utilityDatabase = (UtilityDatabase) UtilityDatabase.getInstance(getApplicationContext());
            Date d = Calendar.getInstance().getTime();
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            String date = formatDate.format(d);
            Log.d(TAG, "== location != null");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("tareq_test", "Accuracy: " + location.getAccuracy() + "Speed: " + location.getSpeed() + "Speed Accuracy meter: " + location.getSpeedAccuracyMetersPerSecond());
            }


            //Send result to activities
            //sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
            //Toast.makeText(this, ""+String.valueOf(location.getLatitude())+" "+String.valueOf(location.getLongitude()), Toast.LENGTH_SHORT).show();

            double latitude = Double.parseDouble(String.format(Locale.US, "%.05f", location.getLatitude()));
            double longitude = Double.parseDouble(String.format(Locale.US, "%.05f", location.getLongitude()));

            String lastTime = prefs.getString("lasttime", "");
            Log.d("tareq_test", "last Time:" + lastTime);
            if (!lastTime.equals("")) {
                //      Log.d("tareq_test", "diff " + CurrentTimeUtilityClass.getDiffbetweenTimeStamps(lastTime));
                Log.d("tareq_test", "diff " + CurrentTimeUtilityClass.getDiffBetween(lastTime));


                //  if ( CurrentTimeUtilityClass.getDiffbetweenTimeStamps(lastTime) >= 5 || (CurrentTimeUtilityClass.getDiffbetweenTimeStamps(lastTime) >= -55 && CurrentTimeUtilityClass.getDiffbetweenTimeStamps(lastTime) <0) ) {
                if (CurrentTimeUtilityClass.getDiffBetween(lastTime) >= 5) {
                    Log.d("tareq_test", "Filtered_addr_if: " + GetAddressFromLatLang.getAddressFromLatLan(getApplicationContext(), location.getLatitude(), location.getLongitude()) + " acc: " + location.getAccuracy() + " T: " + CurrentTimeUtilityClass.getCurrentTimeStamp());
                    prefs.edit().putString("times",prefs.getString("times","")+CurrentTimeUtilityClass.getCurrentTimeStamp()).commit();

                    //region Distance
                    Location prevLocation = new Location("");
                    prevLocation.setLatitude(Double.parseDouble(prefs.getString("last_pos_lat", "0")));
                    prevLocation.setLongitude(Double.parseDouble(prefs.getString("last_pos_long", "0")));


                    Trace myTrace = FirebasePerformance.getInstance().newTrace("test_trace");
                    myTrace.start();

                    // code that you want to trace
                    double dist = location.distanceTo(prevLocation);

                    if (dist < 5000) {
                        Log.d("tareq_test", "Valid distance " + dist);
                        new Thread(() -> {
                            List<RegularPerformanceEntity> regularPerformanceEntities = utilityDatabase.returnUtilityDao().getRegularPerformance(date);
                            if (regularPerformanceEntities.size() == 0) {
                                double distance;
                                String sDist = String.format(Locale.ENGLISH, "%.2f", dist);
                                Log.d("tareq_test", "distance" + sDist);
                                distance = Double.parseDouble(sDist);
                                utilityDatabase.returnUtilityDao().insertRegularPerformance(new RegularPerformanceEntity.Builder().withDate(date).withDistance(distance).withOutlets_checked_in("").build());
                            } else {
                                double distance = regularPerformanceEntities.get(0).distance;
                                distance += dist;
                                String sDist = String.format(Locale.ENGLISH, "%.2f", (distance * 1.1f));
                                Log.d("tareq_test", "distance" + sDist);
                                distance = Double.parseDouble(sDist);

                                utilityDatabase.returnUtilityDao().updateRegularPerformance(distance, date);
                            }
                        }).start();
                    } else {
                        Log.d("tareq_test", "Meters: " + dist);
                    }

                    myTrace.stop();

                    //endregion


                    if (NetworkUtility.isNetworkAvailable(getApplicationContext())) {


                        sendUserLocation(latitude, longitude, CurrentTimeUtilityClass.getCurrentTimeStamp(), -1);
                        Log.d("tareq_test", "user location send ");
                        /*                List<UserVisit> userVisits = dbHandler.getUSerVisits();
                if (!userVisits.isEmpty())
                    for (UserVisit userVisit : userVisits) {
                        sendUserLocation(
                                userVisit.getLatitude(),
                                userVisit.getLongitude(),
                                userVisit.getTimeStamp(),
                                true,
                                userVisit.getVisitId()
                        );
                    }*/
                        lastTime = Calendar.getInstance().getTime().toString();

                        prefs.edit().putString("lasttime", lastTime).apply();
                        //region Distance calculation
                        prefs.edit().putString("last_pos_lat", String.valueOf(latitude)).apply();
                        prefs.edit().putString("last_pos_long", String.valueOf(longitude)).apply();
                        //endregion
                    } else {
                        mDbHandler.addUserVisits(new UserVisit(latitude, longitude, CurrentTimeUtilityClass.getCurrentTimeStamp(), BatteryUtils.getBatteryLevelPercentage(mContext)));

                        Log.d("tareq_test", "User location saved in Database");
                        lastTime = Calendar.getInstance().getTime().toString();

                        prefs.edit().putString("lasttime", lastTime).apply();
                        //region Distance calculation
                        prefs.edit().putString("last_pos_lat", String.valueOf(latitude)).apply();
                        prefs.edit().putString("last_pos_long", String.valueOf(longitude)).apply();
                        //endregion
                    }
                }


            } else {
                // if there  is nothing to compare
                Log.d("tareq_test", "Filtered_addr_if: " + GetAddressFromLatLang.getAddressFromLatLan(getApplicationContext(), location.getLatitude(), location.getLongitude()) + " acc: " + location.getAccuracy() + " T: " + CurrentTimeUtilityClass.getCurrentTimeStamp());

                sendUserLocation(latitude, longitude, CurrentTimeUtilityClass.getCurrentTimeStamp(), -1);

                lastTime = Calendar.getInstance().getTime().toString();

                prefs.edit().putString("lasttime", lastTime).apply();
                prefs.edit().putString("last_pos_lat", String.valueOf(latitude)).apply();
                prefs.edit().putString("last_pos_long", String.valueOf(longitude)).apply();
            }

            Log.d("tareq_test", "location changed");

        }
    }

/*    private void sendMessageToUI(String lat, String lng) {

        Log.d(TAG, "Sending info...");

        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }*/



    private void sendUserLocation(double latitude, double longitude, String timeStamp, final int visitId) {
        SelliscopeApiEndpointInterface apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);
        List<UserLocation.Visit> userLocationVisits = new ArrayList<>();
        //getting data from sqlite database

        for (UserVisit userVisit : mDbHandler.getUSerVisits()) {
            userLocationVisits.add(new UserLocation.Visit(userVisit.getLatitude(), userVisit.getLongitude(), GetAddressFromLatLang.getAddressFromLatLan(getApplicationContext(), userVisit.getLatitude(), userVisit.getLongitude()), userVisit.getTimeStamp(), userVisit.getBattery_status()));
        }


        userLocationVisits.add(new UserLocation.Visit(latitude, longitude, GetAddressFromLatLang.getAddressFromLatLan(getApplicationContext(), latitude, longitude), timeStamp, BatteryUtils.getBatteryLevelPercentage(mContext)));

        Log.d("tareq_test", "" + new Gson().toJson(new UserLocation(userLocationVisits)));

        Call<ResponseBody> call = apiService.sendUserLocation(new UserLocation(userLocationVisits));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if (response.code() == 201) {


                    mDbHandler.deleteUserVisit();
                    Log.d("tareq_test", "Data send and deleted from db");

               /* } else if (response.code() == 400) {
                    Toast.makeText(getApplicationContext(), response.code() + " Can't Send user location request invalid", Toast.LENGTH_SHORT).show();*/
                } else {


                    UserVisit currentUserVisit = new UserVisit(latitude, longitude, timeStamp, BatteryUtils.getBatteryLevelPercentage(mContext));

                    mDbHandler.addUserVisits(currentUserVisit);


                    Toast.makeText(getApplicationContext(), response.code() + " Can't Send user location So stored in Dbs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Response", t.toString());
            }
        });
    }


    private void displayLocationSettingsRequest(Context context) {
      /*  GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
*/
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener((task) -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                // All location settings are satisfied. The client can initialize location
                // requests here.
                // setupLocationListener();

            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                            // Cast to a resolvable exception.
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                        } catch (ClassCastException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sessionManager.isLoggedIn()) {

            try {
                if(wakeLock.isHeld()){
                    wakeLock.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d("tareq_test", "OnDestroy Service");
            Intent bintent = new Intent(this, LocationServiceRestarterBroadcastReceiver.class);
            sendBroadcast(bintent);
            //     myTimer.cancel();
        }
    }
}