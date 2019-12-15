package com.humaclab.selliscope.model.trade_promotion;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {
    @SerializedName("product_id")

    private Integer productId;
    @SerializedName("promotion")

    private List<Promotion> promotion = null;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public List<Promotion> getPromotion() {
        return promotion;
    }

    public void setPromotion(List<Promotion> promotion) {
        this.promotion = promotion;
    }
}
