
package com.sokrio.sokrio_classic.model.performance.orders_model;


import com.google.gson.annotations.SerializedName;


public class PerformanceOrderResponse {

    @SerializedName("error")
    private Boolean mError;
    @SerializedName("result")
    private Result mResult;
    @SerializedName("total_amount")
    private Double mTotalAmount;

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

    public Double getTotalAmount() {
        return mTotalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        mTotalAmount = totalAmount;
    }

}
