package com.nexzen.salebeebd.dbmodel;

/**
 * Created by dipu_ on 4/22/2017.
 */

public class UserVisit {
    private double latitude, longitude;
    private String timeStamp;
    private int visitId = -1;

    public UserVisit(double latitude, double longitude, String timeStamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeStamp = timeStamp;
    }

    public UserVisit(double latitude, double longitude, String timeStamp, int visitId) {
        this(latitude, longitude, timeStamp);
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
}
