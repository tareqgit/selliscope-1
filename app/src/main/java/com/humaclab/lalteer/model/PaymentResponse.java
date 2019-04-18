package com.humaclab.lalteer.model;

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
        @SerializedName("discount")
        public int discount;
        @SerializedName("bank_name")
        public  String bank_name;
        @SerializedName("cheque_no")
        public String cheque_no;
        @SerializedName("depositedSlipNumber")
        public String deposit_slip;
        @SerializedName("deposit_to")
        public String deposit_to;
        @SerializedName("deposit_from")
        public String deposit_from;

        @SerializedName("cheque_date")
        public String cheque_date;
        @SerializedName("img")
        public String img;
    }

    public static class PaymentSucessfull {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public String result;
    }
}
