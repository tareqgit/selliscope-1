package com.humaclab.akij_selliscope.model.PurchaseHistory;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by leon on 6/3/18.
 */

public class PurchaseHistoryItem implements Serializable {

    @SerializedName("order_date")
    private String orderDate;

    @SerializedName("total")
    private String total;

    @SerializedName("due")
    private String due;

    @SerializedName("paid")
    private String paid;

    @SerializedName("order_id")
    private String orderId;

    @SerializedName("line")
    private String line;

    @SerializedName("slab")
    private String slab;

    @SerializedName("outlet_img")
    private String outlet_img;

    @SerializedName("memo_img")
    private String memo_img;

    public String getOutlet() {
        return outlet;
    }

    public void setOutlet(String outlet) {
        this.outlet = outlet;
    }

    public String getOrder_by() {
        return order_by;
    }

    public void setOrder_by(String order_by) {
        this.order_by = order_by;
    }

    public String getDealer_stock() {
        return dealer_stock;
    }

    public void setDealer_stock(String dealer_stock) {
        this.dealer_stock = dealer_stock;
    }

    @SerializedName("outlet")
    private String outlet;

    @SerializedName("order_by")
    private String order_by;

    @SerializedName("dealer_stock")
    private String dealer_stock;



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



    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    @SerializedName("discount")
    private String discount;



    @SerializedName("order_details")
    private List<OrderDetailsItem> orderDetails;

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDue() {
        return due;
    }

    public void setDue(String due) {
        this.due = due;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<OrderDetailsItem> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailsItem> orderDetails) {
        this.orderDetails = orderDetails;
    }
}