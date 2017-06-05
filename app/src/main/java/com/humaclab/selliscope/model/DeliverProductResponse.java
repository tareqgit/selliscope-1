package com.humaclab.selliscope.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tonmoy on 6/4/17.
 */

public class DeliverProductResponse implements Serializable {
    @SerializedName("order")
    public Order order;

    public static class Order implements Serializable {
        @SerializedName("id")
        public int orderId;
        @SerializedName("outlet_id")
        public int outletId;
        @SerializedName("products")
        public List<Product> products;

        public static class Product implements Serializable {
            @SerializedName("id")
            public int productId;
            @SerializedName("qty")
            public int qty;
        }
    }
}