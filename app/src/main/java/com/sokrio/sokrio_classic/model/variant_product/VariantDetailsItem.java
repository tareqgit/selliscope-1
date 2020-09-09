package com.sokrio.sokrio_classic.model.variant_product;

import com.google.gson.annotations.SerializedName;

/**
 * Created by leon on 9/6/2017.
 */

public class VariantDetailsItem {

    @SerializedName("variant_cat_id")
    private int variantCatId;

    @SerializedName("variant_cat_name")
    private String variantCatName;

    @SerializedName("name")
    private String name;

    @SerializedName("row")
    private String row;

    @SerializedName("stock")
    private String stock;

    public int getVariantCatId() {
        return variantCatId;
    }

    public void setVariantCatId(int variantCatId) {
        this.variantCatId = variantCatId;
    }

    public String getVariantCatName() {
        return variantCatName;
    }

    public void setVariantCatName(String variantCatName) {
        this.variantCatName = variantCatName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }
}