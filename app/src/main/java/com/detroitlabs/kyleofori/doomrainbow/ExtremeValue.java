package com.detroitlabs.kyleofori.doomrainbow;

public class ExtremeValue {
    private float xCoordinate, yCoordinate;
    private String text;

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
}
