package com.humaclab.selliscope_kenya.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Miaki on 3/19/17.
 */

public class Thanas {
    public static class Successful {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public ThanaResult thanaResult;

        public class ThanaResult {
            @SerializedName("thana")
            public List<Thana> thanas;
        }

        public class Thana {
            @SerializedName("id")
            public int thanaId;
            @SerializedName("name")
            public String thanaName;
        }
    }

    public static class Unsuccessful {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public String result;
    }
}