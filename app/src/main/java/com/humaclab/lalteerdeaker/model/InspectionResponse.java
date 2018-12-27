package com.humaclab.lalteerdeaker.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leon on 6/29/17.
 */

public class InspectionResponse implements Serializable {
    @SerializedName("error")
    public boolean error;
    @SerializedName("result")
    public String message;

    @SerializedName("inspection")
    public Inspection inspection;

    public static class Inspection implements Serializable {
        @SerializedName("dealer_id")
        public int dealer_id;
        @SerializedName("img")
        public String img;
        @SerializedName("qty")
        public int qty;
        @SerializedName("promotion")
        public String promotion;
        @SerializedName("damaged")
        public int damaged;
        @SerializedName("condition")
        public String condition;
    }
}
