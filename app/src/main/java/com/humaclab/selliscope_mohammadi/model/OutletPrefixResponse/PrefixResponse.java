package com.humaclab.selliscope_mohammadi.model.OutletPrefixResponse;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leon on 18/1/18.
 */

public class PrefixResponse implements Serializable {

    @SerializedName("result")
    private PrefixResult prefixResult;

    @SerializedName("error")
    private boolean error;

    public PrefixResult getPrefixResult() {
        return prefixResult;
    }

    public void setPrefixResult(PrefixResult prefixResult) {
        this.prefixResult = prefixResult;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}

