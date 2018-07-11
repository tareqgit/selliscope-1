package com.nexzen.salebeebd.model.UpdatePassword;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leon on 8/3/18.
 */

public class ChangePasswordResponse implements Serializable {
    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
