package com.sokrio.sokrio_classic.model.performance.leaderboard_model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/***
 * Created by mtita on 30,April,2019.
 */
public class TopSellerModel implements Serializable {
    @SerializedName("image_url")
    private  String image_url;
    @SerializedName("pos")
    private  int pos;
    @SerializedName("name")
    private String name;
    @SerializedName("amount")
    private double amount;

    public TopSellerModel(String image_url, int pos, String name, double amount) {
        this.image_url = image_url;
        this.pos = pos;
        this.name = name;
        this.amount = amount;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
