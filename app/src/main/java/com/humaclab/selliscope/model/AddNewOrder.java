package com.humaclab.selliscope.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tonmoy on 5/13/17.
 */

public class AddNewOrder implements Serializable {
    @SerializedName("order")
    public NewOrder newOrder;

    public static class NewOrder {
        @SerializedName("outlet_id")
        public int outletId;
        @SerializedName("discount")
        public int discount;
        @SerializedName("products")
        public List<Product> products;

        public static class Product {
            @SerializedName("id")
            public int id;
            @SerializedName("qty")
            public int qty;
            @SerializedName("row")
            public int row;
            @SerializedName("price")
            public String price;
            @SerializedName("discount")
            public Double discount;
        }
    }

    public static class OrderResponse {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public Result result;

        public static class Result {
            @SerializedName("order")
            public OrderResult order;

            public static class OrderResult {
                @SerializedName("id")
                public int id;
                @SerializedName("outlet_id")
                public int outlet_id;
                @SerializedName("discount")
                public int discount;
            }
        }
    }
}
