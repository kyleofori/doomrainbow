package com.detroitlabs.kyleofori.doomrainbow;

public class AngleUtils {
    public static double convertToRadians(Double degree) {
        return (degree % 360) * Math.PI / 180;
    }
}
