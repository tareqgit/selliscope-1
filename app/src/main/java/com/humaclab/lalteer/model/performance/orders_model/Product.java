
/*
 * Created by Tareq Islam on 4/3/19 2:38 PM
 *
 *  Last modified 3/28/19 10:52 AM
 */

package com.humaclab.lalteer.model.performance.orders_model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Product implements Serializable {

    @SerializedName("amount")
    private Double mAmount;
    @SerializedName("name")
    private String mName;
    @SerializedName("qty")
    private Long mQty;

    public Double getAmount() {
        return mAmount;
    }

    public void setAmount(Double amount) {
        mAmount = amount;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Long getQty() {
        return mQty;
    }

    public void setQty(Long qty) {
        mQty = qty;
    }

}
