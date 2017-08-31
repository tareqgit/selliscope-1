package com.humaclab.selliscope.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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
import com.humaclab.selliscope.dbmodel.UserVisit;
import com.humaclab.selliscope.model.InstantLocation;
import com.humaclab.selliscope.model.UserLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by leon on 8/31/17.
 */

public class SendUserLocationData {
    private Context context;
    private SessionManager sessionManager;
    private GoogleApiClient googleApiClient;
    private DatabaseHandler dbHandler;

    public SendUserLocationData(Context context) {
        this.context = context;
        this.sessionManager = new SessionManager(this.context);
        this.dbHandler = new DatabaseHandler(this.context);

    }

    public boolean getLocation() {
        this.googleApiClient = new GoogleApiClient.Builder(this.context)
                .addApi(Awareness.API)
                .addApi(LocationServices.API)
                .build();
        this.googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                if (checkPermission()) {
                    Awareness.SnapshotApi.getLocation(googleApiClient).setResultCallback(new ResultCallback<LocationResult>() {
                        @Override
                        public void onResult(@NonNull LocationResult locationResult) {
                            if (locationResult.getStatus().isSuccess()) {
                                Location location = locationResult.getLocation();
                                double latitude = Double.parseDouble(String.format("%.05f", location.getLatitude()));
                                double longitude = Double.parseDouble(String.format("%.05f", location.getLongitude()));

                                Timber.d("Latitude: " + latitude + " Longitude: " + longitude);
                                if (NetworkUtility.isNetworkAvailable(context)) {
                                    sendUserLocation(
                                            latitude,
                                            longitude,
                                            CurrentTimeUtilityClass.getCurrentTimeStamp(),
                                            false,
                                            -1);
                                    List<UserVisit> userVisits = dbHandler.getUSerVisits();
                                    if (!userVisits.isEmpty())
                                        for (UserVisit userVisit : userVisits) {
                                            sendUserLocation(
                                                    userVisit.getLatitude(),
                                                    userVisit.getLongitude(),
                                                    userVisit.getTimeStamp(),
                                                    true,
                                                    userVisit.getVisitId()
                                            );
                                        }
                                } else {
                                    dbHandler.addUserVisits(new UserVisit(latitude, longitude, CurrentTimeUtilityClass.getCurrentTimeStamp()));
                                    Timber.d("User Location Saved in Database");
                                }
                            } else {
                                Timber.d("Didn't get Location Data");
                            }
                        }
                    });
                } else {
                    Timber.d("Location Permission is not enabled.");
                }
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });
        this.googleApiClient.connect();
        return true;
    }

    private void sendUserLocation(double latitude, double longitude, String timeStamp, final boolean fromDB, final int visitId) {
        SelliscopeApiEndpointInterface apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);
        List<UserLocation.Visit> userLocationVisits = new ArrayList<>();
        userLocationVisits.add(new UserLocation.Visit(latitude, longitude, GetAddressFromLatLang.getAddressFromLatLan(this.context, latitude, longitude), timeStamp));
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
                        if (fromDB)
                            dbHandler.deleteUserVisit(visitId);
                    } catch (IOException e) {
                        Timber.d("Error:" + e.toString());
                        e.printStackTrace();
                    }
                } else if (response.code() == 400) {

                } else {
                    Toast.makeText(context, "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Response", t.toString());
            }
        });
    }

    public InstantLocation.LocationDetails getInstantLocation() {
        InstantLocation instantLocation = new InstantLocation();
        return instantLocation.getLocation(this.context);
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
