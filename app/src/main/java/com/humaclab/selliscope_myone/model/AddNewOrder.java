package com.humaclab.selliscope_myone.model;

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
        public String outletId;
        @SerializedName("discount")
        public Double discount;
        @SerializedName("order_date")
        public String date;
        @SerializedName("products")
        public List<Product> products;

        public static class Product {
            @SerializedName("id")
            public String id;
            @SerializedName("qty")
            public int qty;
            @SerializedName("stocktype")
            public String stockType;
            @SerializedName("price")
            public String price;
            @SerializedName("discount")
            public Double discount;
            @SerializedName("flag")
            public String flag;
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
                public String outlet_id;
                @SerializedName("discount")
                public Double discount;
            }
        }
    }
}
