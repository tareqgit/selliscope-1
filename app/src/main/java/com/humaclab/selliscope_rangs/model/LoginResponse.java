package com.humaclab.selliscope_rangs.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leon on 19/12/17.
 */

public class LoginResponse implements Serializable {
    @SerializedName("error")
    private boolean error;

    @SerializedName("result")
    private LoginResult loginResult;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public LoginResult getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(LoginResult loginResult) {
        this.loginResult = loginResult;
    }

    public static class LoginInformation {
        @SerializedName("email")
        private String email;

        @SerializedName("password")
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public class LoginResult {
        @SerializedName("user")
        private LoginUser loginUser;

        public LoginUser getLoginUser() {
            return loginUser;
        }

        public void setLoginUser(LoginUser loginUser) {
            this.loginUser = loginUser;
        }
    }

    public class LoginUser {
        @SerializedName("name")
        private String name;

        @SerializedName("time_from")
        private String timeFrom;

        @SerializedName("time_to")
        private String timeTo;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTimeFrom() {
            return timeFrom;
        }

        public void setTimeFrom(String timeFrom) {
            this.timeFrom = timeFrom;
        }

        public String getTimeTo() {
            return timeTo;
        }

        public void setTimeTo(String timeTo) {
            this.timeTo = timeTo;
        }
    }
}
