package com.humaclab.selliscope_malaysia.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Miaki on 3/19/17.
 */

public class OutletTypes {
    public static class Successful {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public OutletTypeResult outletTypeResult;

        public class OutletTypeResult {
            @SerializedName("type")
            public List<OutletType> outletTypes;
        }

        public class OutletType {
            @SerializedName("id")
            public int outletTypeId;
            @SerializedName("name")
            public String outletTypeName;
        }
    }

    public static class Unsuccessful {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public String result;
    }
}
