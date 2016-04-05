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
        firstView.setChildViewAspectRatio(0.5f);
        firstView.setGoalIndicatorType(RainbowView.IndicatorType.CIRCLE);
        firstView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                firstView.changeCurrentValueBy(1);
            }
        });
    }

    private void initSecondView() {
        secondView.setBackgroundArcColor(Color.YELLOW);
        secondView.setGoalIndicatorType(RainbowView.IndicatorType.CIRCLE);
        secondView.setGoalIndicatorColor(Color.BLACK);
        secondView.setArcWidthDp(24);
    }

    private void initThirdView() {
        thirdView.setBackgroundArcColor(Color.BLACK);
        thirdView.setMinimumValueLabel("E");
        thirdView.setMaximumValueLabel("F");
    }

}
