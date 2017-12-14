package com.humaclab.selliscope_myone.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leon on 6/29/17.
 */

public class InspectionResponse implements Serializable {
    @SerializedName("error")
    public String error;
    @SerializedName("message")
    public String message;

    @SerializedName("inspection")
    public Inspection inspection;

    public static class Inspection implements Serializable {
        @SerializedName("outlet_id")
        public int outletID;
        @SerializedName("image")
        public String image;
        @SerializedName("quantity")
        public int quantity;
        @SerializedName("promotion_type")
        public String promotionType;
        @SerializedName("is_damaged")
        public boolean iDamaged;
        @SerializedName("condition")
        public String condition;
    }
}
