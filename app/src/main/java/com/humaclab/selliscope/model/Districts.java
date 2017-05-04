package com.humaclab.selliscope.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miaki on 3/19/17.
 */

public class Districts {
    public static class Successful {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public DistrictResult districtResult;

        public class DistrictResult {
            @SerializedName("district")
            public List<District> districts;
        }

        public class District {
            @SerializedName("id")
            public int districtId;
            @SerializedName("name")
            public String districtName;
        }
    }

    public static class Unsuccessful {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public String result;
    }
}
