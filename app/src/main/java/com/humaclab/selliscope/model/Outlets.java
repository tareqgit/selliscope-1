package com.humaclab.selliscope.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Miaki on 3/18/17.
 */

public class Outlets {
    public static class Successful {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public OutletsResult outletsResult;

        public class OutletsResult {
            @SerializedName("outlet")
            public List<Outlet> outlets;
        }

        public class Outlet {
            @SerializedName("id")
            public int outletId;
            @SerializedName("type")
            public String outletType;
            @SerializedName("color")
            public String color;
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
        }
    }

    public static class Unsuccessful {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public String result;
    }

}
