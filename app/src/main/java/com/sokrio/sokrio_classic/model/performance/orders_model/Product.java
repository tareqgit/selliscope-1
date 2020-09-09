
package com.sokrio.sokrio_classic.model.performance.orders_model;


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
