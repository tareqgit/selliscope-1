
package com.humaclab.selliscope.model.performance.OrdersModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;


public class Result {

    @SerializedName("orders")
    private List<Order> mOrders;

    public List<Order> getOrders() {
        return mOrders;
    }

    public void setOrders(List<Order> orders) {
        mOrders = orders;
    }

}
