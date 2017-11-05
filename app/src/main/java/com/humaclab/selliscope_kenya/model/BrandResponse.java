package com.humaclab.selliscope_kenya.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tonmoy on 5/7/17.
 */

public class BrandResponse {
    @SerializedName("error")
    public boolean error;
    @SerializedName("result")
    public Brands result;

    public class Brands {
        @SerializedName("brands")
        public List<BrandResult> brandResults;
    }

    public class BrandResult {
        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String name;
    }
}
