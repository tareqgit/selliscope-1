
package com.humaclab.selliscope.model.performance.paymentsModel;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class PaymentsResponse {

    @SerializedName("error")
    private Boolean mError;
    @SerializedName("grand_amount")
    private Long mGrandAmount;
    @SerializedName("result")
    private Result mResult;

    public Boolean getError() {
        return mError;
    }

    public void setError(Boolean error) {
        mError = error;
    }

    public Long getGrandAmount() {
        return mGrandAmount;
    }

    public void setGrandAmount(Long grandAmount) {
        mGrandAmount = grandAmount;
    }

    public Result getResult() {
        return mResult;
    }

    public void setResult(Result result) {
        mResult = result;
    }

}
