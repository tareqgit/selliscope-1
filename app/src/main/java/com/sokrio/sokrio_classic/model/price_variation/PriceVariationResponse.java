package com.sokrio.sokrio_classic.model.price_variation;

import com.google.gson.annotations.SerializedName;


/**
 * Created by anam on 29/7/2018.
 */

public class PriceVariationResponse {

    @SerializedName("result")
    private Result result;

    @SerializedName("error")
    private boolean error;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}