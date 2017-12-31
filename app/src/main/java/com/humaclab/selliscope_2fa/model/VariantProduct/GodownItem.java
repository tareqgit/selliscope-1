package com.humaclab.selliscope_2fa.model.VariantProduct;

import com.google.gson.annotations.SerializedName;

/**
 * Created by leon on 9/3/2017.
 */

public class GodownItem {

    @SerializedName("name")
    private String name;

    @SerializedName("stock")
    private String stock;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }
}