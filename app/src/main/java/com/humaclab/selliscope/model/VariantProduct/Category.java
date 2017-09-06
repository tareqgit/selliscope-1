package com.humaclab.selliscope.model.VariantProduct;

import com.google.gson.annotations.SerializedName;

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
}