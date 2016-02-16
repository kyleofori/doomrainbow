package com.detroitlabs.kyleofori.doomrainbow;

public class ExtremeValue {
    private float xCoordinate, yCoordinate;
    private String text;

    /**
     * Create a new empty ExtremeValue. Coordinates are initialized to 0, and text is a blank string.
     */
    public ExtremeValue() {
        this.xCoordinate = 0;
        this.yCoordinate = 0;
        this.text = "";
    }

    public ExtremeValue(float xCoordinate, float yCoordinate, String text) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.text = text;
    }

    public float getXCoordinate() {
        return xCoordinate;
    }

    public float getYCoordinate() {
        return yCoordinate;
    }

    public String getText() {
        return text;
    }

    public void set(float xCoordinate, float yCoordinate, String text) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.text = text;
    }
}
