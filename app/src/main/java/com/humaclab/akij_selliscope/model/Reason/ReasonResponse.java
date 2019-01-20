
package com.humaclab.akij_selliscope.model.Reason;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReasonResponse {

    @SerializedName("error")

    private Boolean error;
    @SerializedName("result")

    private List<Result> result;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public static class Result {

        @SerializedName("id")

        private Integer id;
        @SerializedName("name")

        private String name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
}

