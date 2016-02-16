package com.detroitlabs.kyleofori.doomrainbow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class RainbowView extends View {

    private static final float DEFAULT_BACKGROUND_START_ANGLE = 135;
    private static final float DEFAULT_BACKGROUND_SWEEP_ANGLE = 270;
    private static final float DEFAULT_OPTIONAL_GOAL_ANGLE = 220;
    private static final float DEFAULT_CURRENT_LEVEL_ANGLE = 160;
    private static final float DEFAULT_GOAL_ARC_LENGTH_DEGREES = 20;
    private static final String DEFAULT_CENTER_TEXT = "¡Hola!";
    private static final String DEFAULT_CURRENT_LEVEL_TEXT = "30%";
    private static final int DEFAULT_ARC_WIDTH = 20;
    private static final String DEFAULT_MIN_VALUE = "E";
    private static final String DEFAULT_MAX_VALUE = "F";
    private static final int DEFAULT_LABEL_COLOR = Color.GRAY;
    private static final int DEFAULT_CIRCLE_COLOR = Color.GRAY;
    private static final int DEFAULT_GOAL_ARC_COLOR = Color.GREEN;
    private int circleColor, labelColor;
    private String centerText, currentLevelText;
    private String minString, maxString;
    private Paint paint;
    private RectF rectF;
    private ExtremeValue minValue, maxValue;
    private float startAngle, sweepAngle, goalAngle, currentLevelAngle;
    private float goalArcSweepAngle;
    private boolean hasExtremeValues;
    private boolean hasChangeButtons;
    private boolean hasGoalIndicator;
    private boolean hasCurrentLevelText;


    public RainbowView(Context context) {
        super(context);
    }

    public RainbowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        rectF = new RectF();
        minValue = new ExtremeValue();
        maxValue = new ExtremeValue();
        setStartAngle(DEFAULT_BACKGROUND_START_ANGLE);
        setSweepAngle(DEFAULT_BACKGROUND_SWEEP_ANGLE);
        setGoalAngle(DEFAULT_OPTIONAL_GOAL_ANGLE);
        setMinString(DEFAULT_MIN_VALUE);
        setMaxString(DEFAULT_MAX_VALUE);
        setCenterText(DEFAULT_CENTER_TEXT);
        setCurrentLevelText(DEFAULT_CURRENT_LEVEL_TEXT);
        setCurrentLevelAngle(DEFAULT_CURRENT_LEVEL_ANGLE);
        setCircleColor(DEFAULT_CIRCLE_COLOR);
        setLabelColor(DEFAULT_LABEL_COLOR);
        setGoalArcSweepAngle(DEFAULT_GOAL_ARC_LENGTH_DEGREES);
        initDefaultValues();
    }

    public RainbowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw view
        //get half of width and height, since we're working with circle
        int viewWidthHalf = this.getMeasuredWidth()/2;
        int viewHeightHalf = this.getMeasuredHeight()/2;

        //set radius to be a little smaller than half of whatever side of display is shorter
        //and set text size
        int radius;
        float textSizeValue;
        if(viewHeightHalf > viewWidthHalf) {
            radius = viewWidthHalf - 70;
            textSizeValue = 50;
        } else {
            radius = viewHeightHalf - 70;
            textSizeValue = 40;
        }

        rectF.set(viewWidthHalf - radius, viewHeightHalf - radius, viewWidthHalf + radius, viewHeightHalf + radius);

        float floatViewHeightHalf = (float) viewHeightHalf;
        float floatRadius = (float) radius;
        float yCoordText = floatViewHeightHalf + floatRadius;


        initBackgroundArcPaint();

        canvas.drawArc(rectF, startAngle, sweepAngle, false, paint);

        initCenterTextPaint();

        canvas.drawText(centerText, viewWidthHalf, viewHeightHalf, paint);

        if(hasCurrentLevelText) {
            initCurrentLevelTextPaint();

            double doubleCurrentLevelAngle = (double) currentLevelAngle;
            double currentLevelAngleRadians = AngleUtils.convertToRadians(doubleCurrentLevelAngle);
            float currentLevelCosCoefficient = (float) Math.cos(currentLevelAngleRadians);
            float currentLevelSinCoefficient = (float) Math.sin(currentLevelAngleRadians);


            canvas.drawText(currentLevelText,viewWidthHalf + currentLevelCosCoefficient * radius * 1.25f,
                    viewHeightHalf + currentLevelSinCoefficient * radius * 1.25f, paint);
        }

        if(hasExtremeValues) {
            initValuePaint(textSizeValue);

            initMinValue(radius, yCoordText);
            drawValue(canvas, minValue);

            initMaxValue(radius, yCoordText);
            drawValue(canvas, maxValue);
        }

        if(hasGoalIndicator) {
            initGoalPaint();

            double doubleGoalAngle = (double) goalAngle;
            double goalAngleRadians = AngleUtils.convertToRadians(doubleGoalAngle);
            float goalCosCoefficient = (float) Math.cos(goalAngleRadians);
            float goalSinCoefficient = (float) Math.sin(goalAngleRadians);

            if(goalArcSweepAngle == 0) {
                canvas.drawPoint(viewWidthHalf + goalCosCoefficient * radius, viewHeightHalf + goalSinCoefficient * radius, paint);
            } else if (goalArcSweepAngle > 0) {
                canvas.drawArc(rectF, goalAngle - goalArcSweepAngle/2, goalArcSweepAngle, false, paint);
            }
        }

        if(hasChangeButtons) {
            initButtonsPaint();

            //draw the increase button circle...but is it a pressable object?????!!!
            drawIncreaseButtonCircle(canvas, viewWidthHalf + 2*radius/3, viewHeightHalf);
            //draw the decrease button circle

            //when up button is pressed, increase goal indicator
            //when down button is pressed, decrease goal indicator
            //if goal indicator is at high point, disable increase button
                //change its color
                //prevent it from having any effect
            //v.v.



        }
    }

    private void drawIncreaseButtonCircle(Canvas canvas, float buttonCenterXCoord, float buttonCenterYCoord) {
        canvas.drawCircle(buttonCenterXCoord, buttonCenterYCoord, DEFAULT_ARC_WIDTH, paint);
        paint.setColor(Color.WHITE);
        canvas.drawText("+", buttonCenterXCoord - 1, buttonCenterYCoord + DEFAULT_ARC_WIDTH - 2, paint);

    }

    private void drawValue(Canvas canvas, ExtremeValue value) {
        canvas.drawText(value.getText(), value.getXCoordinate(), value.getYCoordinate(), paint);
    }

    private void initMinValue(int radius, float yCoordText) {
        float minValRadiusCosCoefficient = getRadiusCosineCoefficient(startAngle - 15);
        float floatViewWidthHalf = (float) this.getMeasuredWidth()/2;
        float xCoordMinText = floatViewWidthHalf + minValRadiusCosCoefficient * (float) radius;
        minValue.set(xCoordMinText, yCoordText, minString);
    }

    private void initMaxValue(int radius, float yCoordText) {
        float maxValRadiusCosCoefficient = getRadiusCosineCoefficient(startAngle + sweepAngle + 15);
        float floatViewWidthHalf = (float) this.getMeasuredWidth()/2;
        float xCoordMaxText = floatViewWidthHalf + maxValRadiusCosCoefficient * (float) radius;
        maxValue.set(xCoordMaxText, yCoordText, maxString);
    }

    private float getRadiusCosineCoefficient(float valuePositionInDegrees) {
        double valuePositionInRadians = AngleUtils.convertToRadians((double) valuePositionInDegrees);
        return (float) Math.cos(valuePositionInRadians);
    }

    private void initBackgroundArcPaint() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(DEFAULT_ARC_WIDTH);
        paint.setColor(circleColor);
    }

    private void initCenterTextPaint() {
        paint.setColor(labelColor);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(50);
        paint.setStrokeWidth(0);
    }

    private void initCurrentLevelTextPaint() {
        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(false);
        paint.setTextSize(25);
    }

    private void initValuePaint(float textSizeValue) {
        paint.setTextSize(textSizeValue);
        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(true);
    }

    private void initGoalPaint() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(DEFAULT_ARC_WIDTH);
        paint.setColor(DEFAULT_GOAL_ARC_COLOR);
    }

    private void initButtonsPaint() {
        paint.setColor(0xFFEEEEEE);
        paint.setStyle(Paint.Style.FILL);
    }

    public void initDefaultValues() {
        setHasExtremeValues(true);
        setHasChangeButtons(true);
        setHasGoalIndicator(true);
        setHasCurrentLevelText(true);
    }

    public void increaseGoal() {
        setGoalAngle(goalAngle + 30);
    }

    public void decreaseGoal() {
        setGoalAngle(goalAngle - 30);
    }

    public int getCircleColor() {
        return circleColor;
    }

    public int getLabelColor() {
        return labelColor;
    }

    public String getCenterText() {
        return centerText;
    }

    public String getCurrentLevelText() {
        return currentLevelText;
    }

    public float getCurrentLevelAngle() {
        return currentLevelAngle;
    }

    public static int getDefaultArcWidth() {
        return DEFAULT_ARC_WIDTH;
    }

    public static float getDefaultBackgroundStartAngle() {
        return DEFAULT_BACKGROUND_START_ANGLE;
    }

    public static float getDefaultBackgroundSweepAngle() {
        return DEFAULT_BACKGROUND_SWEEP_ANGLE;
    }

    public static float getDefaultOptionalGoalAngle() {
        return DEFAULT_OPTIONAL_GOAL_ANGLE;
    }

    public static float getDefaultGoalArcLengthDegrees() {
        return DEFAULT_GOAL_ARC_LENGTH_DEGREES;
    }

    public float getGoalArcSweepAngle() {
        return goalArcSweepAngle;
    }

    public float getGoalAngle() {
        return goalAngle;
    }

    public float getSweepAngle() {
        return sweepAngle;
    }

    public float getStartAngle() {
        return startAngle;
    }

    public String getMinString() {
        return minString;
    }

    public String getMaxString() {
        return maxString;
    }

    public void setHasExtremeValues(boolean hasExtremeValues) {
        this.hasExtremeValues = hasExtremeValues;
    }

    public void setHasChangeButtons(boolean hasChangeButtons) {
        this.hasChangeButtons = hasChangeButtons;
    }

    public void setHasGoalIndicator(boolean hasGoalIndicator) {
        this.hasGoalIndicator = hasGoalIndicator;
    }

    public void setHasCurrentLevelText(boolean hasCurrentLevelText) {
        this.hasCurrentLevelText = hasCurrentLevelText;
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
        invalidateAndRequestLayout();
    }

    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
        invalidateAndRequestLayout();
    }

    public void setCenterText(String centerText) {
        this.centerText = centerText;
        invalidateAndRequestLayout();
    }

    public void setCurrentLevelText(String currentLevelText) {
        this.currentLevelText = currentLevelText;
        invalidateAndRequestLayout();
    }

    public void setCurrentLevelAngle(float currentLevelAngle) {
        this.currentLevelAngle = currentLevelAngle;
        invalidateAndRequestLayout();
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
        invalidateAndRequestLayout();
    }

    public void setSweepAngle(float sweepAngle) {
        this.sweepAngle = sweepAngle;
        invalidateAndRequestLayout();
    }

    public void setGoalArcSweepAngle(float goalArcSweepAngle) {
        this.goalArcSweepAngle = goalArcSweepAngle;
        invalidateAndRequestLayout();
    }

    public void setGoalAngle(float goalAngle) {
        this.goalAngle = goalAngle;
        invalidateAndRequestLayout();
    }

    public void setMaxString(String maxString) {
        this.maxString = maxString;
        invalidateAndRequestLayout();
    }

    public void setMinString(String minString) {
        this.minString = minString;
        invalidateAndRequestLayout();
    }

    public void invalidateAndRequestLayout() {
        invalidate();
        requestLayout();
    }
}
