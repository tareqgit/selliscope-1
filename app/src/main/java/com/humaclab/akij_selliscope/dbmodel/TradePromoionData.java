package com.humaclab.akij_selliscope.dbmodel;

public class TradePromoionData {
    public TradePromoionData(String promoionType, String promoionTitle, String promoionValue, String offerType, String offerValue, String product_name, String product_qty) {
        this.promoionType = promoionType;
        this.promoionTitle = promoionTitle;
        this.promoionValue = promoionValue;
        this.offerType = offerType;
        this.offerValue = offerValue;
        this.product_name = product_name;
        this.product_qty = product_qty;
    }

    public String getPromoionType() {
        return promoionType;
    }

    public void setPromoionType(String promoionType) {
        this.promoionType = promoionType;
    }

    public String getPromoionTitle() {
        return promoionTitle;
    }

    public void setPromoionTitle(String promoionTitle) {
        this.promoionTitle = promoionTitle;
    }

    public String getPromoionValue() {
        return promoionValue;
    }

    public void setPromoionValue(String promoionValue) {
        this.promoionValue = promoionValue;
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public String getOfferValue() {
        return offerValue;
    }

    public void setOfferValue(String offerValue) {
        this.offerValue = offerValue;
    }

    String promoionType;
    String promoionTitle;
    String promoionValue;
    String offerType;
    String offerValue;

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_qty() {
        return product_qty;
    }

    public void setProduct_qty(String product_qty) {
        this.product_qty = product_qty;
    }

    String product_name;
    String product_qty;
}
