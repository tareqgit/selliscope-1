package com.humaclab.selliscope.model.variant_product;

import com.google.gson.annotations.SerializedName;

/**
 * Created by leon on 9/15/17.
 */

public class Promotion {
    @SerializedName("discount")
    private String discount;
    @SerializedName("qty")
    private int qty;

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
