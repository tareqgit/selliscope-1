package com.easyopstech.easyops.model.district;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("district")
    private List<District> district;

    public List<District> getDistrict() {
        return district;
    }

    public void setDistrict(List<District> district) {
        this.district = district;
    }
}