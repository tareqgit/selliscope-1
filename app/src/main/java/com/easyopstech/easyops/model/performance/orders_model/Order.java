
package com.easyopstech.easyops.model.performance.orders_model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("grand_total")
    private Double mGrandTotal;
    @SerializedName("order_date")
    private String mOrderDate;
    @SerializedName("order_id")
    private Long mOrderId;

    @SerializedName("outlet_name")
    private String outletName;

    @SerializedName("products")
    private List<Product> mProducts;

    public Double getGrandTotal() {
        return mGrandTotal;
    }

    public void setGrandTotal(Double grandTotal) {
        mGrandTotal = grandTotal;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
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
