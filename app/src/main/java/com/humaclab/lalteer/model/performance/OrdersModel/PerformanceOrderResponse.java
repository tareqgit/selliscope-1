
/*
 * Created by Tareq Islam on 4/3/19 2:38 PM
 *
 *  Last modified 3/28/19 10:52 AM
 */

package com.humaclab.lalteer.model.performance.OrdersModel;


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
