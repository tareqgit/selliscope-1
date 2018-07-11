package com.nexzen.salebeebd.model.RoutePlan;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RouteResponse implements Serializable {
    @SerializedName("result")
    private Result result;

    @SerializedName("error")
    private boolean error;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public static class Result implements Serializable {
        @SerializedName("route")
        private List<RouteItem> route;

        public List<RouteItem> getRoute() {
            return route;
        }

        public void setRoute(List<RouteItem> route) {
            this.route = route;
        }
    }

    public static class RouteItem implements Serializable {
        @SerializedName("name")
        private String name;

        @SerializedName("week-days")
        private List<String> weekDays;

        @SerializedName("id")
        private int id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getWeekDays() {
            return weekDays;
        }

        public void setWeekDays(List<String> weekDays) {
            this.weekDays = weekDays;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
