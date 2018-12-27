
package com.humaclab.lalteerdeaker.model.Products;

import com.google.gson.annotations.SerializedName;

public class ProductResponse {

    @SerializedName("error")
    private Boolean mError;
    @SerializedName("result")
    private Result mResult;

    public Boolean getError() {
        return mError;
    }

    public void setError(Boolean error) {
        mError = error;
    }

    public Result getResult() {
        return mResult;
    }

    public void setResult(Result result) {
        mResult = result;
    }

}
