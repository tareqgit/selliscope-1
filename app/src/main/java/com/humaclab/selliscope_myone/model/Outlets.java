package com.humaclab.selliscope_myone.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Miaki on 3/18/17.
 */

public class Outlets implements Serializable {
    public static class Successful implements Serializable {
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
            public String outletId;
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
            public String outletLatitude;
            @SerializedName("longitude")
            public String outletLongitude;
            @SerializedName("due")
            public String outletDue;
        }
    }

    public static class Unsuccessful {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public String result;
    }
}
