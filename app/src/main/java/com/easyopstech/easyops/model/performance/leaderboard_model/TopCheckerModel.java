package com.easyopstech.easyops.model.performance.leaderboard_model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/***
 * Created by mtita on 30,April,2019.
 */
public class TopCheckerModel implements Serializable {
    @SerializedName("image_url")
    private  String image_url;
    @SerializedName("name")
    private String name;

    @SerializedName("pos")
    private  int pos;


    @SerializedName("no_outlet")
    private double no_outlet;

    public TopCheckerModel(String image_url, int pos, String name, double no_outlet) {
        this.image_url = image_url;
        this.name = name;
        this.pos = pos;
        this.no_outlet = no_outlet;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public double getNo_outlet() {
        return no_outlet;
    }

    public void setNo_outlet(double no_outlet) {
        this.no_outlet = no_outlet;
    }
}
