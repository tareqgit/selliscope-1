package com.humaclab.akij_selliscope.dbmodel;

public class PriceVariationStartEnd {
    private double startRange;
    private double endRange;
    private double priceRange;

    public PriceVariationStartEnd(double startRange, double endRange, double priceRange) {
        this.startRange = startRange;
        this.endRange = endRange;
        this.priceRange = priceRange;
    }

    public double getStartRange() {
        return startRange;
    }

    public void setStartRange(double startRange) {
        this.startRange = startRange;
    }

    public double getEndRange() {
        return endRange;
    }

    public void setEndRange(double endRange) {
        this.endRange = endRange;
    }

    public double getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(double priceRange) {
        this.priceRange = priceRange;
    }
}
