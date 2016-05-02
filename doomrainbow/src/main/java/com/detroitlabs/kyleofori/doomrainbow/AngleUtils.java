package com.detroitlabs.kyleofori.doomrainbow;

public final class AngleUtils {

    public static float getRadiusCosineCoefficient(final float valuePositionInDegrees) {
        final double valuePositionInRadians = Math.toRadians((double) valuePositionInDegrees);
        return (float) Math.cos(valuePositionInRadians);
    }

    public static float getInteriorAngleFromValue(
            final float value,
            final float exteriorStartValue,
            final float exteriorEndValue,
            final float exteriorStartAngle,
            final float exteriorSweepAngle) {

        if (exteriorStartValue < exteriorEndValue) {
            if(value <= exteriorStartValue) {
                return exteriorStartAngle;
            } else if (value >= exteriorEndValue) {
                return exteriorStartAngle + exteriorSweepAngle;
            } else {
                final float exteriorValueRange = Math.abs(exteriorEndValue - exteriorStartValue);
                final float valueDifference = Math.abs(Math.max(value, exteriorStartValue) - Math.min(value, exteriorStartValue));
                return ((valueDifference/exteriorValueRange) * exteriorSweepAngle) + exteriorStartAngle;
            }
        } else {
            if(value >= exteriorStartValue) {
                return exteriorStartAngle;
            } else if (value <= exteriorEndValue) {
                return exteriorStartAngle + exteriorSweepAngle;
            } else {
                final float exteriorValueRange = Math.abs(exteriorEndValue - exteriorStartValue);
                final float valueDifference = Math.abs(Math.max(value, exteriorStartValue) - Math.min(value, exteriorStartValue));
                return ((valueDifference/exteriorValueRange) * exteriorSweepAngle) + exteriorStartAngle;
            }
        }

    }

    private AngleUtils() {
        // This constructor intentionally left blank
    }

}
