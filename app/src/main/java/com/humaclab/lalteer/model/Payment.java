package com.humaclab.lalteer.model;

import android.graphics.Bitmap;

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

        @SerializedName("paid")
        public double paid;

        @SerializedName("due")
        public double due;

        @SerializedName("products")
        public List<Product> productList;

       /*By tareq for adapter perticular photo select*/
        private Bitmap photo;
        private String img;

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public Bitmap getPhoto() {
            return photo;
        }

        public void setPhoto(Bitmap photo) {
            this.photo = photo;
        }

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
