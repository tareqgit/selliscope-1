package com.humaclab.selliscope.model.Target;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OutletTarget implements Serializable {
    @SerializedName("error")

    private Boolean error;
    @SerializedName("result")

    private Result result;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public class Result {

        @SerializedName("target_type")

        private String targetType;
        @SerializedName("sales_target")

        private String salesTarget;
        @SerializedName("achieved")

        private String achieved;
        @SerializedName("visited")

        private Integer visited;

        public String getTargetType() {
            return targetType;
        }

        public void setTargetType(String targetType) {
            this.targetType = targetType;
        }

        public String getSalesTarget() {
            return salesTarget;
        }

        public void setSalesTarget(String salesTarget) {
            this.salesTarget = salesTarget;
        }

        public String getAchieved() {
            return achieved;
        }

        public void setAchieved(String achieved) {
            this.achieved = achieved;
        }

        public Integer getVisited() {
            return visited;
        }

        public void setVisited(Integer visited) {
            this.visited = visited;
        }

    }
}
