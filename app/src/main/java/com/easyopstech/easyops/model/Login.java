package com.easyopstech.easyops.model;

import com.google.gson.annotations.SerializedName;

/**
 * Updated by Leon on 9/3/2017.
 */

public class Login {
    public static class Successful {
        @SerializedName("error")
        public boolean error;

        @SerializedName("result")
        public LoginResult result;

        public class LoginResult {
            @SerializedName("user")
            public User user;

            @SerializedName("client_id")
            public String clientId;

            @SerializedName("module")
            public String[] modules;

            @SerializedName("theme")
            public Theme theme;
        }

        public class User {
            @SerializedName("name")
            public String name;

            @SerializedName("dob")
            public String dob;

            @SerializedName("gender")
            public String gender;

            @SerializedName("profile_image")
            public String profilePictureUrl;
        }

        public class Theme {
            @SerializedName("color")
            public String color;
        }
    }
}
