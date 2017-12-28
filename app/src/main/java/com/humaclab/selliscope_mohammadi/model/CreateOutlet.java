package com.humaclab.selliscope_mohammadi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dipu_ on 3/25/2017.
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
    public double outletLatitude;
    @SerializedName("longitude")
    public double outletLongitude;
    @SerializedName("img")
    public String outletImage;
    @SerializedName("error")
    public boolean error;
    @SerializedName("result")
    public String result;

    public CreateOutlet(int outletTypeId, String outletName, String ownerName, String address,
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
}
