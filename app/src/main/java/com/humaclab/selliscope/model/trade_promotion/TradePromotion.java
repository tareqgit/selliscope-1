package com.humaclab.selliscope.model.trade_promotion;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TradePromotion {

    @SerializedName("error")

    private Boolean error;
    @SerializedName("result")

    private List<Result> result = null;

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