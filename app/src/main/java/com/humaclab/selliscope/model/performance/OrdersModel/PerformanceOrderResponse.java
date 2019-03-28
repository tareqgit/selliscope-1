
package com.humaclab.selliscope.model.performance.OrdersModel;


import com.google.gson.annotations.SerializedName;


public class PerformanceOrderResponse {

    @SerializedName("error")
    private Boolean mError;
    @SerializedName("result")
    private Result mResult;
    @SerializedName("total_amount")
    private Long mTotalAmount;

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

    public Long getTotalAmount() {
        return mTotalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        mTotalAmount = totalAmount;
    }

}
