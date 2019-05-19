package com.humaclab.selliscope_myone.dbmodel;

/**
 * Created by dipu_ on 4/22/2017.
 */

public class Target {
    private String targetType, targetTimePeriod;
    private int targetCount, targetAchieved;

    public Target(String targetType, String targetTimePeriod, int targetCount, int targetAchieved) {
        this.targetType = targetType;
        this.targetTimePeriod = targetTimePeriod;
        this.targetCount = targetCount;
        this.targetAchieved = targetAchieved;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetTimePeriod() {
        return targetTimePeriod;
    }

    public void setTargetTimePeriod(String targetTimePeriod) {
        this.targetTimePeriod = targetTimePeriod;
    }

    public int getTargetCount() {
        return targetCount;
    }

    public void setTargetCount(int targetCount) {
        this.targetCount = targetCount;
    }

    public int getTargetAchieved() {
        return targetAchieved;
    }

    public void setTargetAchieved(int targetAchieved) {
        this.targetAchieved = targetAchieved;
    }
}
