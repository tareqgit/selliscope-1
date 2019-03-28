
package com.humaclab.selliscope.model.performance.OrdersModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("grand_total")
    private Long mGrandTotal;
    @SerializedName("order_date")
    private String mOrderDate;
    @SerializedName("order_id")
    private Long mOrderId;
    @SerializedName("products")
    private List<Product> mProducts;

    public Long getGrandTotal() {
        return mGrandTotal;
    }

    public void setGrandTotal(Long grandTotal) {
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
