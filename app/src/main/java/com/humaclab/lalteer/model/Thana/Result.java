package com.humaclab.lalteer.model.Thana;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("thana")
    private List<Thana> thana;

    public List<Thana> getThana() {
        return thana;
    }

    public void setThana(List<Thana> thana) {
        this.thana = thana;
    }
}