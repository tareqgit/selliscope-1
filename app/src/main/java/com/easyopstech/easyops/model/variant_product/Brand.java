package com.easyopstech.easyops.model.variant_product;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Created by leon on 9/3/2017.
 */

public class Brand {

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private String id;

    public Brand(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public Brand() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Brand brand = (Brand) o;
        return Objects.equals(name, brand.name) &&
                Objects.equals(id, brand.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }
}