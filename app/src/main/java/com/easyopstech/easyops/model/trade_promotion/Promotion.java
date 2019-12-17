package com.easyopstech.easyops.model.trade_promotion;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Promotion {

    @SerializedName("promotion_title")
    private String promotionTitle;

    @SerializedName("promotion_type")
    private String promotionType;

    @SerializedName("promotion_value")

    private Double promotionValue;
    @SerializedName("offer_type")

    private String offerType;
    @SerializedName("offer_value")

    private Double offerValue;
    @SerializedName("offer_product")

    private List<OfferProduct> offerProduct;

    public String getPromotionTitle() {
        return promotionTitle;
    }

    public void setPromotionTitle(String promotionTitle) {
        this.promotionTitle = promotionTitle;
    }

    public String getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(String promotionType) {
        this.promotionType = promotionType;
    }

    public Double getPromotionValue() {
        return promotionValue;
    }

    public void setPromotionValue(Double promotionValue) {
        this.promotionValue = promotionValue;
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public Double getOfferValue() {
        return offerValue;
    }

    public void setOfferValue(Double offerValue) {
        this.offerValue = offerValue;
    }

    public List<OfferProduct> getOfferProduct() {
        return offerProduct;
    }

    public void setOfferProduct(List<OfferProduct> offerProduct) {
        this.offerProduct = offerProduct;
    }

}
