package com.detroitlabs.kyleofori.doomrainbow;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class RainbowView extends View {

    private int circleColor, labelColor;
    private String circleText;
    private Paint circlePaint;

    public RainbowView(Context context) {
        super(context);
    }

    public RainbowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        circlePaint = new Paint();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RainbowView, 0, 0);

        try {
            circleText = a.getString(R.styleable.RainbowView_circleLabel);
            circleColor = a.getInteger(R.styleable.RainbowView_circleColor, 0);
            labelColor = a.getInteger(R.styleable.RainbowView_labelColor, 0);
        } finally {
            a.recycle();
        }
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

        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);

        circlePaint.setColor(circleColor);

        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, circlePaint);

        //set text color, properties, then draw it
        circlePaint.setColor(labelColor);
        circlePaint.setTextAlign(Paint.Align.CENTER);
        circlePaint.setTextSize(50);

        canvas.drawText(circleText, viewWidthHalf, viewHeightHalf, circlePaint);
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
}
