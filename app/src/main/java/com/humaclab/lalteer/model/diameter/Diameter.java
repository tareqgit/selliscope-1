package com.humaclab.lalteer.model.diameter;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leon on 10/12/17.
 */

public class Diameter implements Serializable {

    @SerializedName("diameter")
    private int diameter;

    public int getDiameter() {
        return diameter;
    }

    public void setDiameter(int diameter) {
        this.diameter = diameter;
    }
}
