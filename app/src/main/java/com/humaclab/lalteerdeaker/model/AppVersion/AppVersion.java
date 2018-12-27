package com.humaclab.lalteerdeaker.model.AppVersion;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AppVersion implements Serializable {

        @SerializedName("error")

        private Boolean error;
        @SerializedName("result")

        private Result result;

        public Boolean getError() {
            return error;
        }

        public void setError(Boolean error) {
            this.error = error;
        }

        public Result getResult() {
            return result;
        }

        public void setResult(Result result) {
            this.result = result;
        }

    public class Result {

        @SerializedName("version_code")

        private String versionCode;
        @SerializedName("version_name")

        private String versionName;
        @SerializedName("url")

        private String url;


        public String getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(String versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

}
