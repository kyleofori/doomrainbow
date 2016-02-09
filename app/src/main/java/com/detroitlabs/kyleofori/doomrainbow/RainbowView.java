package com.detroitlabs.kyleofori.doomrainbow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class RainbowView extends View {

    private static final float DEFAULT_START_ANGLE = 120;
    private static final float DEFAULT_SWEEP_ANGLE = 300;
    private static final int DEFAULT_ARC_WIDTH = 20;
    private int circleColor, labelColor;
    private String circleText;
    private Paint paint;
    private RectF rectF;
    private float startAngle;
    private float sweepAngle;


    public RainbowView(Context context) {
        super(context);
    }

    public RainbowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        rectF = new RectF();
        setStartAngle(DEFAULT_START_ANGLE);
        setSweepAngle(DEFAULT_SWEEP_ANGLE);

        circleText = "Hola";
        circleColor = Color.GRAY;
        labelColor = Color.GRAY;

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
        int radius;
        if(viewWidthHalf > viewHeightHalf) {
            radius = viewHeightHalf - 10;
        } else {
            radius = viewWidthHalf - 10;
        }

        rectF.set(viewWidthHalf - radius, viewHeightHalf - radius, viewWidthHalf + radius, viewHeightHalf + radius);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(DEFAULT_ARC_WIDTH);
        paint.setColor(circleColor);

        canvas.drawArc(rectF, startAngle, sweepAngle, false, paint);

        //set text color, properties, then draw it with the same paint
        paint.setColor(labelColor);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(50);
        paint.setStrokeWidth(0);

        canvas.drawText(circleText, viewWidthHalf, viewHeightHalf, paint);
    }

    public int getCircleColor() {
        return circleColor;
    }

    public int getLabelColor() {
        return labelColor;
    }

    public String getCircleText() {
        return circleText;
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

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
        invalidate();
        requestLayout();
    }

    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
        invalidate();
        requestLayout();
    }

    public void setCircleText(String circleText) {
        this.circleText = circleText;
        invalidate();
        requestLayout();
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }

    public void setSweepAngle(float sweepAngle) {
        this.sweepAngle = sweepAngle;
    }
}
