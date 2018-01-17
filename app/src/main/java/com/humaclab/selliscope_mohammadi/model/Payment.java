package com.humaclab.selliscope_mohammadi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by leon on 6/11/17.
 */

public class Payment implements Serializable {
    @SerializedName("error")
    public boolean error;
    @SerializedName("result")
    public OrderResult result;

    public static class OrderResult implements Serializable {
        @SerializedName("orders")
        public List<OrderList> orderList;
    }

    public static class OrderList implements Serializable {
        @SerializedName("id")
        public int orderId;
        @SerializedName("outlet_id")
        public int outletId;
        @SerializedName("outlet_name")
        public String outletName;
        @SerializedName("discount")
        public String discount;
        @SerializedName("amount")
        public String amount;
        @SerializedName("order_date")
        public String orderDate;
        @SerializedName("products")
        public List<Product> productList;
        @SerializedName("truck_fare")
        public int truckFare;
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
        @SerializedName("amount")
        public String amount;
        @SerializedName("discount")
        public String discount;
    }
}
