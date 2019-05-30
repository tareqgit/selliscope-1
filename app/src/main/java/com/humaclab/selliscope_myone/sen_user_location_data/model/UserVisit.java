/*
 * Created by Tareq Islam on 5/30/19 1:29 PM
 *
 *  Last modified 5/19/19 1:08 PM
 */

package com.humaclab.selliscope_myone.sen_user_location_data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by dipu_ on 4/22/2017.
 */

@Entity(tableName = "user_visits")
public class UserVisit implements Serializable {
    @NonNull
    @PrimaryKey
    @SerializedName("id")
    public String id;

    @SerializedName("latitude")
    public double latitude;
    @SerializedName("longitude")
    public double longitude;
    @SerializedName("time_stamp")
    public String timeStamp;
    @SerializedName("visit_id")
    public int visitId = -1;

    public UserVisit(double latitude, double longitude, String timeStamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeStamp = timeStamp;
    }




}
