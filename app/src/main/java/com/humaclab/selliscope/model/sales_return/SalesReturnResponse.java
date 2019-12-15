package com.humaclab.selliscope.model.sales_return;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SalesReturnResponse implements Serializable {
    @SerializedName("error")
    public boolean error;
    @SerializedName("result")
    public DeliveryResult result;

    public static class DeliveryResult implements Serializable {
        @SerializedName("orders")
        public List<DeliveryList> deliveryList;
    }

    public static class DeliveryList implements Serializable {
        @SerializedName("order_id")
        public int deliveryId;
        @SerializedName("outlet_id")
        public int outletId;
        @SerializedName("outlet_name")
        public String outletName;
        @SerializedName("user")
        public String user;
        @SerializedName("products")
        public List<Product> productList;
    }

    public static class Product implements Serializable {
        @SerializedName("id")
        public int productId;
        @SerializedName("name")
        public String name;
        @SerializedName("variant_row")
        public int variantRow;
        @SerializedName("deliveryQty")
        public int deliveryQty;

    }
}
