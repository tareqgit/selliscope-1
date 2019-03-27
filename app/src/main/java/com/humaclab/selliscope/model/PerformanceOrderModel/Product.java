
package com.humaclab.selliscope.model.PerformanceOrderModel;


import com.google.gson.annotations.SerializedName;


public class Product {

    @SerializedName("amount")
    private Long mAmount;
    @SerializedName("name")
    private String mName;
    @SerializedName("qty")
    private Long mQty;

    public Long getAmount() {
        return mAmount;
    }

    public void setAmount(Long amount) {
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
