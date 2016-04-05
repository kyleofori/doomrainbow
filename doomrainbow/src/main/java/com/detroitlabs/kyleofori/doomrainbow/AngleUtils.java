package com.detroitlabs.kyleofori.doomrainbow;

public final class AngleUtils {

    public static double convertToRadians(final double degree) {
        return (degree % 360) * Math.PI / 180;
    }

    public static float getRadiusCosineCoefficient(final float valuePositionInDegrees) {
        final double valuePositionInRadians = AngleUtils.convertToRadians((double) valuePositionInDegrees);
        return (float) Math.cos(valuePositionInRadians);
    }

    public static float convertFromValueToAngle(
            final float value,
            final float distanceBetweenExtremeAngles,
            final float distanceBetweenExtremeValues) {

        return value * distanceBetweenExtremeAngles / distanceBetweenExtremeValues - distanceBetweenExtremeAngles/2;
    }

    private AngleUtils() {
        // This constructor intentionally left blank
    }

}
