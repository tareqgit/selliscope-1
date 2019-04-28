package com.humaclab.selliscope;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.humaclab.selliscope.activity.HomeActivity;
import com.humaclab.selliscope.dbmodel.UserVisit;
import com.humaclab.selliscope.model.UserLocation;
import com.humaclab.selliscope.service.LocationServiceRestarterBroadcastReceiver;
import com.humaclab.selliscope.utils.CurrentTimeUtilityClass;
import com.humaclab.selliscope.utils.DatabaseHandler;
import com.humaclab.selliscope.utils.GetAddressFromLatLang;
import com.humaclab.selliscope.utils.NetworkUtility;
import com.humaclab.selliscope.utils.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.humaclab.selliscope.SelliscopeApplication.CHANNEL_ID;


/**
 * Created by anam on 27-09-2018.
 */

public class LocationMonitoringService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = LocationMonitoringService.class.getSimpleName();
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();
    private DatabaseHandler dbHandler = new DatabaseHandler(this);
    private SessionManager sessionManager;
    // public static final String ACTION_LOCATION_BROADCAST = LocationMonitoringService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    SharedPreferences prefs;


    public LocationMonitoringService(Context c) {
        super();
    }

    public LocationMonitoringService() {
    }
    public    Timer myTimer;
    @Override
    public void onCreate() {
        super.onCreate();
        sessionManager = new SessionManager(this);

        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = null;


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Hello "+sessionManager.getUserDetails().get("userName"))

                    .setSmallIcon(R.drawable.ic_untitled)
                    .setColor(ContextCompat.getColor(this,R.color.colorDefault))
                    .setContentIntent(pendingIntent)
                    .build();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_untitled)
                    .setContentTitle("Hello "+ sessionManager.getUserDetails().get("userName"))
                    .setColor(ContextCompat.getColor(this,R.color.colorDefault))
                    .setTicker("TICKER")
                    .setContentIntent(pendingIntent);
            notification = builder.build();
        }

        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        prefs = getSharedPreferences("ServiceRunning", MODE_PRIVATE);
        startForeground(1, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);


        mLocationRequest.setInterval(60 * 5 * 1000);
        mLocationRequest.setFastestInterval(60 * 5 * 1000);

        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
        //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes


        mLocationRequest.setPriority(priority);
        mLocationClient.connect();

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
                    0,
//Set the amount of time between each execution (in milliseconds)
                    60  * 1000);


                //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * LOCATION CALLBACKS
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Log.d(TAG, "== Error On onConnected() Permission not granted");
            //Permission not granted by user so cancel the further execution.

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);


        Log.d(TAG, "Connected to Google API");
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }



    //to get the location change
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");


        if (location != null) {
            Log.d(TAG, "== location != null");

            //Send result to activities
            //sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
            //Toast.makeText(this, ""+String.valueOf(location.getLatitude())+" "+String.valueOf(location.getLongitude()), Toast.LENGTH_SHORT).show();

            double latitude = Double.parseDouble(String.format("%.05f", location.getLatitude()));
            double longitude = Double.parseDouble(String.format("%.05f", location.getLongitude()));

            Timber.d("Latitude: " + latitude + " Longitude: " + longitude);
            String lastTime = prefs.getString("lasttime", "");
            Log.d("tareq_test", "last Time:" + lastTime);
            if (!lastTime.equals("")) {
                //      Log.d("tareq_test", "diff " + CurrentTimeUtilityClass.getDiffbetweenTimeStamps(lastTime));
                Log.d("tareq_test", "diff " + CurrentTimeUtilityClass.getDiffBetween(lastTime));

                //  if ( CurrentTimeUtilityClass.getDiffbetweenTimeStamps(lastTime) >= 5 || (CurrentTimeUtilityClass.getDiffbetweenTimeStamps(lastTime) >= -55 && CurrentTimeUtilityClass.getDiffbetweenTimeStamps(lastTime) <0) ) {
                if (CurrentTimeUtilityClass.getDiffBetween(lastTime) >= 5) {
                    Log.d("tareq_test", "Filtered_addr_if: " + GetAddressFromLatLang.getAddressFromLatLan(getApplicationContext(), location.getLatitude(), location.getLongitude()) + " acc: " + location.getAccuracy() + " T: " + CurrentTimeUtilityClass.getCurrentTimeStamp());


                    if (NetworkUtility.isNetworkAvailable(getApplicationContext())) {

                        sendUserLocation(latitude, longitude, CurrentTimeUtilityClass.getCurrentTimeStamp(), false, -1);
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
                    } else {
                        dbHandler.addUserVisits(new UserVisit(latitude, longitude, CurrentTimeUtilityClass.getCurrentTimeStamp()));

                        Log.d("tareq_test", "User location saved in Database");
                        lastTime = Calendar.getInstance().getTime().toString();

                        prefs.edit().putString("lasttime", lastTime).apply();
                    }
                }


            } else {
                // if there  is nothing to compare
                Log.d("tareq_test", "Filtered_addr_if: " + GetAddressFromLatLang.getAddressFromLatLan(getApplicationContext(), location.getLatitude(), location.getLongitude()) + " acc: " + location.getAccuracy() + " T: " + CurrentTimeUtilityClass.getCurrentTimeStamp());

                sendUserLocation(latitude, longitude, CurrentTimeUtilityClass.getCurrentTimeStamp(), false, -1);

                lastTime = Calendar.getInstance().getTime().toString();

                prefs.edit().putString("lasttime", lastTime).apply();

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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect to Google API");

    }


    private void sendUserLocation(double latitude, double longitude, String timeStamp, final boolean fromDB, final int visitId) {
        SelliscopeApiEndpointInterface apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);
        List<UserLocation.Visit> userLocationVisits = new ArrayList<>();
        //getting data from sqlite database
        for (UserVisit userVisit : dbHandler.getUSerVisits()) {
            userLocationVisits.add(new UserLocation.Visit(userVisit.getLatitude(), userVisit.getLongitude(), GetAddressFromLatLang.getAddressFromLatLan(getApplicationContext(), userVisit.getLatitude(), userVisit.getLongitude()), userVisit.getTimeStamp()));

        }

        userLocationVisits.add(new UserLocation.Visit(latitude, longitude, GetAddressFromLatLang.getAddressFromLatLan(getApplicationContext(), latitude, longitude), timeStamp));

        Log.d("tareq_test", "" + new Gson().toJson(new UserLocation(userLocationVisits)));

        Call<ResponseBody> call = apiService.sendUserLocation(new UserLocation(userLocationVisits));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Timber.d("Code:" + response.code());
                Gson gson = new Gson();
                if (response.code() == 201) {
                    try {
                        UserLocation.Successful userLocationSuccess = gson.fromJson(response.body().string(), UserLocation.Successful.class);
                        Timber.d("Result:" + userLocationSuccess.result);
                        //   if (fromDB)
                        dbHandler.deleteUserVisit();
                        Log.d("tareq_test", "Data send and deleted from db");
                    } catch (IOException e) {
                        Timber.d("Error:" + e.toString());
                        e.printStackTrace();
                    }
                } else if (response.code() == 400) {

                } else {
                    Toast.makeText(getApplicationContext(), response.code() + " Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
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
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);
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

            Log.d("tareq_test", "OnDestroy Service");
            Intent bintent = new Intent(this, LocationServiceRestarterBroadcastReceiver.class);
            sendBroadcast(bintent);
            myTimer.cancel();
        }
    }
}