package com.detroitlabs.kyleofori.doomrainbow;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class RainbowView extends FrameLayout {

    private static final Paint BASE_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint DEFAULT_BACKGROUND_ARC_PAINT = new Paint(BASE_PAINT);
    private static final Paint DEFAULT_CURRENT_LEVEL_TEXT_PAINT = new Paint(BASE_PAINT);
    private static final Paint DEFAULT_EXTREME_LABEL_TEXT_PAINT = new Paint(BASE_PAINT);
    private static final Paint DEFAULT_GOAL_PAINT = new Paint(BASE_PAINT);
    private static final Paint DEFAULT_CURRENT_LEVEL_ARC_PAINT = new Paint(BASE_PAINT);
    private static final float DEFAULT_BACKGROUND_START_ANGLE = -135;
    private static final float DEFAULT_BACKGROUND_END_ANGLE = 135;
    private static final float DEFAULT_BACKGROUND_EXTREME_LABEL_PADDING = 15;
    private static final float DEFAULT_GOAL_VALUE = 20;
    private static final float DEFAULT_GOAL_ARC_LENGTH = 4;
    private static final long DEFAULT_ANIMATION_DURATION = 2000;
    private static final String DEFAULT_CURRENT_LEVEL_TEXT = "30%";
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final Paint.Cap DEFAULT_ARC_STROKE_CAP = Paint.Cap.ROUND;
    private static final int DEFAULT_ARC_STROKE_WIDTH = 20;
    private static final float LEVEL_TEXT_RADIUS_SCALE_FACTOR = 1.25f;


    static {
        initDefaultBackgroundArcPaint();
        initDefaultCurrentLevelTextPaint();
        initDefaultExtremeLabelTextPaint();
        initDefaultGoalPaint();
        initDefaultCurrentLevelArcPaint();
    }

    private static void initDefaultBackgroundArcPaint() {
        DEFAULT_BACKGROUND_ARC_PAINT.setStyle(Paint.Style.STROKE);
        DEFAULT_BACKGROUND_ARC_PAINT.setStrokeCap(DEFAULT_ARC_STROKE_CAP);
        DEFAULT_BACKGROUND_ARC_PAINT.setStrokeWidth(DEFAULT_ARC_STROKE_WIDTH);
        DEFAULT_BACKGROUND_ARC_PAINT.setColor(Color.GRAY);
    }

    private static void initDefaultCurrentLevelTextPaint() {
        DEFAULT_CURRENT_LEVEL_TEXT_PAINT.setColor(Color.BLACK);
        DEFAULT_CURRENT_LEVEL_TEXT_PAINT.setFakeBoldText(false);
//        paint.setTextSize(25);
    }

    private static void initDefaultExtremeLabelTextPaint() {
//        paint.setTextSize(textSizeValue);
        DEFAULT_EXTREME_LABEL_TEXT_PAINT.setColor(Color.BLACK);
        DEFAULT_EXTREME_LABEL_TEXT_PAINT.setFakeBoldText(true);
        DEFAULT_EXTREME_LABEL_TEXT_PAINT.setTextAlign(Paint.Align.CENTER);
    }

    private static void initDefaultGoalPaint() {
        DEFAULT_GOAL_PAINT.setColor(Color.GREEN);
    }

    private static void initDefaultCurrentLevelArcPaint() {
        DEFAULT_CURRENT_LEVEL_ARC_PAINT.setStyle(Paint.Style.STROKE);
        DEFAULT_CURRENT_LEVEL_ARC_PAINT.setStrokeCap(DEFAULT_ARC_STROKE_CAP);
        DEFAULT_CURRENT_LEVEL_ARC_PAINT.setStrokeWidth(DEFAULT_ARC_STROKE_WIDTH);
        DEFAULT_CURRENT_LEVEL_ARC_PAINT.setColor(Color.BLUE);
    }

    private Paint customBackgroundArcPaint;
    private Paint customCurrentLevelTextPaint;
    private Paint customExtremeValueTextPaint;
    private Paint customGoalPaint;
    private Paint customCurrentLevelArcPaint;
    private Paint paint;
    private IndicatorType indicatorType = IndicatorType.NONE;
    private String currentLevelText;
    private String minString, maxString;
    private RectF doomRainbowRectF;
    private Rect childViewRect;
    private ValueAnimator animation;
    private int minValue, maxValue, distanceBetweenExtremeValues;
    private float currentLevelValue, goalValue;
    private float backgroundStartAngle, backgroundEndAngle, distanceBetweenExtremeAngles;
    public boolean hasCurrentLevelText;
    private float radius;
    private float viewWidthHalf;
    private float viewHeightHalf;
    private float valueToDraw;
    private boolean animated;
    private long animationDuration = DEFAULT_ANIMATION_DURATION;
    /**
     * Aspect ratio of child view, such that
     *
     *     w = LAMBDA h
     *
     * where w is the width of the child view, and h is the height of the child view.
     */
    private float lambda = 2f;

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

    // The following are accessor methods for the different paints used.

    @NonNull
    public Paint getBackgroundArcPaint() {
        return getPaint(customBackgroundArcPaint, DEFAULT_BACKGROUND_ARC_PAINT);
    }

    @NonNull
    public Paint getCurrentLevelTextPaint() {
        return getPaint(customCurrentLevelTextPaint, DEFAULT_CURRENT_LEVEL_TEXT_PAINT);
    }

    @NonNull
    public Paint getExtremeValueTextPaint() {
        return getPaint(customExtremeValueTextPaint, DEFAULT_EXTREME_LABEL_TEXT_PAINT);
    }

    @NonNull
    public Paint getGoalPaint() {
        return getPaint(customGoalPaint, DEFAULT_GOAL_PAINT);
    }

    @NonNull
    public Paint getCurrentLevelArcPaint() {
        return getPaint(customCurrentLevelArcPaint, DEFAULT_CURRENT_LEVEL_ARC_PAINT);
    }

    @NonNull
    public Paint getPaint(@Nullable Paint customPaint, @NonNull Paint defaultPaint) {
        if(customPaint != null) {
            return customPaint;
        } else {
            return defaultPaint;
        }
    }

    // The following methods set properties on each paint used.

    public void setArcStrokeCap(Paint.Cap strokeCap) {
        final Paint newBackgroundPaint = new Paint(getBackgroundArcPaint());
        newBackgroundPaint.setStrokeCap(strokeCap);
        customBackgroundArcPaint = newBackgroundPaint;
        final Paint newCurrentLevelPaint = new Paint(getCurrentLevelArcPaint());
        newCurrentLevelPaint.setStrokeCap(strokeCap);
        customCurrentLevelArcPaint = newCurrentLevelPaint;
        invalidate();
    }

    public void setArcStrokeWidth(float strokeWidth) {
        final Paint newBackgroundStrokeWidth = new Paint(getBackgroundArcPaint());
        newBackgroundStrokeWidth.setStrokeWidth(strokeWidth);
        customBackgroundArcPaint = newBackgroundStrokeWidth;
        final Paint newCurrentLevelStrokeWidth = new Paint(getCurrentLevelArcPaint());
        newCurrentLevelStrokeWidth.setStrokeWidth(strokeWidth);
        customCurrentLevelArcPaint = newCurrentLevelStrokeWidth;
        invalidate();
    }

    public void setBackgroundArcColor(@ColorInt int color) {
        final Paint newPaint = new Paint(getBackgroundArcPaint());
        newPaint.setColor(color);
        customBackgroundArcPaint = newPaint;
        invalidate();
    }

    public void setCurrentLevelTextPaintColor(@ColorInt int color) {
        final Paint newPaint = new Paint(getCurrentLevelTextPaint());
        newPaint.setColor(color);
        customCurrentLevelTextPaint = newPaint;
        invalidate();
    }

    public void setCurrentLevelTextPaintFakeBoldText(boolean fakeBoldText) {
        final Paint newPaint = new Paint(getCurrentLevelTextPaint());
        newPaint.setFakeBoldText(fakeBoldText);
        customCurrentLevelTextPaint = newPaint;
        invalidate();
    }

    public void setExtremeValueTextPaintColor(@ColorInt int color) {
        final Paint newPaint = new Paint(getExtremeValueTextPaint());
        newPaint.setColor(color);
        customExtremeValueTextPaint = newPaint;
        invalidate();
    }

    public void setExtremeValueTextPaintFakeBoldText(boolean fakeBoldText) {
        final Paint newPaint = new Paint(getExtremeValueTextPaint());
        newPaint.setFakeBoldText(fakeBoldText);
        customExtremeValueTextPaint = newPaint;
        invalidate();
    }

    public void setGoalColor(@ColorInt int color) {
        final Paint newPaint = new Paint(getGoalPaint());
        newPaint.setColor(color);
        customGoalPaint = newPaint;
        invalidate();
    }

    public void setGoalIndicatorType(IndicatorType indicatorType) {
        //TODO: define this method. It'll set the enum value and we'll have predefined paints for each type.
        this.indicatorType = indicatorType;
        invalidate();
    }

    public enum IndicatorType {
        CIRCLE, ARC, NONE
    }

    private void init() {
        setAnimated(true);
        setSaveEnabled(true);
        this.setWillNotDraw(false);
        paint = new Paint();
        doomRainbowRectF = new RectF();
        childViewRect = new Rect();
        minValue = DEFAULT_MIN_VALUE;
        maxValue = DEFAULT_MAX_VALUE;
        distanceBetweenExtremeValues = maxValue - minValue;
        setBackgroundStartAngle(DEFAULT_BACKGROUND_START_ANGLE);
        setBackgroundEndAngle(DEFAULT_BACKGROUND_END_ANGLE);
        distanceBetweenExtremeAngles = backgroundEndAngle - backgroundStartAngle;
        setGoalValue(DEFAULT_GOAL_VALUE);
        setCurrentLevelText(DEFAULT_CURRENT_LEVEL_TEXT);
        currentLevelValue = minValue;
        resetValueToDraw();
        initDefaultValues();
        reanimate();
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

        final int count = getChildCount();

        if(count >= 2) {
            throw new IllegalStateException("You may only add two children to this view.");
        }

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            child.layout(
                    childViewRect.left,
                    childViewRect.top,
                    childViewRect.right,
                    childViewRect.bottom);


        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final double circleInternalRadius = radius - getBackgroundArcPaint().getStrokeWidth() / 2;
        final double childViewHeight = 2 * circleInternalRadius / sqrt(1 + pow(lambda, 2));
        final double childViewWidth = lambda * childViewHeight;

        childViewRect.set(
                (int) ceil((getMeasuredWidth() - childViewWidth) / 2),
                (int) floor((getMeasuredHeight() - childViewHeight) / 2),
                (int) ceil((getMeasuredWidth() + childViewWidth) / 2),
                (int) floor((getMeasuredHeight() + childViewHeight) / 2));

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
        doomRainbowRectF.set(
                viewWidthHalf - radius,
                viewHeightHalf - radius,
                viewWidthHalf + radius,
                viewHeightHalf + radius
        );

        drawShiftedArc(canvas, doomRainbowRectF, minValue, maxValue, getBackgroundArcPaint());

        drawShiftedArc(canvas, doomRainbowRectF, minValue, valueToDraw, getCurrentLevelArcPaint());

        if(hasCurrentLevelText) {
            double currentLevelAngle = AngleUtils.convertFromValueToAngle(
                    currentLevelValue,
                    distanceBetweenExtremeAngles,
                    distanceBetweenExtremeValues
            );
            double shiftedAngle = currentLevelAngle - 90;
            double angleInRadians = AngleUtils.convertToRadians(shiftedAngle);

            canvas.drawText(
                    currentLevelText,
                    viewWidthHalf + (float) Math.cos(angleInRadians) * radius * LEVEL_TEXT_RADIUS_SCALE_FACTOR,
                    viewHeightHalf + (float) Math.sin(angleInRadians) * radius * LEVEL_TEXT_RADIUS_SCALE_FACTOR,
                    paint
            );
        }

        drawExtremeLabelsIfPresent(canvas);

        switch(indicatorType) {
            case CIRCLE:
                float goalAngle = AngleUtils.convertFromValueToAngle(
                        goalValue,
                        backgroundEndAngle - backgroundStartAngle,
                        maxValue - minValue
                        );
                double goalAngleRadians = AngleUtils.convertToRadians(goalAngle);
                canvas.drawPoint(
                        viewWidthHalf + (float) Math.cos(goalAngleRadians) * radius,
                        viewHeightHalf + (float) Math.sin(goalAngleRadians) * radius,
                        getGoalPaint()
                );
                break;
            case ARC:
                drawShiftedArc(
                        canvas,
                        doomRainbowRectF,
                        goalValue - DEFAULT_GOAL_ARC_LENGTH/2,
                        goalValue + DEFAULT_GOAL_ARC_LENGTH/2,
                        getGoalPaint()
                );
                break;
            case NONE:
            default:
                break;
        }
    }

    public void drawShiftedArc(Canvas canvas, RectF rectF, float startValue, float endValue, Paint paint) {
        float startAngle = AngleUtils.convertFromValueToAngle(
                startValue,
                distanceBetweenExtremeAngles,
                distanceBetweenExtremeValues
        );
        float endAngle = AngleUtils.convertFromValueToAngle(
                endValue,
                distanceBetweenExtremeAngles,
                distanceBetweenExtremeValues
        );
        canvas.drawArc(rectF, startAngle - 90, (endAngle - startAngle), false, paint);
    }


    private void drawExtremeLabelsIfPresent(Canvas canvas) {
        float yCoord = viewHeightHalf + radius;
        float floatViewWidthHalf = (float) this.getMeasuredWidth()/2;

        if(minString != null) {
            float minValRadiusCosCoefficient = AngleUtils.getRadiusCosineCoefficient(backgroundStartAngle - DEFAULT_BACKGROUND_EXTREME_LABEL_PADDING);
            float xCoord = floatViewWidthHalf + minValRadiusCosCoefficient * radius;
            drawValue(canvas, minString, xCoord, yCoord);
        }

        if(maxString != null) {
            float maxValRadiusCosCoefficient = AngleUtils.getRadiusCosineCoefficient(backgroundEndAngle + DEFAULT_BACKGROUND_EXTREME_LABEL_PADDING);
            float xCoord = floatViewWidthHalf - maxValRadiusCosCoefficient * radius;
            drawValue(canvas, maxString, xCoord, yCoord);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.currentLevelValue = currentLevelValue;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        currentLevelValue = ss.currentLevelValue;
        resetValueToDraw();
    }

    private void drawValue(Canvas canvas, String string, float xCoord, float yCoord) {
        canvas.drawText(string, xCoord, yCoord, getExtremeValueTextPaint());
    }

    public void initDefaultValues() {
        setHasCurrentLevelText(true);
    }

    public float getCurrentLevelValue() {
        return currentLevelValue;
    }

    public float getGoalValue() {
        return goalValue;
    }

    public float getBackgroundEndAngle() {
        return backgroundEndAngle;
    }

    public float getBackgroundStartAngle() {
        return backgroundStartAngle;
    }

    public void setHasCurrentLevelText(boolean hasCurrentLevelText) {
        this.hasCurrentLevelText = hasCurrentLevelText;
    }

    public void setCurrentLevelText(String currentLevelText) {
        this.currentLevelText = currentLevelText;
        invalidate();
    }

    private void resetValueToDraw() {
        valueToDraw = currentLevelValue;
    }

    private void animateBetweenValues(float startValue, float stopValue) {
        animation = ValueAnimator.ofFloat(startValue, stopValue);

        animation.setDuration(animationDuration);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                valueToDraw = (float) valueAnimator.getAnimatedValue();
                RainbowView.this.invalidate();
            }
        });

        animation.start();
    }

    public void setBackgroundStartAngle(float backgroundStartAngle) {
        this.backgroundStartAngle = backgroundStartAngle;
        invalidate();
    }

    public void setBackgroundEndAngle(float backgroundEndAngle) {
        this.backgroundEndAngle = backgroundEndAngle;
        invalidate();
    }

    public void setCurrentLevelValue(float currentLevelValue) {
        float previousValue = this.currentLevelValue;

        this.currentLevelValue = Math.min(
                Math.max(minValue, currentLevelValue),
                maxValue
        );

        if(animation != null) {
            animation.cancel();
        }

        if(animated) {
            animateBetweenValues(previousValue, this.currentLevelValue);
        } else {
            valueToDraw = this.currentLevelValue;
        }

        invalidate();
    }

    public void setGoalValue(float goalValue) {
        this.goalValue = goalValue;
        invalidate();
    }

    public void setMaxString(String maxString) {
        this.maxString = maxString;
        invalidate();
    }

    public void setMinString(String minString) {
        this.minString = minString;
        invalidate();
    }

    public void setAnimationDuration(long animationDuration) {
        this.animationDuration = animationDuration;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public void reanimate() {
        animateBetweenValues(minValue, currentLevelValue);
    }

    private static class SavedState extends BaseSavedState {
        float currentLevelValue;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentLevelValue = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(currentLevelValue);
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