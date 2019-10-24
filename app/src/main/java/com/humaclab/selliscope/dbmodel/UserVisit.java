package com.humaclab.selliscope.dbmodel;

/**
 * Created by dipu_ on 4/22/2017.
 */

public class UserVisit {
    private double latitude, longitude;
    private String timeStamp, battery_status;
    private int visitId = -1;

    public UserVisit(double latitude, double longitude, String timeStamp, String battery_status) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeStamp = timeStamp;
        this.battery_status= battery_status;
    }

    public UserVisit(double latitude, double longitude, String timeStamp, int visitId, String battery_status) {
        this(latitude, longitude, timeStamp, battery_status);
        this.visitId = visitId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getVisitId() {
        return visitId;
    }

    public void setVisitId(int visitId) {
        this.visitId = visitId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getBattery_status() {
        return battery_status;
    }

    public void setBattery_status(String battery_status) {
        this.battery_status = battery_status;
    }
}
