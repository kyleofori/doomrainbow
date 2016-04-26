package com.example.doomrainbow;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.detroitlabs.kyleofori.doomrainbow.RainbowView;

public class MainActivity extends AppCompatActivity  {

    private RainbowView firstView, secondView, thirdView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstView = (RainbowView) findViewById(R.id.rainbow_view);
        secondView = (RainbowView) findViewById(R.id.rainbow_view1);
        thirdView = (RainbowView) findViewById(R.id.rainbow_view2);

        initFirstView();
        initSecondView();
        initThirdView();
    }

    private void initFirstView() {
        firstView.setArcRadialPaddingDp(16);
        firstView.setRepresentedRange(0, 100, true);
        firstView.setGoalValue(72);
        firstView.setCurrentValue(64);
        firstView.setChildViewAspectRatio(1f);
        firstView.setRangeLabelTextColor(Color.parseColor("#1EB2E9"));
        firstView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                firstView.changeCurrentValueBy(1);
            }
        });
    }

    private void initSecondView() {
        secondView.setArcRadialPaddingDp(48);
        secondView.setBackgroundArcColor(Color.YELLOW);
        secondView.setGoalIndicatorColor(Color.BLACK);
        secondView.setRangeLabelAngularOffset(-23.5f);
        secondView.setRangeLabelRadialPaddingDp(-24);
        secondView.alignRangeLabelTextInward();
        secondView.setMinimumValueLabel("MIN");
        secondView.setMaximumValueLabel("MAX");
        secondView.setRangeLabelTextColor(Color.YELLOW);
        secondView.setArcStrokeWidthDp(24);
    }

    private void initThirdView() {
        thirdView.setArcRadialPaddingDp(16);
        thirdView.setBackgroundArcColor(Color.BLACK);
        thirdView.setMinimumValueLabel("E");
        thirdView.setMaximumValueLabel("F");
        thirdView.setRepresentedRange(0, 200, false);
        thirdView.setCurrentValue(150);
        thirdView.setRangeLabelTextSizeSp(32);
    }

}
