package com.humaclab.akij_selliscope.model.Target;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OutletTarget implements Serializable {
    @SerializedName("error")
    private Boolean error;

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    @SerializedName("result")
    private List<Result> result;
    @SerializedName("line")
    private String line;
    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }



    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }



    public class Result {

        public String getSlab() {
            return slab;
        }

        public void setSlab(String slab) {
            this.slab = slab;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getAchieved() {
            return achieved;
        }

        public void setAchieved(String achieved) {
            this.achieved = achieved;
        }

        @SerializedName("slab")
        private String slab;

        @SerializedName("target")
        private String target;

        @SerializedName("achieved")
        private String achieved;

    }
}
