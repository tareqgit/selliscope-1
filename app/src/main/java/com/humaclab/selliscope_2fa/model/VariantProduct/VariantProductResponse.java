package com.humaclab.selliscope_2fa.model.VariantProduct;

import com.google.gson.annotations.SerializedName;

/**
 * Created by leon on 9/3/2017.
 */

public class VariantProductResponse {

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