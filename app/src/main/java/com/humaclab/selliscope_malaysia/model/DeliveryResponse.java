package com.humaclab.selliscope_malaysia.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tonmoy on 5/16/17.
 */

public class DeliveryResponse implements Serializable {
    @SerializedName("error")
    public boolean error;
    @SerializedName("result")
    public DeliveryResult result;

    public static class DeliveryResult implements Serializable {
        @SerializedName("orders")
        public List<DeliveryList> deliveryList;
    }

    public static class DeliveryList implements Serializable {
        @SerializedName("id")
        public int deliveryId;
        @SerializedName("outlet_id")
        public int outletId;
        @SerializedName("outlet_name")
        public String outletName;
        @SerializedName("discount")
        public String discount;
        @SerializedName("amount")
        public String amount;
        @SerializedName("order_date")
        public String deliveryDate;
        @SerializedName("products")
        public List<Product> productList;
    }

    public static class Product implements Serializable {
        @SerializedName("id")
        public int productId;
        @SerializedName("name")
        public String name;
        @SerializedName("price")
        public String price;
        @SerializedName("qty")
        public int qty;
        @SerializedName("dQty")
        public String dQty;
        @SerializedName("amount")
        public String amount;
        @SerializedName("discount")
        public String discount;
        @SerializedName("variant_row")
        public int variantRow;
    }
}
