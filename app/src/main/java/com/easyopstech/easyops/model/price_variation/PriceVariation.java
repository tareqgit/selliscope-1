package com.easyopstech.easyops.model.price_variation;

import com.google.gson.annotations.SerializedName;

public class PriceVariation {
    @SerializedName("variant_row")

    private Integer variantRow;
    @SerializedName("outlet_type_id")

    private Integer outletTypeId;
    @SerializedName("outlet_type")

    private String outletType;
    @SerializedName("start_range")

    private Double startRange;
    @SerializedName("end_range")

    private Double endRange;
    @SerializedName("price")

    private Double price;

    public Integer getVariantRow() {
        return variantRow;
    }

    public void setVariantRow(Integer variantRow) {
        this.variantRow = variantRow;
    }

    public Integer getOutletTypeId() {
        return outletTypeId;
    }

    public void setOutletTypeId(Integer outletTypeId) {
        this.outletTypeId = outletTypeId;
    }

    public String getOutletType() {
        return outletType;
    }

    public void setOutletType(String outletType) {
        this.outletType = outletType;
    }

    public Double getStartRange() {
        return startRange;
    }

    public void setStartRange(Double startRange) {
        this.startRange = startRange;
    }

    public Double getEndRange() {
        return endRange;
    }

    public void setEndRange(Double endRange) {
        this.endRange = endRange;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
