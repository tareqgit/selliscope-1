package com.humaclab.selliscope.Service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.Utils.CurrentTimeUtilityClass;
import com.humaclab.selliscope.Utils.DatabaseHandler;
import com.humaclab.selliscope.Utils.GetAddressFromLatLang;
import com.humaclab.selliscope.Utils.NetworkUtility;
import com.humaclab.selliscope.Utils.SessionManager;
import com.humaclab.selliscope.dbmodel.UserVisit;
import com.humaclab.selliscope.model.UserLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class UserTrackingService extends Service {
    private GoogleApiClient googleApiClient;
    private SelliscopeApiEndpointInterface apiService;
    private DatabaseHandler dbHandler;

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbHandler = new DatabaseHandler(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("User Tracking Service OnStartCommand");
        googleApiClient = new GoogleApiClient.Builder(UserTrackingService.this)
                .addApi(Awareness.API)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                getLocation();
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });
        googleApiClient.connect();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void getLocation() {
        if (checkPermission(getApplicationContext())) {
            Awareness.SnapshotApi.getLocation(googleApiClient).setResultCallback(new ResultCallback<LocationResult>() {
                @Override
                public void onResult(@NonNull LocationResult locationResult) {
                    if (locationResult.getStatus().isSuccess()) {
                        Location location = locationResult.getLocation();
                        double latitude = Double.parseDouble(String.format("%.05f", location.getLatitude()));
                        double longitude = Double.parseDouble(String.format("%.05f", location.getLongitude()));

                        Timber.d("Latitude: " + latitude
                                + "Longitude: " + longitude);
                        if (NetworkUtility.isNetworkAvailable(UserTrackingService.this)) {
                            sendUserLocation(
                                    latitude,
                                    longitude,
                                    CurrentTimeUtilityClass.getCurrentTimeStamp(),
                                    false,
                                    -1);
                            List<UserVisit> userVisits = dbHandler.getUSerVisits();
                            if (!userVisits.isEmpty())
                                for (UserVisit userVisit : userVisits) {
                                    sendUserLocation(userVisit.getLatitude(),
                                            userVisit.getLongitude(),
                                            userVisit.getTimeStamp(),
                                                    /*CurrentTimeUtilityClass
                                                            .getCurrentTimeStamp(),*/ true,
                                            userVisit.getVisitId());
                                }
                        } else {
                            dbHandler.addUserVisits(
                                    new UserVisit(latitude,
                                            longitude,
                                            CurrentTimeUtilityClass.getCurrentTimeStamp())
                            );
                            Timber.d("User Location Saved in Database");
                        }
                    } else {
                        Timber.d("Didn't get Location Data");
//                        stopSelf();
                    }
                }
            });
        } else {
            Timber.d("Location Permission is not enabled.");
        }
    }

    private void sendUserLocation(double latitude, double longitude, String timeStamp, final boolean fromDB, final int visitId) {
        SessionManager sessionManager = new SessionManager(UserTrackingService.this);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);
        List<UserLocation.Visit> userLocationVisits = new ArrayList<>();
        userLocationVisits.add(new UserLocation.Visit(latitude, longitude, GetAddressFromLatLang.getAddressFromLatLan(getApplicationContext(), latitude, longitude), timeStamp));
        Call<ResponseBody> call = apiService.sendUserLocation(new UserLocation(userLocationVisits));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Timber.d("Code:" + response.code());
                Gson gson = new Gson();
                if (response.code() == 201) {
                    try {
                        UserLocation.Successful userLocationSuccess
                                = gson.fromJson(response.body().string()
                                , UserLocation.Successful.class);
                        Timber.d("Result:" + userLocationSuccess.result);
                        if (fromDB)
                            dbHandler.deleteUserVisit(visitId);
//                        stopSelf();
                    } catch (IOException e) {
                        Timber.d("Error:" + e.toString());
                        e.printStackTrace();
                    }
                } else if (response.code() == 400) {

                } else {
                    Toast.makeText(UserTrackingService.this,
                            "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                Log.d("Response", t.toString());

            }
        });
    }

    @Override
    public void onDestroy() {
        googleApiClient.disconnect();
        super.onDestroy();
    }
}
