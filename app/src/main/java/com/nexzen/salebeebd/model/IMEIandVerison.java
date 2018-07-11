package com.nexzen.salebeebd.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leon on 10/12/17.
 */

public class IMEIandVerison implements Serializable {
    @SerializedName("IMEI_code")
    private String IMEIcode;

    @SerializedName("app_version")
    private String appVersion;

    public String getIMEIcode() {
        return IMEIcode;
    }

    public void setIMEIcode(String IMEIcode) {
        this.IMEIcode = IMEIcode;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
}
