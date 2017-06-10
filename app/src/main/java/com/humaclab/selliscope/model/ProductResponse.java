package com.humaclab.selliscope.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tonmoy on 5/6/17.
 */

public class ProductResponse {
    @SerializedName("error")
    public boolean error;
    @SerializedName("result")
    public Products result;

    public class Products {
        @SerializedName("products")
        public List<ProductResult> productResults;
    }

    public static class ProductResult {
        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String name;
        @SerializedName("price")
        public String price;
        @SerializedName("img")
        public String img;
        @SerializedName("category")
        public Category category;
        @SerializedName("brand")
        public Brand brand;

        @SerializedName("discount")
        public int discount;
        @SerializedName("promotion")
        public Promotion promotion;
        @SerializedName("pitch")
        public Pitch pitch;

        public static class Promotion {
            @SerializedName("discount")
            public String discount;
            @SerializedName("qty")
            public int qty;
        }

        public static class Pitch {
            @SerializedName("usp")
            public String usp;
            @SerializedName("tips")
            public String tips;
        }
    }

    public static class Category {
        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String name;
    }

    public static class Brand {
        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String name;
    }
}
