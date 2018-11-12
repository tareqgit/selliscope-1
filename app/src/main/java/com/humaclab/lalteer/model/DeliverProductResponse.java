package com.humaclab.lalteer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tonmoy on 6/4/17.
 */

public class DeliverProductResponse implements Serializable {
    @SerializedName("result")
    public String result;
    @SerializedName("order")
    public Order order;

    public static class Order implements Serializable {
        @SerializedName("order_id")
        public int orderId;
        @SerializedName("outlet_id")
        public int outletId;
        @SerializedName("products")
        public List<Product> products;

        public static class Product implements Serializable {
            @SerializedName("product_id")
            public int productId;
            @SerializedName("qty")
            public int qty;
            @SerializedName("godown_id")
            public int godownId;
            @SerializedName("variant_row")
            public int variantRow;
            @SerializedName("delivery_date")
            public String delivery_date;
        }
    }
}
