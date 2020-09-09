package com.sokrio.sokrio_classic.model.variant_product;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Created by leon on 9/3/2017.
 */

public class Category {

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private String id;

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

    public Category() {
    }

    public Category(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name) &&
                Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }
}