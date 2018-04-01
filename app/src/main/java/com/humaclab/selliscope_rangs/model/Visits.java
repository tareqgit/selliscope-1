package com.humaclab.selliscope_rangs.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Miaki on 3/23/17.
 */

public class Visits {
    public static class Successful {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public VisitsResult visitsResult;

        public class VisitsResult {
            @SerializedName("outlet")
            public List<Outlet> outlets;
        }

        public class Outlet {
            @SerializedName("latitude")
            public double latitude;
            @SerializedName("longitude")
            public double longitude;
            @SerializedName("created_at")
            public String createdAt;
        }
    }

    public static class Unsuccessful {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public String result;
    }

}
