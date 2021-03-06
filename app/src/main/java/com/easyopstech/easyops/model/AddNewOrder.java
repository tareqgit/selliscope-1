package com.easyopstech.easyops.model;

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
        public Double discount;


        @SerializedName("products")
        public List<Product> products;

        @SerializedName("comments")
        public String comment;

        @SerializedName("lat")
        public String latitude;

        @SerializedName("long")
        public String longitude;

        @SerializedName("orderTotal")
        public Double orderTotal;

        @SerializedName("orderGrandTotal")
        public Double orderGrandTotal;

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
            @SerializedName("tpDiscount")
            public Double tpDiscount;

            @SerializedName("productTotal")
            public Double productTotal;

            @SerializedName("productSubTotal")
            public Double productSubTotal;

            @SerializedName("is_free")
            public int is_free;

            @SerializedName("order_return_id")
            public int order_return_id;
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
                public Double discount;
                @SerializedName("amount")
                public Double amount;
            }
        }
    }
}
