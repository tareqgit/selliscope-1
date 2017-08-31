package com.humaclab.selliscope.model;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.gson.annotations.SerializedName;
import com.humaclab.selliscope.Utils.CurrentTimeUtilityClass;
import com.humaclab.selliscope.Utils.GetAddressFromLatLang;

import java.io.Serializable;

import timber.log.Timber;

/**
 * Created by leon on 8/31/17.
 */

public class InstantLocation implements Serializable {

    public InstantLocation.LocationDetails getLocation(final Context context) {
        final LocationDetails locationDetails = new LocationDetails();
        final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Awareness.API)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                if (checkPermission(context)) {
                    Awareness.SnapshotApi.getLocation(googleApiClient).setResultCallback(new ResultCallback<LocationResult>() {
                        @Override
                        public void onResult(@NonNull LocationResult locationResult) {
                            if (locationResult.getStatus().isSuccess()) {
                                Location location = locationResult.getLocation();
                                double latitude = Double.parseDouble(String.format("%.05f", location.getLatitude()));
                                double longitude = Double.parseDouble(String.format("%.05f", location.getLongitude()));

                                Timber.d("Latitude: " + latitude + " Longitude: " + longitude);

                                locationDetails.setLatitude(latitude);
                                locationDetails.setLongitude(longitude);
                                locationDetails.setTimeStamp(CurrentTimeUtilityClass.getCurrentTimeStamp());
                                locationDetails.setAddress(GetAddressFromLatLang.getAddressFromLatLan(context, latitude, longitude));
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
        googleApiClient.connect();

        return locationDetails;
    }

    private boolean checkPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    //For getting location
    public static class LocationDetails {
        @SerializedName("latitude")
        private double latitude;
        @SerializedName("longitude")
        private double longitude;
        @SerializedName("formatted_address")
        private String address;
        @SerializedName("created_at")
        private String timeStamp;

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
        }
    }
}
