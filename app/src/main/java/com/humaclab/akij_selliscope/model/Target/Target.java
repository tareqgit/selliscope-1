package com.humaclab.akij_selliscope.model.Target;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Target {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("line")
    @Expose
    private String line;
    @SerializedName("result")
    @Expose
    private List<Result> result = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

}