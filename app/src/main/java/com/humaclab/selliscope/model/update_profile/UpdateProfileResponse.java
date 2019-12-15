package com.humaclab.selliscope.model.update_profile;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UpdateProfileResponse implements Serializable {
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

    public class Result implements Serializable {

        @SerializedName("user")
        private User user;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

    public class User implements Serializable {

        @SerializedName("image")
        private String image;

        @SerializedName("address")
        private Object address;

        @SerializedName("gender")
        private String gender;

        @SerializedName("dob")
        private String dob;

        @SerializedName("name")
        private String name;

        @SerializedName("email")
        private String email;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Object getAddress() {
            return address;
        }

        public void setAddress(Object address) {
            this.address = address;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
