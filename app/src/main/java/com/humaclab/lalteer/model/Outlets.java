package com.humaclab.lalteer.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by leon on 5/18/17.
 */

public class Outlets implements Serializable {
    @SerializedName("error")
    public boolean error;
    @SerializedName("result")
    public OutletsResult outletsResult;

    public static class OutletsResult implements Serializable {
        @SerializedName("outlet")
        public List<Outlet> outlets;
    }

    public static class Outlet implements Serializable {
        @SerializedName("id")
        public int outletId;
        @SerializedName("type")
        public String outletType;
        @SerializedName("name")
        public String outletName;
        @SerializedName("owner")
        public String ownerName;
        @SerializedName("address")
        public String outletAddress;
        @SerializedName("district")
        public String district;
        @SerializedName("thana")
        public String thana;
        @SerializedName("phone")
        public String phone;
        @Nullable
        @SerializedName("img")
        public String outletImgUrl;
        @SerializedName("latitude")
        public Double outletLatitude;
        @SerializedName("longitude")
        public Double outletLongitude;
        @SerializedName("due")
        public String outletDue;
        @SerializedName("outlet_routeplan")
        public String outlet_routeplan;
    }
}
