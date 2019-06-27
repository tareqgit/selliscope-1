package com.humaclab.selliscope_mohammadi.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope_mohammadi.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_mohammadi.SelliscopeApplication;
import com.humaclab.selliscope_mohammadi.dbmodel.UserVisit;
import com.humaclab.selliscope_mohammadi.model.UserLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
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
    private DatabaseHandler dbHandler;

    public SendUserLocationData(Context context) {
        this.context = context;
        this.sessionManager = new SessionManager(this.context);
        this.dbHandler = new DatabaseHandler(this.context);

        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean getLocation() {
        long mLocTrackingInterval = 100; // 1 second
        float trackingDistance = 0;
        LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;

        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(trackingAccuracy)
                .setDistance(trackingDistance)
                .setInterval(mLocTrackingInterval);

        SmartLocation.with(context)
                .location()
                .oneFix()
                .config(builder.build())
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
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

                    }
                });
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
                        Timber.d("PrefixResult:" + userLocationSuccess.result);
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

    public void getInstantLocation(Activity activity, final OnGetLocation onGetLocation) {
        if (checkPermission()) {
            long mLocTrackingInterval = 0; // 1 second
            float trackingDistance = 0;
            LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;

            LocationParams.Builder builder = new LocationParams.Builder()
                    .setAccuracy(trackingAccuracy)
                    .setDistance(trackingDistance)
                    .setInterval(mLocTrackingInterval);

            SmartLocation.with(context)
                    .location()
                    .oneFix()
                    .config(builder.build())
                    .start(new OnLocationUpdatedListener() {
                        @Override
                        public void onLocationUpdated(Location location) {
                            double latitude = Double.parseDouble(String.format("%.05f", location.getLatitude()));
                            double longitude = Double.parseDouble(String.format("%.05f", location.getLongitude()));

                            Timber.d("Latitude: " + latitude + " Longitude: " + longitude);
                            onGetLocation.getLocation(latitude, longitude);
                        }
                    });
        } else {
            String[] permissions = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
            ActivityCompat.requestPermissions(activity, permissions, 404);
        }
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public interface OnGetLocation {
        void getLocation(Double latitude, Double longitude);
    }
}
