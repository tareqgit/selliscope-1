package com.humaclab.lalteer.model.variant_product;

import com.google.gson.annotations.SerializedName;

/**
 * Created by leon on 9/15/17.
 */

public class Pitch {
    @SerializedName("usp")
    private String usp;
    @SerializedName("tips")
    private String tips;

    public String getUsp() {
        return usp;
    }

    public void setUsp(String usp) {
        this.usp = usp;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }
}
