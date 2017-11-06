package com.humaclab.selliscope_nepal.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Nahid on 3/5/2017.
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
            @SerializedName("avatar")
            public String profilePictureUrl;
        }

        public class Theme {
            @SerializedName("color")
            public String color;
        }
    }

    public static class Unsuccessful {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public String result;
    }

}
