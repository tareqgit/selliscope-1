package com.humaclab.selliscope_rangs.model.promotion;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PromotionQuantityResponse implements Serializable {
    @SerializedName("result")
    private List<PromotionQuantityItem> promotionQuantityItems;

    @SerializedName("error")
    private boolean error;

    public List<PromotionQuantityItem> getPromotionQuantityItems() {
        return promotionQuantityItems;
    }

    public void setPromotionQuantityItems(List<PromotionQuantityItem> promotionQuantityItems) {
        this.promotionQuantityItems = promotionQuantityItems;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public static class PromotionQuantityItem implements Serializable {
        @SerializedName("zid")
        private int zid;

        @SerializedName("xpromo")
        private String promoName;

        @SerializedName("xline")
        private int xline;

        @SerializedName("xrowkit")
        private int xrowkit;

        @SerializedName("xqty")
        private String freeQuantity;

        @SerializedName("xitem")
        private String soldProductId;

        @SerializedName("xunitsel")
        private String unitType;

        @SerializedName("xdisc")
        private String discountAmount;

        @SerializedName("xcustype")
        private String customerType;

        @SerializedName("xstdprice")
        private String productPrice;

        @SerializedName("xcomp")
        private String freeProductId;

        @SerializedName("xunitalt")
        private String xunitalt;

        @SerializedName("xqtyoff")
        private String soldQuantity;

        @SerializedName("xdateeff")
        private String dateFrom;

        @SerializedName("xdateexp")
        private String dateTo;

        @SerializedName("xtr")
        private String territory;

        @SerializedName("xgcus")
        private String customer;

        @SerializedName("zactive")
        private String zactive;

        public int getZid() {
            return zid;
        }

        public void setZid(int zid) {
            this.zid = zid;
        }

        public String getPromoName() {
            return promoName;
        }

        public void setPromoName(String promoName) {
            this.promoName = promoName;
        }

        public int getXline() {
            return xline;
        }

        public void setXline(int xline) {
            this.xline = xline;
        }

        public int getXrowkit() {
            return xrowkit;
        }

        public void setXrowkit(int xrowkit) {
            this.xrowkit = xrowkit;
        }

        public String getFreeQuantity() {
            return freeQuantity;
        }

        public void setFreeQuantity(String freeQuantity) {
            this.freeQuantity = freeQuantity;
        }

        public String getSoldProductId() {
            return soldProductId;
        }

        public void setSoldProductId(String soldProductId) {
            this.soldProductId = soldProductId;
        }

        public String getUnitType() {
            return unitType;
        }

        public void setUnitType(String unitType) {
            this.unitType = unitType;
        }

        public String getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(String discountAmount) {
            this.discountAmount = discountAmount;
        }

        public String getDateTo() {
            return dateTo;
        }

        public void setDateTo(String dateTo) {
            this.dateTo = dateTo;
        }

        public String getCustomerType() {
            return customerType;
        }

        public void setCustomerType(String customerType) {
            this.customerType = customerType;
        }

        public String getProductPrice() {
            return productPrice;
        }

        public void setProductPrice(String productPrice) {
            this.productPrice = productPrice;
        }

        public String getFreeProductId() {
            return freeProductId;
        }

        public void setFreeProductId(String freeProductId) {
            this.freeProductId = freeProductId;
        }

        public String getXunitalt() {
            return xunitalt;
        }

        public void setXunitalt(String xunitalt) {
            this.xunitalt = xunitalt;
        }

        public String getTerritory() {
            return territory;
        }

        public void setTerritory(String territory) {
            this.territory = territory;
        }

        public String getCustomer() {
            return customer;
        }

        public void setCustomer(String customer) {
            this.customer = customer;
        }

        public String getSoldQuantity() {
            return soldQuantity;
        }

        public void setSoldQuantity(String soldQuantity) {
            this.soldQuantity = soldQuantity;
        }

        public String getDateFrom() {
            return dateFrom;
        }

        public void setDateFrom(String dateFrom) {
            this.dateFrom = dateFrom;
        }

        public String getZactive() {
            return zactive;
        }

        public void setZactive(String zactive) {
            this.zactive = zactive;
        }
    }
}
