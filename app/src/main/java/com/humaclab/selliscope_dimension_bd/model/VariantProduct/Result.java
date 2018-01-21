package com.humaclab.selliscope_dimension_bd.model.VariantProduct;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by leon on 9/3/2017.
 */

public class Result {

    @SerializedName("variant")
    private List<VariantItem> variant;

    @SerializedName("products")
    private List<ProductsItem> products;

    public List<VariantItem> getVariant() {
        return variant;
    }

    public void setVariant(List<VariantItem> variant) {
        this.variant = variant;
    }

    public List<ProductsItem> getProducts() {
        return products;
    }

    public void setProducts(List<ProductsItem> products) {
        this.products = products;
    }
}