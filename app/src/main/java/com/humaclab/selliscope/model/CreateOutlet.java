package com.humaclab.selliscope.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dipu_ on 3/25/2017.
 */

public class CreateOutlet {
    @SerializedName("outlet")
    List<Outlet> outlets;

    public CreateOutlet(List<Outlet> outlets) {
        this.outlets = outlets;

    }

    public static class Outlet {
        public Outlet(int outletTypeId, String outletName, String ownerName, String address,
                      int outletThanaId, String outletPhoneNumber, double outletLatitude,
                      double outletLongitude, String outletImage) {
            this.outletTypeId = outletTypeId;
            this.outletName = outletName;
            this.ownerName = ownerName;
            this.address = address;
            this.outletThanaId = outletThanaId;
            this.outletPhoneNumber = outletPhoneNumber;
            this.outletLatitude = outletLatitude;
            this.outletLongitude = outletLongitude;
            this.outletImage = outletImage;
        }

        @SerializedName("type_id")
        public double outletTypeId;
        @SerializedName("name")
        public String outletName;
        @SerializedName("owner")
        String ownerName;
        @SerializedName("address")
        String address;
        @SerializedName("thana_id")
        int outletThanaId;
        @SerializedName("phone")
        String outletPhoneNumber;
        @SerializedName("latitude")
        double outletLatitude;
        @SerializedName("longitude")
        double outletLongitude;
        @SerializedName("image")
        String outletImage;
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
