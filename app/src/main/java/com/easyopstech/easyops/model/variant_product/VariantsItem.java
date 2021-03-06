package com.easyopstech.easyops.model.variant_product;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by leon on 9/3/2017.
 */

public class VariantsItem {

    @SerializedName("variant_details")
    private List<VariantDetailsItem> variantDetailsItems;

    public List<VariantDetailsItem> getVariantDetailsItems() {
        return variantDetailsItems;
    }

    public void setVariantDetailsItems(List<VariantDetailsItem> variantDetailsItems) {
        this.variantDetailsItems = variantDetailsItems;
    }
}
