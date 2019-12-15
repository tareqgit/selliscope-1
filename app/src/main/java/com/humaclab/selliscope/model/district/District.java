package com.humaclab.selliscope.model.district;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class District {

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private int id;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        District district = (District) o;
        return id == district.id &&
                Objects.equals(name, district.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }
}