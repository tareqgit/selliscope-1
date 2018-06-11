package com.humaclab.lalteer.model.OutletType;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Leon on 3/19/17.
 */

public class OutletType {

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
}