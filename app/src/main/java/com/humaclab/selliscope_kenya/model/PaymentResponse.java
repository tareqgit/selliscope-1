package com.humaclab.selliscope_kenya.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tonmoy on 5/27/17.
 */

public class PaymentResponse {
    @SerializedName("payment")
    public Payment payment;

    public static class Payment {
        @SerializedName("order_id")
        public int order_id;
        @SerializedName("outlet_id")
        public int outlet_id;
        @SerializedName("amount")
        public int amount;
        @SerializedName("type")
        public int type;
    }

    public static class PaymentSucessfull {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public String result;
    }
}
