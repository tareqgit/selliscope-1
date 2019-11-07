
/*
 * Created by Tareq Islam on 4/3/19 2:38 PM
 *
 *  Last modified 3/28/19 10:52 AM
 */

package com.humaclab.lalteer.model.performance.orders_model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Order {

    @SerializedName("amount")
    private Double mGrandTotal;
    @SerializedName("order_date")
    private String mOrderDate;
    @SerializedName("id")
    private Long mOrderId;
    @SerializedName("products")
    private List<Product> mProducts;

    public Double getGrandTotal() {
        return mGrandTotal;
    }

    public void setGrandTotal(Double grandTotal) {
        mGrandTotal = grandTotal;
    }

    public String getOrderDate() {
        return mOrderDate;
    }

    public void setOrderDate(String orderDate) {
        mOrderDate = orderDate;
    }

    public Long getOrderId() {
        return mOrderId;
    }

    public void setOrderId(Long orderId) {
        mOrderId = orderId;
    }

    public List<Product> getProducts() {
        return mProducts;
    }

    public void setProducts(List<Product> products) {
        mProducts = products;
    }

}
