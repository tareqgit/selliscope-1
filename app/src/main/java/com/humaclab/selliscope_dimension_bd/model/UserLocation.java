package com.humaclab.selliscope_dimension_bd.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Miaki on 3/19/17.
 * Updated by Leon on 8/20/17.
 */

public class UserLocation {
    @SerializedName("visit")
    List<Visit> visits;

    public UserLocation(List<Visit> visits) {
        this.visits = visits;
    }

    public static class Visit {
        @SerializedName("latitude")
        public double latitude;
        @SerializedName("longitude")
        public double longitude;
        @SerializedName("formatted_address")
        public String address;
        @Nullable
        @SerializedName("created_at")
        public String timeStamp;
        @Nullable
        @SerializedName("outlet_id")
        public int outletId;

        public Visit(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.address = address;
        }

        public Visit(double latitude, double longitude, String address, String timeStamp) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.address = address;
            this.timeStamp = timeStamp;
        }

        public Visit(double latitude, double longitude, String address, int outletId) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.address = address;
            this.outletId = outletId;
        }
    }

    public static class Successful {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public String result;
    }

    public static class Unsuccessful {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public String result;
    }
}
