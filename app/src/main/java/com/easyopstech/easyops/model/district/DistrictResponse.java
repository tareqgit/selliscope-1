package com.easyopstech.easyops.model.district;
import com.google.gson.annotations.SerializedName;

public class DistrictResponse {

    @SerializedName("result")
    private com.easyopstech.easyops.model.district.Result result;

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