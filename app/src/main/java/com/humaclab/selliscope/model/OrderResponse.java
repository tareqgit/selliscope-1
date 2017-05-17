package com.humaclab.selliscope.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tonmoy on 5/16/17.
 */

public class OrderResponse implements Serializable {
    @SerializedName("error")
    public boolean error;
    @SerializedName("result")
    public OrderResult result;

    public static class OrderResult {
        @SerializedName("orders")
        public List<OrderList> orderList;
    }

    public static class OrderList {
        @SerializedName("id")
        public int orderId;
        @SerializedName("date")
        public String orderDate;
        @SerializedName("outlet_id")
        public int outletId;
        @SerializedName("outlet_name")
        public String outletName;
        @SerializedName("products")
        public List<Product> productList;
    }

    public static class Product {
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
