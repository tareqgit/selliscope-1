package com.humaclab.akij_selliscope.model.Target;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("slab")
    @Expose
    private String slab;
    @SerializedName("target")
    @Expose
    private Integer target;
    @SerializedName("achieved")
    @Expose
    private Integer achieved;

    public String getSlab() {
        return slab;
    }

    public void setSlab(String slab) {
        this.slab = slab;
    }

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }

    public Integer getAchieved() {
        return achieved;
    }

    public void setAchieved(Integer achieved) {
        this.achieved = achieved;
    }

}