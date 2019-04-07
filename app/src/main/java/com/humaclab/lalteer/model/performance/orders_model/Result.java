
/*
 * Created by Tareq Islam on 4/3/19 2:38 PM
 *
 *  Last modified 3/28/19 10:52 AM
 */

package com.humaclab.lalteer.model.performance.orders_model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


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
