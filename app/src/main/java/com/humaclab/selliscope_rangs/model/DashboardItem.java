package com.humaclab.selliscope_rangs.model;

/**
 * Created by Miaki on 3/3/17.
 */

public class DashboardItem {
    private String itemName;
    private int itemImageId;

    public DashboardItem(String itemName, int itemImageId) {
        this.itemName = itemName;
        this.itemImageId = itemImageId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemImageId() {
        return itemImageId;
    }

}