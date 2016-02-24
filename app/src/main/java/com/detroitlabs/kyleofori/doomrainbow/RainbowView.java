package com.detroitlabs.kyleofori.doomrainbow;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class RainbowView extends FrameLayout {

    private static final float DEFAULT_BACKGROUND_START_ANGLE = 135;
    private static final float DEFAULT_BACKGROUND_SWEEP_ANGLE = 270;
    private static final float DEFAULT_GOAL_ANGLE = 330;
    private static final float DEFAULT_CURRENT_LEVEL_ANGLE = 160;
    private static final float DEFAULT_GOAL_ARC_LENGTH_DEGREES = 0;
    private static final long DEFAULT_ANIMATION_DURATION = 2000;
    private static final String DEFAULT_CENTER_TEXT = "¡Hola!";
    private static final String DEFAULT_CURRENT_LEVEL_TEXT = "30%";
    private static final int DEFAULT_ARC_WIDTH = 20;
    private static final String DEFAULT_MIN_VALUE = "E";
    private static final String DEFAULT_MAX_VALUE = "F";
    private static final int DEFAULT_LABEL_COLOR = Color.GRAY;
    private static final int DEFAULT_CIRCLE_COLOR = Color.GRAY;
    private static final int DEFAULT_GOAL_ARC_COLOR = Color.GREEN;
    private static final int DEFAULT_CURRENT_ARC_COLOR = Color.BLUE;
    private int circleColor, labelColor;
    private String centerText, currentLevelText;
    private String minString, maxString;
    private Paint paint;
    private RectF rectF, inscribedRectF;
    private ExtremeValue minValue, maxValue;
    private float backgroundStartAngle, backgroundSweepAngle, goalAngle, currentLevelAngle;
    private float goalArcSweepAngle;
    public boolean hasExtremeValues;
    public boolean hasChangeButtons;
    public boolean hasGoalIndicator;
    public boolean hasCurrentLevelText;
    private float radius;
    private float viewWidthHalf;
    private float viewHeightHalf;
    private float valueToDraw;
    private boolean animated;
    private long animationDuration = DEFAULT_ANIMATION_DURATION;
    private ValueAnimator animation;


    public RainbowView(Context context) {
        super(context);
    }

    public RainbowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RainbowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        setAnimated(true);
        setSaveEnabled(true);
        this.setWillNotDraw(false);
        paint = new Paint();
        rectF = new RectF();
        inscribedRectF = new RectF();
        minValue = new ExtremeValue();
        maxValue = new ExtremeValue();
        setBackgroundStartAngle(DEFAULT_BACKGROUND_START_ANGLE);
        setBackgroundSweepAngle(DEFAULT_BACKGROUND_SWEEP_ANGLE);
        setGoalAngle(DEFAULT_GOAL_ANGLE);
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

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        viewWidthHalf = getMeasuredWidth() / 2;
        viewHeightHalf = getMeasuredHeight() / 2;

        if(viewHeightHalf > viewWidthHalf) {
            radius = viewWidthHalf - 70;
        } else {
            radius = viewHeightHalf - 70;
        }

        inscribedRectF.set(
                viewWidthHalf - (float) (radius * Math.cos(Math.PI / 6)),
                viewHeightHalf - (float) (radius * Math.sin(Math.PI / 6)),
                viewWidthHalf + (float) (radius * Math.cos(Math.PI / 6)),
                viewHeightHalf + (float) (radius * Math.sin(Math.PI / 6))
        );

        final int count = getChildCount();

        if(count != 2) {
            throw new IllegalStateException("You may only add two children to this view.");
        }

        int currentChildWidth, currentChildHeight, currentChildLeft, currentChildTop;

        final int childrenSpaceLeft = Math.round(inscribedRectF.left);
        final int childrenSpaceTop = Math.round(inscribedRectF.top);
        final int childrenSpaceRight = Math.round(inscribedRectF.right);
        final int childrenSpaceBottom = Math.round(inscribedRectF.bottom);
        final int childrenSpaceWidth = childrenSpaceRight - childrenSpaceLeft;
        final int childrenSpaceHeight = childrenSpaceBottom - childrenSpaceTop;

        currentChildLeft = childrenSpaceLeft;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if(child.getVisibility() == GONE) {
                return;
            }

            child.measure(MeasureSpec.makeMeasureSpec(childrenSpaceWidth, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(childrenSpaceHeight, MeasureSpec.AT_MOST));
            currentChildWidth = child.getMeasuredWidth();
            currentChildHeight = child.getMeasuredHeight();
            currentChildTop = (childrenSpaceTop + childrenSpaceBottom) / 2 - currentChildHeight/2;
            if(i == 1) {
                currentChildLeft = childrenSpaceRight - currentChildWidth;
            }

            if(currentChildLeft + currentChildWidth > childrenSpaceRight) {
                throw new IllegalStateException("A button is being laid out beyond the right boundary.");
            }

            child.layout(
                    currentChildLeft,
                    currentChildTop,
                    currentChildLeft + currentChildWidth,
                    currentChildTop + currentChildHeight
            );
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int count = getChildCount();

        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            maxWidth += Math.max(maxWidth, child.getMeasuredWidth());
            maxHeight = Math.max(maxHeight, child.getMeasuredHeight());

            childState = combineMeasuredStates(childState, child.getMeasuredState());
        }

        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        rectF.set(viewWidthHalf - radius, viewHeightHalf - radius, viewWidthHalf + radius, viewHeightHalf + radius);

        float yCoordText = viewHeightHalf + radius;

        initBackgroundArcPaint();

        canvas.drawArc(rectF, backgroundStartAngle, backgroundSweepAngle, false, paint);

        initCurrentLevelArcPaint();

        canvas.drawArc(rectF, backgroundStartAngle, valueToDraw - backgroundStartAngle, false, paint);

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
            float textSizeValue;
            if(viewHeightHalf > viewWidthHalf) {
                textSizeValue = 50;
            } else {
                textSizeValue = 40;
            }

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
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.currentLevelAngle = currentLevelAngle;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        currentLevelAngle = ss.currentLevelAngle;
        valueToDraw = currentLevelAngle;
    }

    private void drawValue(Canvas canvas, ExtremeValue value) {
        canvas.drawText(value.getText(), value.getXCoordinate(), value.getYCoordinate(), paint);
    }

    private void initMinValue(float radius, float yCoordText) {
        float minValRadiusCosCoefficient = getRadiusCosineCoefficient(backgroundStartAngle - 15);
        float floatViewWidthHalf = (float) this.getMeasuredWidth()/2;
        float xCoordMinText = floatViewWidthHalf + minValRadiusCosCoefficient * radius;
        minValue.set(xCoordMinText, yCoordText, minString);
    }

    private void initMaxValue(float radius, float yCoordText) {
        float maxValRadiusCosCoefficient = getRadiusCosineCoefficient(backgroundStartAngle + backgroundSweepAngle + 15);
        float floatViewWidthHalf = (float) this.getMeasuredWidth()/2;
        float xCoordMaxText = floatViewWidthHalf + maxValRadiusCosCoefficient * radius;
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

    private void initCurrentLevelArcPaint() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(DEFAULT_ARC_WIDTH);
        paint.setColor(DEFAULT_CURRENT_ARC_COLOR);
    }

    public void initDefaultValues() {
        setHasExtremeValues(true);
        setHasChangeButtons(true);
        setHasGoalIndicator(true);
        setHasCurrentLevelText(true);
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

    public static float getDefaultGoalAngle() {
        return DEFAULT_GOAL_ANGLE;
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

    public float getBackgroundSweepAngle() {
        return backgroundSweepAngle;
    }

    public float getBackgroundStartAngle() {
        return backgroundStartAngle;
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
        float previousValue = this.currentLevelAngle;
        if(currentLevelAngle < DEFAULT_BACKGROUND_START_ANGLE) {
            this.currentLevelAngle = DEFAULT_BACKGROUND_START_ANGLE;
        } else if (currentLevelAngle > DEFAULT_BACKGROUND_START_ANGLE + DEFAULT_BACKGROUND_SWEEP_ANGLE) {
            this.currentLevelAngle = DEFAULT_BACKGROUND_START_ANGLE + DEFAULT_BACKGROUND_SWEEP_ANGLE;
        } else {
            this.currentLevelAngle = currentLevelAngle;
        }

        if(animation != null) {
            animation.cancel();
        }

        if(animated) {
            animation = ValueAnimator.ofFloat(previousValue, this.currentLevelAngle);

            animation.setDuration(animationDuration);
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    valueToDraw = (float) valueAnimator.getAnimatedValue();
                    RainbowView.this.invalidate();
                }
            });

            animation.start();
        } else {
            valueToDraw = this.currentLevelAngle;
        }
        
        invalidateAndRequestLayout();
    }

    public void setBackgroundStartAngle(float backgroundStartAngle) {
        this.backgroundStartAngle = backgroundStartAngle;
        invalidateAndRequestLayout();
    }

    public void setBackgroundSweepAngle(float backgroundSweepAngle) {
        this.backgroundSweepAngle = backgroundSweepAngle;
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

    public void setAnimationDuration(long animationDuration) {
        this.animationDuration = animationDuration;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public void invalidateAndRequestLayout() {
        invalidate();
        requestLayout();
    }

    private static class SavedState extends BaseSavedState {
        float currentLevelAngle;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentLevelAngle = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(currentLevelAngle);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
