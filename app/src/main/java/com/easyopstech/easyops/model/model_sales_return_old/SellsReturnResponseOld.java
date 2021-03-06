package com.easyopstech.easyops.model.model_sales_return_old;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leon on 6/15/17.
 */

public class SellsReturnResponseOld implements Serializable {
    @SerializedName("error")
    public String error;
    @SerializedName("message")
    public String message;

    @SerializedName("return")
    public SellsReturn sellsReturn;

    public static class SellsReturn implements Serializable {
        @SerializedName("outlet_id")
        public int outletID;
        @SerializedName("order_id")
        public int orderID;
        @SerializedName("product_id")
        public int productID;
        @SerializedName("quantity")
        public int quantity;
        @SerializedName("cause")
        public String cause;
    }
}
