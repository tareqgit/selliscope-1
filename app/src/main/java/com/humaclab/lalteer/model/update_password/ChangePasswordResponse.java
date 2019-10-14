package com.humaclab.lalteer.model.update_password;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leon on 8/3/18.
 */

public class ChangePasswordResponse implements Serializable {
    @SerializedName("error")
    private boolean error;

    @SerializedName("result")
    private String result;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
