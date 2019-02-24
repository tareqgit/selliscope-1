package com.humaclab.akij_selliscope.model.Order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmObject;

/**
 * Created by tonmoy on 5/13/17.
 */

public class Order implements Serializable {
    @SerializedName("order")
    public NewOrder newOrder;

    public static class NewOrder  {
        @SerializedName("outlet_id")
        public int outletId;

        @SerializedName("discount")
        public Double discount;

        @SerializedName("comment")
        public String comment;

        @SerializedName("lat")
        public String latitude;

        @SerializedName("long")
        public String longitude;

        @SerializedName("line")
        public String line;

        @SerializedName("slab")
        public String slab;

        @SerializedName("order_date")
        public String order_date;

        @SerializedName("outlet_img")
        public String outlet_img;

        @SerializedName("memo_img")
        public String memo_img;

        public String getStock() {
            return stock;
        }

        public void setStock(String stock) {
            this.stock = stock;
        }

        @SerializedName("stock")
        public String stock;

        public int getOutletId() {
            return outletId;
        }

        public void setOutletId(int outletId) {
            this.outletId = outletId;
        }

        public Double getDiscount() {
            return discount;
        }

        public void setDiscount(Double discount) {
            this.discount = discount;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public String getSlab() {
            return slab;
        }

        public void setSlab(String slab) {
            this.slab = slab;
        }

        public String getOrder_date() {
            return order_date;
        }

        public void setOrder_date(String order_date) {
            this.order_date = order_date;
        }

        public String getOutlet_img() {
            return outlet_img;
        }

        public void setOutlet_img(String outlet_img) {
            this.outlet_img = outlet_img;
        }

        public String getMemo_img() {
            return memo_img;
        }

        public void setMemo_img(String memo_img) {
            this.memo_img = memo_img;
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
            }
        }
    }
}
