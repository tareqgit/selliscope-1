package com.humaclab.akij_selliscope.model.OutletType;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Leon on 3/19/17.
 */

public class OutletTypeResponse {

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