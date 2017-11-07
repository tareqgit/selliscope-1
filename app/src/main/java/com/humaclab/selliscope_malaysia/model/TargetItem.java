package com.humaclab.selliscope_malaysia.model;

/**
 * Created by Nahid on 3/6/2017.
 */

public class TargetItem {
    private String targetLabel;
    private String targetCompleted;
    private String targetRemaining;
    private int targetImageId;

    public TargetItem(String targetLabel, String targetCompleted,
                      String targetRemaining, int targetImageId) {
        this.targetLabel = targetLabel;
        this.targetCompleted = targetCompleted;
        this.targetRemaining = targetRemaining;
        this.targetImageId = targetImageId;
    }

    public String getTargetLabel() {
        return targetLabel;
    }

    public String getTargetCompleted() {
        return targetCompleted;
    }

    public String getTargetRemaining() {
        return targetRemaining;
    }

    public int getTargetImageId() {
        return targetImageId;
    }
}
