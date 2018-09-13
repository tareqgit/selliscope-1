package com.humaclab.selliscope.dbmodel;

public class TradePromoionData {
    public TradePromoionData(String promoionType, String promoionTitle, String promoionValue, String offerType, String offerValue) {
        this.promoionType = promoionType;
        this.promoionTitle = promoionTitle;
        this.promoionValue = promoionValue;
        this.offerType = offerType;
        this.offerValue = offerValue;
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
}
