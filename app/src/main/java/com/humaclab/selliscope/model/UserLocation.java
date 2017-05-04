package com.humaclab.selliscope.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Miaki on 3/19/17.
 */

public class UserLocation {
    @SerializedName("visit")
    List<Visit> visits;

    public UserLocation(List<Visit> visits) {
        this.visits = visits;

    }

    public static class Visit {
        public Visit(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Visit(double latutude, double longitude, String timeStamp) {
            this.latitude = latutude;
            this.longitude = longitude;
            this.timeStamp = timeStamp;
        }

        public Visit(double latitude, double longitude, int outletId) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.outletId = outletId;
        }

        @SerializedName("latitude")
        public double latitude;
        @SerializedName("longitude")
        public double longitude;
        @Nullable
        @SerializedName("created_at")
        public String timeStamp;
        @Nullable
        @SerializedName("outlet_id")
        public int outletId;
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
