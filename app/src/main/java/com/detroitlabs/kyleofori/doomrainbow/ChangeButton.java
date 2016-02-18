package com.detroitlabs.kyleofori.doomrainbow;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class ChangeButton extends Button {
    private String buttonText;

    public ChangeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ChangeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChangeButton(Context context) {
        super(context);
    }

    private void init() {
    }
}
