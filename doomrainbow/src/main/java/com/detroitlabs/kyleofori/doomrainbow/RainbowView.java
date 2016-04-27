package com.detroitlabs.kyleofori.doomrainbow;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import static java.lang.Math.ceil;
import static java.lang.Math.cos;
import static java.lang.Math.floor;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class RainbowView extends FrameLayout {

    private static final Paint BASE_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint DEFAULT_BACKGROUND_ARC_PAINT = new Paint(BASE_PAINT);
    private static final Paint DEFAULT_CURRENT_VALUE_LABEL_PAINT = new Paint(BASE_PAINT);
    private static final Paint DEFAULT_MINIMUM_LABEL_PAINT = new Paint(BASE_PAINT);
    private static final Paint DEFAULT_MAXIMUM_LABEL_PAINT = new Paint(BASE_PAINT);
    private static final Paint DEFAULT_GOAL_INDICATOR_PAINT = new Paint(BASE_PAINT);
    private static final Paint DEFAULT_FOREGROUND_ARC_PAINT = new Paint(BASE_PAINT);
    private static final Paint.Cap DEFAULT_ARC_STROKE_CAP = Paint.Cap.ROUND;
    private static final float DEFAULT_BACKGROUND_START_ANGLE = -135;
    private static final float DEFAULT_BACKGROUND_END_ANGLE = 135;

    private static final float DEFAULT_RANGE_LABEL_ANGULAR_OFFSET = 15;
    private static final float DEFAULT_RANGE_LABEL_RADIAL_PADDING_DP = 0;
    private static final float DEFAULT_RADIUS_COEFFICIENT = .75f;
    private static final float DEFAULT_CHILD_VIEW_ASPECT_RATIO = 2f;
    private static final long DEFAULT_ANIMATION_DURATION_MS = 2000;
    private static final int DEFAULT_MINIMUM_VALUE = 0;
    private static final float DEFAULT_GOAL_VALUE = 90;
    private static final int DEFAULT_MAXIMUM_VALUE = 100;
    private static final int DEFAULT_ARC_STROKE_WIDTH_DP = 16;
    private static final float DEFAULT_CURRENT_VALUE_LABEL_TEXT_SIZE_SP = 14;
    private static final float DEFAULT_RANGE_LABEL_TEXT_SIZE_SP = 14;
    private static final float LEVEL_TEXT_RADIUS_SCALE_FACTOR = 1.10f;

    static {
        initDefaultBackgroundArcPaint();
        initDefaultCurrentValueLabelPaint();
        initDefaultRangeLabelPaint();
        initDefaultGoalIndicatorPaint();
        initDefaultForegroundArcPaint();
    }

    private static void initDefaultBackgroundArcPaint() {
        DEFAULT_BACKGROUND_ARC_PAINT.setStyle(Paint.Style.STROKE);
        DEFAULT_BACKGROUND_ARC_PAINT.setStrokeCap(DEFAULT_ARC_STROKE_CAP);
        DEFAULT_BACKGROUND_ARC_PAINT.setColor(Color.parseColor("#E3EBED"));
    }

    private static void initDefaultCurrentValueLabelPaint() {
        DEFAULT_CURRENT_VALUE_LABEL_PAINT.setColor(Color.BLACK);
    }

    private static void initDefaultRangeLabelPaint() {
        initDefaultMinimumLabelPaint();
        initDefaultMaximumLabelPaint();
    }

    private static void initDefaultMinimumLabelPaint() {
        DEFAULT_MINIMUM_LABEL_PAINT.setColor(Color.BLACK);
        DEFAULT_MINIMUM_LABEL_PAINT.setTextAlign(Paint.Align.CENTER);
    }

    private static void initDefaultMaximumLabelPaint() {
        DEFAULT_MAXIMUM_LABEL_PAINT.setColor(Color.BLACK);
        DEFAULT_MAXIMUM_LABEL_PAINT.setTextAlign(Paint.Align.CENTER);
    }

    private static void initDefaultGoalIndicatorPaint() {
        DEFAULT_GOAL_INDICATOR_PAINT.setStyle(Paint.Style.STROKE);
        DEFAULT_GOAL_INDICATOR_PAINT.setStrokeCap(DEFAULT_ARC_STROKE_CAP);
        DEFAULT_GOAL_INDICATOR_PAINT.setColor(Color.parseColor("#A4AFB4"));
    }

    private static void initDefaultForegroundArcPaint() {
        DEFAULT_FOREGROUND_ARC_PAINT.setStyle(Paint.Style.STROKE);
        DEFAULT_FOREGROUND_ARC_PAINT.setStrokeCap(DEFAULT_ARC_STROKE_CAP);
        DEFAULT_FOREGROUND_ARC_PAINT.setColor(Color.parseColor("#1EB2E9"));
    }

    @Nullable
    private Paint customBackgroundArcPaint;
    @Nullable
    private Paint customCurrentValueLabelPaint;
    @Nullable
    private Paint customMinimumLabelPaint;
    @Nullable
    private Paint customMaximumLabelPaint;
    @Nullable
    private Paint customGoalIndicatorPaint;
    @Nullable
    private Paint customForegroundArcPaint;

    private int minimumValue, maximumValue;
    private float currentValue;
    private float rangeLabelAngularOffset;
    private float rangeLabelRadialPadding;
    private float minimumBackgroundArcAngle, maximumBackgroundArcAngle;
    private float radius;
    private float internalRadius;
    private float viewWidthHalf;
    private float viewHeightHalf;
    private float valueToDraw;
    private boolean animateChangesInCurrentLevel = true;
    private boolean displayCurrentLevelLabel;
    private long animationDuration = DEFAULT_ANIMATION_DURATION_MS;

    private String minimumValueLabel, maximumValueLabel;
    private RectF doomRainbowRectF;
    private Rect childViewRect;
    private ValueAnimator animation;
    private Float goalValue;

    /**
     * Aspect ratio of child view, such that
     * <p/>
     * w = LAMBDA h
     * <p/>
     * where w is the width of the child view, and h is the height of the child view.
     */
    private float lambda = DEFAULT_CHILD_VIEW_ASPECT_RATIO;

    public RainbowView(final Context context) {
        this(context, null);
    }

    public RainbowView(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RainbowView(
            final Context context,
            @Nullable final AttributeSet attrs,
            final int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        DEFAULT_BACKGROUND_ARC_PAINT.setStrokeWidth(dpToPx(DEFAULT_ARC_STROKE_WIDTH_DP));
        DEFAULT_GOAL_INDICATOR_PAINT.setStrokeWidth(dpToPx(DEFAULT_ARC_STROKE_WIDTH_DP));
        DEFAULT_FOREGROUND_ARC_PAINT.setStrokeWidth(dpToPx(DEFAULT_ARC_STROKE_WIDTH_DP));

        DEFAULT_CURRENT_VALUE_LABEL_PAINT.setTextSize(spToPx(DEFAULT_CURRENT_VALUE_LABEL_TEXT_SIZE_SP));
        DEFAULT_MINIMUM_LABEL_PAINT.setTextSize(spToPx(DEFAULT_RANGE_LABEL_TEXT_SIZE_SP));
        DEFAULT_MAXIMUM_LABEL_PAINT.setTextSize(spToPx(DEFAULT_RANGE_LABEL_TEXT_SIZE_SP));

        setSaveEnabled(true);
        setWillNotDraw(false);
        doomRainbowRectF = new RectF();
        childViewRect = new Rect();
        minimumValue = DEFAULT_MINIMUM_VALUE;
        maximumValue = DEFAULT_MAXIMUM_VALUE;
        setMinimumBackgroundArcAngle(DEFAULT_BACKGROUND_START_ANGLE);
        setMaximumBackgroundArcAngle(DEFAULT_BACKGROUND_END_ANGLE);
        setGoalValue(DEFAULT_GOAL_VALUE);
        setRangeLabelAngularOffset(DEFAULT_RANGE_LABEL_ANGULAR_OFFSET);
        setRangeLabelRadialPaddingDp(DEFAULT_RANGE_LABEL_RADIAL_PADDING_DP);
        currentValue = minimumValue;
        resetValueToDraw();
        reanimate();
    }

    // Overrides

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int measuredWidth = getMeasuredWidth();

        //noinspection SuspiciousNameCombination
        setMeasuredDimension(measuredWidth, measuredWidth);

        internalRadius = radius - getBackgroundArcPaint().getStrokeWidth() / 2;
        final double childViewHeight = (2 * internalRadius) / sqrt(1 + pow(lambda, 2));
        final double childViewWidth = lambda * childViewHeight;

        childViewRect.set(
                (int) ceil((getMeasuredWidth() - childViewWidth) / 2),
                (int) floor((getMeasuredHeight() - childViewHeight) / 2),
                (int) ceil((getMeasuredWidth() + childViewWidth) / 2),
                (int) floor((getMeasuredHeight() + childViewHeight) / 2)
        );

        final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                childViewRect.height(),
                MeasureSpec.EXACTLY
        );

        final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                childViewRect.width(),
                MeasureSpec.EXACTLY
        );

        if (getChildCount() > 0) {
            getChildAt(0).measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(
            final boolean changed,
            final int left,
            final int top,
            final int right,
            final int bottom) {

        super.onLayout(changed, left, top, right, bottom);
        viewWidthHalf = getMeasuredWidth() / 2;
        viewHeightHalf = getMeasuredHeight() / 2;

        if (viewHeightHalf > viewWidthHalf) {
            radius = viewWidthHalf * DEFAULT_RADIUS_COEFFICIENT;
        } else {
            radius = viewHeightHalf * DEFAULT_RADIUS_COEFFICIENT;
        }

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            child.layout(
                    childViewRect.left,
                    childViewRect.top,
                    childViewRect.right,
                    childViewRect.bottom
            );
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        doomRainbowRectF.set(
                viewWidthHalf - radius,
                viewHeightHalf - radius,
                viewWidthHalf + radius,
                viewHeightHalf + radius);

        drawShiftedArc(canvas, doomRainbowRectF, minimumValue, maximumValue, getBackgroundArcPaint());

        drawShiftedArc(canvas, doomRainbowRectF, minimumValue, valueToDraw, getCurrentLevelArcPaint());

        drawCurrentLevelTextIfPresent(canvas);

        drawRangeLabelsIfPresent(canvas);

        if (goalValue != null) {
            drawIndicator(canvas);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState ss = new SavedState(superState);
        ss.currentLevelValue = currentValue;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(final Parcelable state) {
        final SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        currentValue = ss.currentLevelValue;
        resetValueToDraw();
    }

    @Override
    public void addView(final View child) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("RainbowView can host at most one direct child");
        }

        super.addView(child);
    }

    @Override
    public void addView(final View child, final int index) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("RainbowView can host at most one direct child");
        }

        super.addView(child, index);
    }

    @Override
    public void addView(final View child, final ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("RainbowView can host at most one direct child");
        }

        super.addView(child, params);
    }

    @Override
    public void addView(final View child, final int index, final ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("RainbowView can host at most one direct child");
        }

        super.addView(child, index, params);
    }

    // Public API

    public void setArcCapStyle(final Paint.Cap strokeCap) {
        final Paint newBackgroundPaint = new Paint(getBackgroundArcPaint());
        newBackgroundPaint.setStrokeCap(strokeCap);
        customBackgroundArcPaint = newBackgroundPaint;
        final Paint newCurrentLevelPaint = new Paint(getCurrentLevelArcPaint());
        newCurrentLevelPaint.setStrokeCap(strokeCap);
        customForegroundArcPaint = newCurrentLevelPaint;
        final Paint newGoalPaint = new Paint(getGoalPaint());
        newGoalPaint.setStrokeCap(strokeCap);
        customGoalIndicatorPaint = newGoalPaint;
        invalidate();
    }

    public void setArcWidthDp(final float arcWidthDp) {
        final float arcWidthPx = dpToPx(arcWidthDp);

        final Paint newBackgroundPaint = new Paint(getBackgroundArcPaint());
        newBackgroundPaint.setStrokeWidth(arcWidthPx);
        customBackgroundArcPaint = newBackgroundPaint;

        final Paint newCurrentLevelPaint = new Paint(getCurrentLevelArcPaint());
        newCurrentLevelPaint.setStrokeWidth(arcWidthPx);
        customForegroundArcPaint = newCurrentLevelPaint;

        final Paint newGoalPaint = new Paint(getGoalPaint());
        newGoalPaint.setStrokeWidth(arcWidthPx);
        customGoalIndicatorPaint = newGoalPaint;

        invalidate();
    }

    public void setBackgroundArcColor(@ColorInt final int color) {
        final Paint newPaint = new Paint(getBackgroundArcPaint());
        newPaint.setColor(color);
        customBackgroundArcPaint = newPaint;
        invalidate();
    }

    public void setForegroundArcColor(@ColorInt final int color) {
        final Paint newPaint = new Paint(getCurrentLevelArcPaint());
        newPaint.setColor(color);
        customForegroundArcPaint = newPaint;
        invalidate();
    }

    /**
     * User should pass in a function that maps from the minimum and maximum value of the rainbowView
     * to the color code.
     */
    public void setCurrentLevelArcPaintColorFunction(
            final Float value,
            final Function<Integer, Integer> function) {

        final Paint newPaint = new Paint(getCurrentLevelArcPaint());
        newPaint.setColor(function.apply(Math.round(value)));
        customForegroundArcPaint = newPaint;
        invalidate();
    }

    public void setCurrentValueLabelTypeface(@NonNull final Typeface typeface){
        final Paint newPaint = new Paint(getCurrentLevelTextPaint());
        newPaint.setTypeface(typeface);
        customCurrentValueLabelPaint = newPaint;
        invalidate();
    }

    public void setCurrentValueLabelTextColor(@ColorInt final int textColor) {
        final Paint newPaint = new Paint(getCurrentLevelTextPaint());
        newPaint.setColor(textColor);
        customCurrentValueLabelPaint = newPaint;
        invalidate();
    }

    public void setCurrentValueLabelTextSizeSp(final float textSizeSp) {
        final float textSizePx = spToPx(textSizeSp);
        final Paint newPaint = new Paint(getCurrentLevelTextPaint());
        newPaint.setTextSize(textSizePx);
        customCurrentValueLabelPaint = newPaint;
        invalidate();
    }

    public void setRangeLabelTextColor(@ColorInt final int color) {
        final Paint newMinPaint = new Paint(getMinimumLabelTextPaint());
        newMinPaint.setColor(color);
        customMinimumLabelPaint = newMinPaint;

        final Paint newMaxPaint = new Paint(getMaximumLabelTextPaint());
        newMaxPaint.setColor(color);
        customMaximumLabelPaint = newMaxPaint;
        invalidate();
    }

    public void setRangeLabelTypeface(@NonNull final Typeface typeface){
        final Paint newMinPaint = new Paint(getMinimumLabelTextPaint());
        newMinPaint.setTypeface(typeface);
        customMinimumLabelPaint = newMinPaint;

        final Paint newMaxPaint = new Paint(getMaximumLabelTextPaint());
        newMaxPaint.setTypeface(typeface);
        customMaximumLabelPaint = newMaxPaint;
        invalidate();
    }

    public void alignRangeLabelTextInward() {
        final Paint newMinPaint = new Paint(getMinimumLabelTextPaint());
        newMinPaint.setTextAlign(Paint.Align.LEFT);
        customMinimumLabelPaint = newMinPaint;

        final Paint newMaxPaint = new Paint(getMaximumLabelTextPaint());
        newMaxPaint.setTextAlign(Paint.Align.RIGHT);
        customMaximumLabelPaint = newMaxPaint;
        invalidate();
    }

    public void alignRangeLabelTextOutward() {
        final Paint newMinPaint = new Paint(getMinimumLabelTextPaint());
        newMinPaint.setTextAlign(Paint.Align.RIGHT);
        customMinimumLabelPaint = newMinPaint;

        final Paint newMaxPaint = new Paint(getMaximumLabelTextPaint());
        newMaxPaint.setTextAlign(Paint.Align.LEFT);
        customMaximumLabelPaint = newMaxPaint;
        invalidate();
    }

    public void alignRangeLabelTextCentered() {
        final Paint newMinPaint = new Paint(getMinimumLabelTextPaint());
        newMinPaint.setTextAlign(Paint.Align.CENTER);
        customMinimumLabelPaint = newMinPaint;

        final Paint newMaxPaint = new Paint(getMaximumLabelTextPaint());
        newMaxPaint.setTextAlign(Paint.Align.CENTER);
        customMaximumLabelPaint = newMaxPaint;
        invalidate();
    }

    public void setRangeLabelAngularOffset(final float rangeLabelAngularOffset) {
        this.rangeLabelAngularOffset = rangeLabelAngularOffset;
        invalidate();
    }

    public void setRangeLabelRadialPaddingDp(final float rangeLabelRadialPaddingDp) {
        this.rangeLabelRadialPadding = dpToPx(rangeLabelRadialPaddingDp);
        invalidate();
    }

    public void setRangeLabelTextSizeSp(final float textSizeSp) {
        final float textSizePx = spToPx(textSizeSp);

        final Paint newMinPaint = new Paint(getMinimumLabelTextPaint());
        newMinPaint.setTextSize(textSizePx);
        customMinimumLabelPaint = newMinPaint;

        final Paint newMaxPaint = new Paint(getMaximumLabelTextPaint());
        newMaxPaint.setTextSize(textSizePx);
        customMaximumLabelPaint = newMaxPaint;
        invalidate();
    }

    public void setGoalIndicatorColor(@ColorInt final int color) {
        final Paint newPaint = new Paint(getGoalPaint());
        newPaint.setColor(color);
        customGoalIndicatorPaint = newPaint;
        invalidate();
    }

    public void changeCurrentValueBy(final float difference) {
        setCurrentValue(currentValue + difference);
    }

    public void setRepresentedRange(
            final int minimumValue,
            final int maximumValue,
            final boolean updateRangeLabels) {

        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;

        if (updateRangeLabels) {
            minimumValueLabel = Integer.toString(minimumValue);
            maximumValueLabel = Integer.toString(maximumValue);
        }

        invalidate();
    }

    public void setCurrentValue(final float currentValue) {
        final float previousValue = this.currentValue;

        this.currentValue = min(max(minimumValue, currentValue), maximumValue);

        if (animation != null) {
            animation.cancel();
        }

        if (animateChangesInCurrentLevel) {
            animateBetweenValues(previousValue, currentValue);
        } else {
            valueToDraw = this.currentValue;
        }

        invalidate();
    }

    public void setGoalValue(final float goalValue) {
        this.goalValue = goalValue;
        invalidate();
    }

    public void clearGoalValue() {
        goalValue = null;
        invalidate();
    }

    public void setMaximumValueLabel(final String maximumValueLabel) {
        this.maximumValueLabel = maximumValueLabel;
        invalidate();
    }

    public void setMinimumValueLabel(final String minimumValueLabel) {
        this.minimumValueLabel = minimumValueLabel;
        invalidate();
    }

    public void setAnimationDuration(final long animationDuration) {
        this.animationDuration = animationDuration;
    }

    public void setShouldAnimateChangesInCurrentLevel(final boolean animateChangesInCurrentLevel) {
        this.animateChangesInCurrentLevel = animateChangesInCurrentLevel;
    }

    private void setShouldDisplayCurrentLevelLabel(final boolean displayCurrentLevelLabel) {
        this.displayCurrentLevelLabel = displayCurrentLevelLabel;
    }

    public void setChildViewAspectRatio(final float lambda) {
        this.lambda = lambda;
        requestLayout();
    }

    public void setMinimumBackgroundArcAngle(final float minimumBackgroundArcAngle) {
        this.minimumBackgroundArcAngle = minimumBackgroundArcAngle;
        invalidate();
    }

    public void setMaximumBackgroundArcAngle(final float backgroundEndAngle) {
        this.maximumBackgroundArcAngle = backgroundEndAngle;
        invalidate();
    }

    // Private implementation

    @NonNull
    private Paint getBackgroundArcPaint() {
        return getPaint(customBackgroundArcPaint, DEFAULT_BACKGROUND_ARC_PAINT);
    }

    @NonNull
    private Paint getCurrentLevelTextPaint() {
        return getPaint(customCurrentValueLabelPaint, DEFAULT_CURRENT_VALUE_LABEL_PAINT);
    }

    @NonNull
    private Paint getMinimumLabelTextPaint() {
        return getPaint(customMinimumLabelPaint, DEFAULT_MINIMUM_LABEL_PAINT);
    }

    @NonNull
    private Paint getMaximumLabelTextPaint() {
        return getPaint(customMaximumLabelPaint, DEFAULT_MAXIMUM_LABEL_PAINT);
    }

    @NonNull
    private Paint getGoalPaint() {
        return getPaint(customGoalIndicatorPaint, DEFAULT_GOAL_INDICATOR_PAINT);
    }

    @NonNull
    private Paint getCurrentLevelArcPaint() {
        return getPaint(customForegroundArcPaint, DEFAULT_FOREGROUND_ARC_PAINT);
    }

    @NonNull
    private Paint getPaint(@Nullable final Paint customPaint, @NonNull final Paint defaultPaint) {
        return customPaint != null ? customPaint : defaultPaint;
    }

    private void drawCurrentLevelTextIfPresent(final Canvas canvas) {
        if (displayCurrentLevelLabel) {
            final double currentLevelAngle = AngleUtils.convertFromValueToAngle(
                    currentValue,
                    getBackgroundArcAngleRangeLength(),
                    getRepresentedRangeLength());

            final double angleInRadians = Math.toRadians(currentLevelAngle - 90);

            canvas.drawText(
                    String.valueOf(Math.round(currentValue)),
                    viewWidthHalf + (float) cos(angleInRadians) * radius * LEVEL_TEXT_RADIUS_SCALE_FACTOR,
                    viewHeightHalf + (float) sin(angleInRadians) * radius * LEVEL_TEXT_RADIUS_SCALE_FACTOR,
                    getCurrentLevelTextPaint()
            );
        }
    }

    private void drawShiftedArc(
            final Canvas canvas,
            final RectF rectF,
            final float startValue,
            final float endValue,
            final Paint paint) {

        final float startAngle = AngleUtils.convertFromValueToAngle(
                startValue,
                getBackgroundArcAngleRangeLength(),
                getRepresentedRangeLength());

        final float endAngle = AngleUtils.convertFromValueToAngle(
                endValue,
                getBackgroundArcAngleRangeLength(),
                getRepresentedRangeLength());

        canvas.drawArc(rectF, startAngle - 90, (endAngle - startAngle), false, paint);
    }


    private void drawRangeLabelsIfPresent(final Canvas canvas) {
        if (minimumValueLabel != null) {
            drawMinimumLabel(canvas);
        }
        if (maximumValueLabel != null) {
            drawMaximumLabel(canvas);
        }
    }

    private void drawMinimumLabel(final Canvas canvas) {
        final float rangeLabelRadius = internalRadius + rangeLabelRadialPadding;
        final Rect minLabelTextBounds = new Rect();
        getMinimumLabelTextPaint().getTextBounds(minimumValueLabel, 0, minimumValueLabel.length(), minLabelTextBounds);

        final float minLabelYCoord = viewHeightHalf
                - (rangeLabelRadius * AngleUtils.getRadiusCosineCoefficient(minimumBackgroundArcAngle - rangeLabelAngularOffset))
                + minLabelTextBounds.height();

        final float minLabelXCoord = viewWidthHalf
                + (AngleUtils.getRadiusCosineCoefficient(minimumBackgroundArcAngle + rangeLabelAngularOffset) * rangeLabelRadius);

        canvas.drawText(minimumValueLabel, minLabelXCoord, minLabelYCoord, getMinimumLabelTextPaint());
    }

    private void drawMaximumLabel(final Canvas canvas) {
        final float rangeLabelRadius = internalRadius + rangeLabelRadialPadding;
        final Rect maxLabelTextBounds = new Rect();
        getMaximumLabelTextPaint().getTextBounds(maximumValueLabel, 0, maximumValueLabel.length(), maxLabelTextBounds);

        final float maxLabelYCoord = viewHeightHalf
                - (rangeLabelRadius * AngleUtils.getRadiusCosineCoefficient(maximumBackgroundArcAngle + rangeLabelAngularOffset))
                + maxLabelTextBounds.height();

        final float maxLabelXCoord = viewWidthHalf
                - (AngleUtils.getRadiusCosineCoefficient(maximumBackgroundArcAngle - rangeLabelAngularOffset) * rangeLabelRadius);

        canvas.drawText(maximumValueLabel, maxLabelXCoord, maxLabelYCoord, getMaximumLabelTextPaint());
    }

    private void drawIndicator(final Canvas canvas) {
        final float goalAngle = AngleUtils.convertFromValueToAngle(
                goalValue,
                getBackgroundArcAngleRangeLength(),
                getRepresentedRangeLength());

        final double goalAngleRadians = Math.toRadians(goalAngle - 90);

        canvas.drawPoint(
                viewWidthHalf + (float) cos(goalAngleRadians) * radius,
                viewHeightHalf + (float) sin(goalAngleRadians) * radius,
                getGoalPaint());
    }

    private void reanimate() {
        animateBetweenValues(minimumValue, currentValue);
    }

    private void resetValueToDraw() {
        valueToDraw = currentValue;
    }

    private void animateBetweenValues(final float startValue, final float stopValue) {
        animation = ValueAnimator.ofFloat(startValue, stopValue);

        animation.setDuration(animationDuration);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                valueToDraw = (float) valueAnimator.getAnimatedValue();
                RainbowView.this.invalidate();
            }
        });

        animation.start();
    }

    private int getRepresentedRangeLength() {
        return maximumValue - minimumValue;
    }

    private float getBackgroundArcAngleRangeLength() {
        return maximumBackgroundArcAngle - minimumBackgroundArcAngle;
    }

    private float spToPx(final float sp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    private float dpToPx(final float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private static class SavedState extends BaseSavedState {

        private float currentLevelValue;

        SavedState(final Parcelable superState) {
            super(superState);
        }

        private SavedState(final Parcel in) {
            super(in);
            currentLevelValue = in.readFloat();
        }

        @Override
        public void writeToParcel(final Parcel out, final int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(currentLevelValue);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(final Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(final int size) {
                return new SavedState[size];
            }
        };
    }

}
