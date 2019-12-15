package com.humaclab.selliscope.dbmodel;

public class TradePromoionData {

    String promoionType;
    String promoionTitle;
    String promoionValue;
    String offerType;
    String offerValue;

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

    String freeProductName;
    String freeProductQty;
    String freeProductId;

    public String getFreeProductName() {
        return freeProductName;
    }

    public void setFreeProductName(String freeProductName) {
        this.freeProductName = freeProductName;
    }

    public String getFreeProductQty() {
        return freeProductQty;
    }

    public void setFreeProductQty(String freeProductQty) {
        this.freeProductQty = freeProductQty;
    }

    public String getFreeProductId() {
        return freeProductId;
    }

    public void setFreeProductId(String freeProductId) {
        this.freeProductId = freeProductId;
    }

    public TradePromoionData(String promoionType, String promoionTitle, String promoionValue, String offerType, String offerValue, String freeProductName, String freeProductQty, String freeProductId) {
        this.promoionType = promoionType;
        this.promoionTitle = promoionTitle;
        this.promoionValue = promoionValue;
        this.offerType = offerType;
        this.offerValue = offerValue;
        this.freeProductName = freeProductName;
        this.freeProductQty = freeProductQty;
        this.freeProductId = freeProductId;
    }

}
