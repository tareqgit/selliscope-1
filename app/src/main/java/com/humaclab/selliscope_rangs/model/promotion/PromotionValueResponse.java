package com.humaclab.selliscope_rangs.model.promotion;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PromotionValueResponse implements Serializable {
    @SerializedName("result")
    private List<PromotionValueItem> promotionValueItems;

    @SerializedName("error")
    private boolean error;

    public List<PromotionValueItem> getPromotionValueItems() {
        return promotionValueItems;
    }

    public void setPromotionValueItems(List<PromotionValueItem> promotionValueItems) {
        this.promotionValueItems = promotionValueItems;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public class PromotionValueItem implements Serializable {
        @SerializedName("zid")
        private int zid;

        @SerializedName("xpromo")
        private String promoName;

        @SerializedName("xcomp")
        private int freeProductId;

        @SerializedName("xline")
        private int xline;

        @SerializedName("xitem")
        private String soldProductId;

        @SerializedName("xunitsel")
        private String unitType;

        @SerializedName("xtypealc")
        private String discountType;

        @SerializedName("xdiscload")
        private String discountAmount;

        @SerializedName("xqtyoff")
        private String soldQuantity;

        @SerializedName("xdateeff")
        private String dateTo;

        @SerializedName("xdateexp")
        private String dateFrom;

        @SerializedName("xcustype")
        private String customerType;

        @SerializedName("xtr")
        private String territory;

        @SerializedName("xgcus")
        private String customer;

        @SerializedName("zactive")
        private String zactive;

        public int getFreeProductId() {
            return freeProductId;
        }

        public void setFreeProductId(int freeProductId) {
            this.freeProductId = freeProductId;
        }

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

        public String getDiscountType() {
            return discountType;
        }

        public void setDiscountType(String discountType) {
            this.discountType = discountType;
        }

        public String getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(String discountAmount) {
            this.discountAmount = discountAmount;
        }

        public String getSoldQuantity() {
            return soldQuantity;
        }

        public void setSoldQuantity(String soldQuantity) {
            this.soldQuantity = soldQuantity;
        }

        public String getDateTo() {
            return dateTo;
        }

        public void setDateTo(String dateTo) {
            this.dateTo = dateTo;
        }

        public String getDateFrom() {
            return dateFrom;
        }

        public void setDateFrom(String dateFrom) {
            this.dateFrom = dateFrom;
        }

        public String getCustomerType() {
            return customerType;
        }

        public void setCustomerType(String customerType) {
            this.customerType = customerType;
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

        public String getZactive() {
            return zactive;
        }

        public void setZactive(String zactive) {
            this.zactive = zactive;
        }
    }
}
