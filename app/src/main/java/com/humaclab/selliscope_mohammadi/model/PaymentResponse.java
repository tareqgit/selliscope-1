package com.humaclab.selliscope_mohammadi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Leon on 5/27/17.
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
        public Double amount;
        @SerializedName("type")
        public int type;

        @SerializedName("deposited_bank")
        public String depositedBank;
        @SerializedName("deposited_account")
        public String depositedAccount;
        @SerializedName("deposit_form")
        public String depositForm;
    }

    public static class PaymentSucessfull {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public String result;
    }
}
