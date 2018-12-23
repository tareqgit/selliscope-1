package com.humaclab.lalteer.model.PromotionalAds;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PromotionalAds implements Serializable {
    @SerializedName("error")
    private Boolean error;
    @SerializedName("result")
    private List<Result> result ;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }


}
