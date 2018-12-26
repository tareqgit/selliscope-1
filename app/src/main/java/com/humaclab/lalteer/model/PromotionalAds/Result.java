package com.humaclab.lalteer.model.PromotionalAds;

import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("id")
    private Integer id;
    @SerializedName("title")
    private String title;
    @SerializedName("image")
    private String image;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}