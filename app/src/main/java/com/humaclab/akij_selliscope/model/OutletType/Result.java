package com.humaclab.akij_selliscope.model.OutletType;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Leon on 3/19/17.
 */

public class Result {

    @SerializedName("type")
    private List<OutletType> type;

    public List<OutletType> getType() {
        return type;
    }

    public void setType(List<OutletType> type) {
        this.type = type;
    }
}