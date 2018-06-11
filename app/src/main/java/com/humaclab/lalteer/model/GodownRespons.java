package com.humaclab.lalteer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by leon on 8/31/17.
 */

public class GodownRespons implements Serializable {
    @SerializedName("error")
    private boolean error;
    @SerializedName("result")
    private Result result;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public static class Result implements Serializable {
        @SerializedName("godowns")
        private List<Godown> godownList;

        public List<Godown> getGodownList() {
            return godownList;
        }

        public void setGodownList(List<Godown> godownList) {
            this.godownList = godownList;
        }
    }

    public static class Godown implements Serializable {
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
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
