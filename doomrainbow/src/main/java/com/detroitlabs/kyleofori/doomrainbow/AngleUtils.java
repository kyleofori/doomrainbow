package com.detroitlabs.kyleofori.doomrainbow;

public final class AngleUtils {

    public static float getRadiusCosineCoefficient(final float valuePositionInDegrees) {
        final double valuePositionInRadians = Math.toRadians((double) valuePositionInDegrees);
        return (float) Math.cos(valuePositionInRadians);
    }

    private AngleUtils() {
        // This constructor intentionally left blank
    }

}
