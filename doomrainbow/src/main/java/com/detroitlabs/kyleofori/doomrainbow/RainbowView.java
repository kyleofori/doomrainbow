package com.detroitlabs.kyleofori.doomrainbow;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

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
    private static final float DEFAULT_BACKGROUND_START_VALUE = 0;
    private static final float DEFAULT_BACKGROUND_END_VALUE = 100;
    private static final float DEFAULT_GOAL_ANGLE = 330;
    private static final float DEFAULT_CURRENT_LEVEL_ANGLE = 160;
    private static final float DEFAULT_GOAL_ARC_LENGTH_DEGREES = 0;
    private static final long DEFAULT_ANIMATION_DURATION = 2000;
    private static final String DEFAULT_CENTER_TEXT = "¡Hola!";
    private static final String DEFAULT_CURRENT_LEVEL_TEXT = "30%";
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_LABEL_COLOR = Color.GRAY;
    private static final int DEFAULT_CIRCLE_COLOR = Color.GRAY;
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
    private IndicatorType indicatorType = IndicatorType.NONE;

    private String centerText, currentLevelText;
    private String minString, maxString;
    private int minValue, maxValue;
    private Paint paint;
    private RectF rectF, inscribedRectF;
    private float backgroundStartAngle, backgroundEndAngle, goalAngle, currentLevelAngle;
    private float goalArcSweepAngle;
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
        rectF = new RectF();
        inscribedRectF = new RectF();
        minValue = DEFAULT_MIN_VALUE;
        maxValue = DEFAULT_MAX_VALUE;
        setBackgroundStartAngle(DEFAULT_BACKGROUND_START_ANGLE);
        setBackgroundEndAngle(DEFAULT_BACKGROUND_END_ANGLE);
        setGoalAngle(DEFAULT_GOAL_ANGLE);
        setCenterText(DEFAULT_CENTER_TEXT);
        setCurrentLevelText(DEFAULT_CURRENT_LEVEL_TEXT);
        currentLevelAngle = DEFAULT_BACKGROUND_START_ANGLE;
        resetValueToDraw();
        setGoalArcSweepAngle(DEFAULT_GOAL_ARC_LENGTH_DEGREES);
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

        inscribedRectF.set(
                viewWidthHalf - (float) (radius * Math.cos(Math.PI / 6)),
                viewHeightHalf - (float) (radius * Math.sin(Math.PI / 6)),
                viewWidthHalf + (float) (radius * Math.cos(Math.PI / 6)),
                viewHeightHalf + (float) (radius * Math.sin(Math.PI / 6))
        );

        final int count = getChildCount();

        if(count >= 2) {
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

        drawShiftedArc(canvas, rectF, backgroundStartAngle, backgroundEndAngle, getBackgroundArcPaint());

        drawShiftedArc(canvas, rectF, backgroundStartAngle, valueToDraw, getCurrentLevelArcPaint());

        canvas.drawText(centerText, viewWidthHalf, viewHeightHalf, paint);

        if(hasCurrentLevelText) {
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
                double goalAngleRadians = AngleUtils.convertToRadians(goalAngle);
                canvas.drawPoint(
                        viewWidthHalf + (float) Math.cos(goalAngleRadians) * radius,
                        viewHeightHalf + (float) Math.sin(goalAngleRadians) * radius,
                        getGoalPaint()
                );
                break;
            case ARC:
                drawShiftedArc(canvas, rectF, goalAngle - goalArcSweepAngle/2, goalArcSweepAngle, getGoalPaint());
                break;
            case NONE:
            default:
                break;
        }
    }

    private void drawExtremeLabelsIfPresent(Canvas canvas) {
        float yCoord = viewHeightHalf + radius;
        float floatViewWidthHalf = (float) this.getMeasuredWidth()/2;

        if(minString != null) {
            float minValRadiusCosCoefficient = getRadiusCosineCoefficient(backgroundStartAngle - DEFAULT_BACKGROUND_EXTREME_LABEL_PADDING);
            float xCoord = floatViewWidthHalf + minValRadiusCosCoefficient * radius;
            drawValue(canvas, minString, xCoord, yCoord);
        }

        if(maxString != null) {
            float maxValRadiusCosCoefficient = getRadiusCosineCoefficient(backgroundEndAngle + DEFAULT_BACKGROUND_EXTREME_LABEL_PADDING);
            float xCoord = floatViewWidthHalf - maxValRadiusCosCoefficient * radius;
            drawValue(canvas, maxString, xCoord, yCoord);
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
        resetValueToDraw();
    }

    private void drawValue(Canvas canvas, String string, float xCoord, float yCoord) {
        canvas.drawText(string, xCoord, yCoord, getExtremeValueTextPaint());
    }

    private float getRadiusCosineCoefficient(float valuePositionInDegrees) {
        double valuePositionInRadians = AngleUtils.convertToRadians((double) valuePositionInDegrees);
        return (float) Math.cos(valuePositionInRadians);
    }

    public void initDefaultValues() {
        setHasCurrentLevelText(true);
    }

    public float getCurrentLevelAngle() {
        return currentLevelAngle;
    }

    public float getGoalAngle() {
        return goalAngle;
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

    public void setCenterText(String centerText) {
        this.centerText = centerText;
        invalidateAndRequestLayout();
    }

    public void setCurrentLevelText(String currentLevelText) {
        this.currentLevelText = currentLevelText;
        invalidateAndRequestLayout();
    }

    private void resetValueToDraw() {
        valueToDraw = currentLevelAngle;
    }

    public void setCurrentLevelAngle(float currentLevelAngle) {
        float previousValue = this.currentLevelAngle;

        this.currentLevelAngle = Math.min(
                Math.max(getBackgroundStartAngle(), currentLevelAngle),
                getBackgroundEndAngle()
        );

        if(animation != null) {
            animation.cancel();
        }

        if(animated) {
            animateBetweenAngles(previousValue, this.currentLevelAngle);
        } else {
            valueToDraw = this.currentLevelAngle;
        }
        
        invalidateAndRequestLayout();
    }

    private void animateBetweenAngles(float startAngle, float stopAngle) {
        animation = ValueAnimator.ofFloat(startAngle, stopAngle);

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
        invalidateAndRequestLayout();
    }

    public void setBackgroundEndAngle(float backgroundEndAngle) {
        this.backgroundEndAngle = backgroundEndAngle;
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

    public void drawShiftedArc(Canvas canvas, RectF rectF, float startAngle, float endAngle, Paint paint) {
        canvas.drawArc(rectF, startAngle - 90, (endAngle - startAngle), false, paint);
    }

    public void reanimate() {
        animateBetweenAngles(getBackgroundStartAngle(), currentLevelAngle);
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
