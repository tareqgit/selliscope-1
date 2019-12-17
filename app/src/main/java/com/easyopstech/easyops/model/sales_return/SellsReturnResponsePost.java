package com.easyopstech.easyops.model.sales_return;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leon on 6/15/17.
 */

public class SellsReturnResponsePost implements Serializable {
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
        @SerializedName("return_date")
        public String mReturnDate;
        @SerializedName("variant_row")
        public int mVariantRow;
        @SerializedName("note")
        public String mNote;
        @SerializedName("reason_id")
        public int mReasonId;
        @SerializedName("sku")
        public String mSku;
        @SerializedName("quantity")
        public int quantity;
    }
}
