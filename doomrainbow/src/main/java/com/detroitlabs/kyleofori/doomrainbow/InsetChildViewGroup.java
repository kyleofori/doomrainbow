package com.detroitlabs.kyleofori.doomrainbow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import static java.lang.Math.*;

public class InsetChildViewGroup extends ViewGroup {

    public static final Paint CIRCLE_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);

    static {
        CIRCLE_PAINT.setColor(Color.BLUE);
        CIRCLE_PAINT.setStrokeWidth(40);
        CIRCLE_PAINT.setStyle(Paint.Style.STROKE);
    }

    private final Rect childViewRect = new Rect();

    // w = lambda * h, where w is width of childView and h is height of childView
    private float lambda = 0.2f;

    public InsetChildViewGroup(Context context) {
        this(context, null);
    }

    public InsetChildViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InsetChildViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //in order to fix the aspect ratio at 1:1
        //noinspection SuspiciousNameCombination
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

        final double circleInternalRadius = getMeasuredWidth() / 2 - CIRCLE_PAINT.getStrokeWidth();
        final double childViewHeight = 2 * circleInternalRadius / sqrt(1 + pow(lambda, 2));
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

        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {
            getChildAt(childIndex).measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for(int childIndex = 0; childIndex < getChildCount(); childIndex++) {
            final View child = getChildAt(childIndex);
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
        super.onDraw(canvas);
        canvas.drawCircle(
                (int) (getMeasuredWidth() / 2.0),
                (int) (getMeasuredHeight() / 2.0),
                (getWidth() - CIRCLE_PAINT.getStrokeWidth()) / 2,
                CIRCLE_PAINT
        );
    }

    public void setChildViewAspectRatio(final float lambda) {
        this.lambda = lambda;
        requestLayout();
    }
}