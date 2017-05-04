package com.humaclab.selliscope.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Nahid on 3/12/2017.
 */

public class OutletDetails {
    public static class Successful {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public OutletResult result;

        public class OutletResult {
            @SerializedName("id")
            public int outletId;
            @SerializedName("type_id")
            public int outletTypeId;
            @SerializedName("client_id")
            public int clientId;
            @SerializedName("name")
            public String outletName;
            @SerializedName("owner")
            public String outletOwner;
            @SerializedName("address")
            public String outletAddress;
            @SerializedName("thana_id")
            public String outletThanaId;
            @SerializedName("phone")
            public String outletPhoneNumber;
            @SerializedName("img")
            public String outletImageUrl;
            @SerializedName("latitude")
            public Double outletLatitude;
            @SerializedName("longitude")
            public Double outletLongitude;
            @SerializedName("status")
            public int outletStatus;
            @SerializedName("user_id")
            public int userId;
            @SerializedName("created_at")
            public String createdAt;
            @SerializedName("updated_at")
            public String updatedAt;
        }
    }
    public static class Unsuccessful {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public String result;
    }

}
