package com.humaclab.selliscope_mohammadi.model.OutletPrefixResponse;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leon on 18/1/18.
 */

public class PrefixItem implements Serializable {

    @SerializedName("prefix")
    private String prefix;

    @SerializedName("id")
    private int id;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}