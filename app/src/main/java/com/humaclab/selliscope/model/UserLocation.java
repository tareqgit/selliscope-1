package com.humaclab.selliscope.model;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

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

    @Entity(tableName = "check_in")
    public static class Visit {
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        public int id;

        @SerializedName("latitude")
        @ColumnInfo(name = "lat")
        public double latitude;

        @SerializedName("longitude")
        @ColumnInfo(name = "long")
        public double longitude;

        @SerializedName("formatted_address")
        @ColumnInfo(name = "address")
        public String address;

        @Nullable
        @SerializedName("created_at")
        @ColumnInfo(name = "created_at")
        public String timeStamp;

        @Nullable
        @SerializedName("outlet_id")
        @ColumnInfo(name = "outlet_id")
        public int outletId;

        @Nullable
        @SerializedName("img")
        @ColumnInfo(name = "img")
        public String img;

        @Nullable
        @SerializedName("comment")
        @ColumnInfo(name = "comment")
        public String comment;

        @Ignore
        public Visit(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.address = address;
        }

        public Visit(double latitude, double longitude, String address, @Nullable String timeStamp, int outletId, @Nullable String img, @Nullable String comment) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.address = address;
            this.timeStamp = timeStamp;
            this.outletId = outletId;
            this.img = img;
            this.comment = comment;
        }
        @Ignore
        public Visit(double latitude, double longitude, String address, String timeStamp) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.address = address;
            this.timeStamp = timeStamp;
        }
        @Ignore
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
        @SerializedName("msg")
        public String msg;
    }

    public static class Unsuccessful {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public String result;
    }
}
