package com.easyopstech.easyops.model.thana;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class Thana implements Serializable {

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private int id;

    @SerializedName("district_id")
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Thana thana = (Thana) o;
        return id == thana.id &&
                districtId == thana.districtId &&
                Objects.equals(name, thana.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id, districtId);
    }
}