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
        firstView.setRepresentedRange(0, 100, true);
        firstView.setGoalValue(72);
        firstView.setCurrentValue(64);
        firstView.setChildViewAspectRatio(1f);
        thirdView.setMinimumValueLabel("E");
        thirdView.setMaximumValueLabel("F");
        firstView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                firstView.changeCurrentValueBy(1);
            }
        });
    }

    private void initSecondView() {
        secondView.setBackgroundArcColor(Color.YELLOW);
        secondView.setGoalIndicatorColor(Color.BLACK);
        secondView.setRangeLabelAngularOffset(-23.5f);
        secondView.setRangeLabelRadialPaddingDp(-24);
        secondView.alignRangeLabelTextInward();
        secondView.setMinimumValueLabel("MIN");
        secondView.setMaximumValueLabel("MAX");
        secondView.setArcWidthDp(24);
    }

    private void initThirdView() {
        thirdView.setBackgroundArcColor(Color.BLACK);
        thirdView.setMinimumValueLabel("E");
        thirdView.setMaximumValueLabel("F");
        thirdView.setRangeLabelTextSizeSp(32);
    }

}
