package com.humaclab.selliscope.model.PriceVariation;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by leon on 9/3/2017.
 */

public class Result {

    @SerializedName("products")
    private List<Product> products;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}