package com.humaclab.selliscope_mohammadi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Leon on 3/25/2017.
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

    @SerializedName("prefix_id")
    public int prefixID;
    @SerializedName("payment_type")
    public int paymentType;
    @SerializedName("bank_name")
    public String bankName;
    @SerializedName("bg_amount")
    public String bgAmount;
    @SerializedName("exp_date")
    public String expDate;
    @SerializedName("credit_limit")
    public String creditLimit;
    @SerializedName("email")
    public String email;

    public CreateOutlet(int outletTypeId, String outletName, String ownerName, String address,
                        int outletThanaId, String outletPhoneNumber, double outletLatitude,
                        double outletLongitude, String outletImage,
                        int prefixID, int paymentType, String bankName, String bgAmount, String expDate, String creditAmount, String email) {
        this.outletTypeId = outletTypeId;
        this.outletName = outletName;
        this.ownerName = ownerName;
        this.address = address;
        this.outletThanaId = outletThanaId;
        this.outletPhoneNumber = outletPhoneNumber;
        this.outletLatitude = outletLatitude;
        this.outletLongitude = outletLongitude;
        this.outletImage = outletImage;
        this.prefixID = prefixID;
        this.paymentType = paymentType;
        this.bankName = bankName;
        this.bgAmount = bgAmount;
        this.expDate = expDate;
        this.creditLimit = creditAmount;
        this.email = email;
    }
}
