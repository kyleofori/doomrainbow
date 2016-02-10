package com.detroitlabs.kyleofori.doomrainbow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class RainbowView extends View {

    private static final float DEFAULT_START_ANGLE = 135;
    private static final float DEFAULT_SWEEP_ANGLE = 270;
    private static final float DEFAULT_OPTIONAL_GOAL_ANGLE = 220;
    private static final float DEFAULT_GOAL_ARC_LENGTH_DEGREES = 20;
    private static final String DEFAULT_CIRCLE_TEXT = "¡Hola!";
    private static final int DEFAULT_ARC_WIDTH = 20;
    private static final String DEFAULT_MIN_VALUE = "E";
    private static final String DEFAULT_MAX_VALUE = "F";
    private static final int DEFAULT_LABEL_COLOR = Color.GRAY;
    private static final int DEFAULT_CIRCLE_COLOR = Color.GRAY;
    private static final int DEFAULT_GOAL_ARC_COLOR = Color.GREEN;
    private int circleColor, labelColor;
    private String centerText;
    private String minValue, maxValue;
    private Paint paint;
    private RectF rectF;
    private float startAngle, sweepAngle, goalAngle;
    private float goalArcSweepAngle;


    public RainbowView(Context context) {
        super(context);
    }

    public RainbowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        rectF = new RectF();
        setStartAngle(DEFAULT_START_ANGLE);
        setSweepAngle(DEFAULT_SWEEP_ANGLE);
        setGoalAngle(DEFAULT_OPTIONAL_GOAL_ANGLE);
        setMinValue(DEFAULT_MIN_VALUE);
        setMaxValue(DEFAULT_MAX_VALUE);
        setCenterText(DEFAULT_CIRCLE_TEXT);
        setCircleColor(DEFAULT_CIRCLE_COLOR);
        setLabelColor(DEFAULT_LABEL_COLOR);
        setGoalArcSweepAngle(DEFAULT_GOAL_ARC_LENGTH_DEGREES);
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
            radius = viewWidthHalf - 10;
            textSizeValue = 50;
        } else {
            radius = viewHeightHalf - 10;
            textSizeValue = 40;
        }

        rectF.set(viewWidthHalf - radius, viewHeightHalf - radius, viewWidthHalf + radius, viewHeightHalf + radius);

        initBackgroundArcPaint();

        canvas.drawArc(rectF, startAngle, sweepAngle, false, paint);

        initCenterTextPaint();

        canvas.drawText(centerText, viewWidthHalf, viewHeightHalf, paint);

        initValuePaint(textSizeValue);

        float floatViewWidthHalf = (float) viewWidthHalf;
        float floatViewHeightHalf = (float) viewHeightHalf;
        float floatRadius = (float) radius;
        double minValuePositionDegrees = (double) startAngle - 15;
        double minValuePositionRadians = AngleUtils.convertToRadians(minValuePositionDegrees);
        float radiusCosCoefficient = (float) Math.cos(minValuePositionRadians);

        float yCoordText = floatViewHeightHalf + floatRadius;

        float xCoordMinText = floatViewWidthHalf + radiusCosCoefficient * floatRadius;

        canvas.drawText(minValue, xCoordMinText, yCoordText, paint);

        double maxValuePositionDegrees = (double) startAngle + sweepAngle + 15;
        double maxValuePositionRadians = AngleUtils.convertToRadians(maxValuePositionDegrees);
        float maxValRadiusCosCoefficient = (float) Math.cos(maxValuePositionRadians);

        float xCoordMaxText = floatViewWidthHalf + maxValRadiusCosCoefficient * floatRadius;

        canvas.drawText(maxValue, xCoordMaxText, yCoordText, paint);

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

    public int getCircleColor() {
        return circleColor;
    }

    public int getLabelColor() {
        return labelColor;
    }

    public String getCenterText() {
        return centerText;
    }

    public static int getDefaultArcWidth() {
        return DEFAULT_ARC_WIDTH;
    }

    public static float getDefaultStartAngle() {
        return DEFAULT_START_ANGLE;
    }

    public static float getDefaultSweepAngle() {
        return DEFAULT_SWEEP_ANGLE;
    }

    public static float getDefaultOptionalGoalAngle() {
        return DEFAULT_OPTIONAL_GOAL_ANGLE;
    }

    public static float getDefaultGoalArcLengthDegrees() {
        return DEFAULT_GOAL_ARC_LENGTH_DEGREES;
    }

    public String getMinValue() {
        return minValue;
    }

    public String getMaxValue() {
        return maxValue;
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

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
        invalidateAndRequestLayout();

    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
        invalidateAndRequestLayout();
    }

    public void invalidateAndRequestLayout() {
        invalidate();
        requestLayout();
    }
}
