package com.humaclab.selliscope_mohammadi.model.OutletPrefixResponse;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by leon on 18/1/18.
 */

public class PrefixResult implements Serializable {

    @SerializedName("prefix")
    private List<PrefixItem> prefix;

    public List<PrefixItem> getPrefix() {
        return prefix;
    }

    public void setPrefix(List<PrefixItem> prefix) {
        this.prefix = prefix;
    }
}