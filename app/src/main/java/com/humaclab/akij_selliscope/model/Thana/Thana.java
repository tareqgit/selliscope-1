package com.humaclab.akij_selliscope.model.Thana;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Thana implements Serializable {

    @SerializedName("route_name")
    private String name;

    @SerializedName("id")
    private int id;

    @SerializedName("point_id")
    private int districtId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }
}