package com.detroitlabs.kyleofori.doomrainbow;

public class AngleUtils {
    public static double convertToRadians(double degree) {
        return (degree % 360) * Math.PI / 180;
    }

    public static float getRadiusCosineCoefficient(float valuePositionInDegrees) {
        double valuePositionInRadians = AngleUtils.convertToRadians((double) valuePositionInDegrees);
        return (float) Math.cos(valuePositionInRadians);
    }
}
