package com.sokrio.sokrio_classic.model.purchase_history;

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