package com.humaclab.lalteer.model.RoutePlan;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RouteDetailsResponse implements Serializable {
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

        @SerializedName("total_outlet")
        private int totalOutlet;

        @SerializedName("checked_outlet")
        private int checkedOutlet;

        @SerializedName("week-days")
        private List<String> weekDays;

        @SerializedName("outlet")
        private List<OutletItem> outletItemList;

        public int getTotalOutlet() {
            return totalOutlet;
        }

        public void setTotalOutlet(int totalOutlet) {
            this.totalOutlet = totalOutlet;
        }

        public int getCheckedOutlet() {
            return checkedOutlet;
        }

        public void setCheckedOutlet(int checkedOutlet) {
            this.checkedOutlet = checkedOutlet;
        }

        public List<String> getWeekDays() {
            return weekDays;
        }

        public void setWeekDays(List<String> weekDays) {
            this.weekDays = weekDays;
        }

        public List<OutletItem> getOutletItemList() {
            return outletItemList;
        }

        public void setOutletItemList(List<OutletItem> outletItemList) {
            this.outletItemList = outletItemList;
        }
    }

    public static class OutletItem implements Serializable {

        @SerializedName("owner")
        private String owner;

        @SerializedName("img")
        private String img;

        @SerializedName("address")
        private String address;

        @SerializedName("latitude")
        private double latitude;

        @SerializedName("type")
        private String type;

        @SerializedName("thana")
        private String thana;

        @SerializedName("checkIn")
        private String checkIn;

        @SerializedName("phone")
        private String phone;

        @SerializedName("credit_balance")
        private String creditBalance;

        @SerializedName("due")
        private String due;

        @SerializedName("district")
        private String district;

        @SerializedName("name")
        private String name;

        @SerializedName("credit_limit")
        private String creditLimit;

        @SerializedName("id")
        private int id;

        @SerializedName("longitude")
        private double longitude;

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getThana() {
            return thana;
        }

        public void setThana(String thana) {
            this.thana = thana;
        }

        public String getCheckIn() {
            return checkIn;
        }

        public void setCheckIn(String checkIn) {
            this.checkIn = checkIn;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCreditBalance() {
            return creditBalance;
        }

        public void setCreditBalance(String creditBalance) {
            this.creditBalance = creditBalance;
        }

        public String getDue() {
            return due;
        }

        public void setDue(String due) {
            this.due = due;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCreditLimit() {
            return creditLimit;
        }

        public void setCreditLimit(String creditLimit) {
            this.creditLimit = creditLimit;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}
