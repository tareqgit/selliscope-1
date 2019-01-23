package com.humaclab.akij_selliscope.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dipu_ on 3/25/2017.
 */

public class CreateOutlet {
    /*    @SerializedName("type_id")
        public double outletTypeId;*/
    @SerializedName("name")
    public String outletName;
    @SerializedName("owner")
    public String ownerName;
    @SerializedName("address")
    public String address;
    @SerializedName("route_id")
    public int outletThanaId;

    @SerializedName("line")
    public String line;
    @SerializedName("phone")
    public String outletPhoneNumber;
/*    @SerializedName("refphone")
    public String outletrefPhoneNumber;*/

    @SerializedName("comment")
    public String comment;

    @SerializedName("latitude")
    public double outletLatitude;

    @SerializedName("longitude")
    public double outletLongitude;

    @SerializedName("img")
    public String outletImage;

    @SerializedName("cooler_status")
    public String cooler_status;

    @SerializedName("other_bevarage")
    public String other_bevarage;

    @SerializedName("error")
    public boolean error;
    @SerializedName("result")
    public String result;

/*    public CreateOutlet(int outletTypeId, String outletName, String ownerName, String address,
                        int outletThanaId, String outletPhoneNumber, double outletLatitude,
                        double outletLongitude, String outletImage,String outletrefPhoneNumber) {    */

    public CreateOutlet(String outletName, String ownerName, String address, int outletThanaId,
                        String line, String outletPhoneNumber, String comment,
                        double outletLatitude, double outletLongitude, String outletImage, String cooler_status, String other_bevarage) {
        this.outletName = outletName;
        this.ownerName = ownerName;
        this.address = address;
        this.outletThanaId = outletThanaId;
        this.line = line;
        this.outletPhoneNumber = outletPhoneNumber;
        this.comment = comment;
        this.outletLatitude = outletLatitude;
        this.outletLongitude = outletLongitude;
        this.outletImage = outletImage;
        this.cooler_status = cooler_status;
        this.other_bevarage = other_bevarage;
        // this.outletTypeId = outletTypeId;
        //this.outletName = outletName;
        //this.ownerName = ownerName;
        //this.address = address;
        //this.outletThanaId = outletThanaId;
        //this.outletPhoneNumber = outletPhoneNumber;
        //this.outletLatitude = outletLatitude;
        //this.outletLongitude = outletLongitude;
        //this.outletImage = outletImage;
        // this.outletrefPhoneNumber = outletrefPhoneNumber;


    }
}
