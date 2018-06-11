package com.humaclab.lalteer.model.Diameter;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leon on 10/12/17.
 */

public class DiameterResponse implements Serializable {

    @SerializedName("result")
    private Diameter diameter;

    @SerializedName("error")
    private boolean error;

    public Diameter getDiameter() {
        return diameter;
    }

    public void setDiameter(Diameter diameter) {
        this.diameter = diameter;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
