package com.humaclab.selliscope_mohammadi.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Nahid on 3/7/2017.
 */

public class Targets {
    public static class Successful {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public Targets.Successful.TargetResult targetResult;

        public class TargetResult {
            @SerializedName("daily")
            public List<Targets.Successful.Target> dailyTarget;
            @SerializedName("weekly")
            public List<Targets.Successful.Target> weeklyTarget;
            @SerializedName("monthly")
            public List<Targets.Successful.Target> monthlyTarget;
        }

        public class Target {
            @SerializedName("must-sales")
            public Sales mustSales;
            @SerializedName("priority-sales")
            public Sales prioritySales;
            @SerializedName("regular-sales")
            public Sales regularSales;
        }

        public class Sales {
            @SerializedName("target")
            public int target;
            @SerializedName("achieved")
            public int achieved;
        }
    }

    public static class Unsuccessful {
        @SerializedName("error")
        public boolean error;
        @SerializedName("result")
        public String result;
    }
}
