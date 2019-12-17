package com.easyopstech.easyops.model.trade_promotion;

import com.google.gson.annotations.SerializedName;

public class OfferProduct {

    @SerializedName("product_id")
    private Integer productId;
    @SerializedName("product_name")

    private String productName;
    @SerializedName("product_qty")

    private Integer productQty;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getProductQty() {
        return productQty;
    }

    public void setProductQty(Integer productQty) {
        this.productQty = productQty;
    }
}
