package com.humaclab.selliscope_myone.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tonmoy on 5/7/17.
 */

public class CategoryResponse {
    @SerializedName("error")
    public boolean error;
    @SerializedName("result")
    public Categories result;

    public class Categories {
        @SerializedName("categories")
        public List<CategoryResult> categoryResults;
    }

    public class CategoryResult {
        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String name;
    }
}
