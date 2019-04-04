
/*
 * Created by Tareq Islam on 4/3/19 2:38 PM
 *
 *  Last modified 3/28/19 10:52 AM
 */

package com.humaclab.lalteer.model.performance.paymentsModel;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class PaymentsResponse {

    @SerializedName("error")
    private Boolean mError;
    @SerializedName("grand_amount")
    private Double mGrandAmount;
    @SerializedName("result")
    private Result mResult;

    public Boolean getError() {
        return mError;
    }

    public void setError(Boolean error) {
        mError = error;
    }

    public Double getGrandAmount() {
        return mGrandAmount;
    }

    public void setGrandAmount(Double grandAmount) {
        mGrandAmount = grandAmount;
    }

    public Result getResult() {
        return mResult;
    }

    public void setResult(Result result) {
        mResult = result;
    }

}
