package com.humaclab.selliscope_rangs.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Leon on 12/21/2017.
 */

public class CreateOutlet {
    @SerializedName("type_id")
    public double outletTypeId;
    @SerializedName("name")
    public String outletName;
    @SerializedName("owner")
    public String ownerName;
    @SerializedName("address")
    public String address;
    @SerializedName("thana_id")
    public int outletThanaId;
    @SerializedName("phone")
    public String outletPhoneNumber;
    @SerializedName("latitude")
    public String outletLatitude;
    @SerializedName("longitude")
    public String outletLongitude;
    @SerializedName("img")
    public String outletImage;
    @SerializedName("error")
    public boolean error;
    @SerializedName("result")
    public String result;

    public CreateOutlet(int outletTypeId, String outletName, String ownerName, String address,
                        int outletThanaId, String outletPhoneNumber, String outletLatitude,
                        String outletLongitude, String outletImage) {
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
}
