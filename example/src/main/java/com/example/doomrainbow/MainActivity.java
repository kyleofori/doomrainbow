package com.example.doomrainbow;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.detroitlabs.kyleofori.doomrainbow.Function;
import com.detroitlabs.kyleofori.doomrainbow.RainbowView;

public class MainActivity extends AppCompatActivity  {

    private RainbowView firstView, secondView, thirdView;
    private Function<Integer, Integer> colorFunction;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firstView = (RainbowView) findViewById(R.id.rainbow_view);
        secondView = (RainbowView) findViewById(R.id.rainbow_view1);
        thirdView = (RainbowView) findViewById(R.id.rainbow_view2);
        colorFunction = new Function<Integer, Integer>() {
            @Override
            public Integer apply(final Integer integer) {
                return Color.argb(80, 2 * integer, 0, integer);
            }
        };
        initFirstView();
        initSecondView();
        initThirdView();
    }

    private void initFirstView() {
        firstView.setMinLabel("0");
        firstView.setMaxLabel("100");
        firstView.setCurrentLevelArcPaintColorFunction(firstView.getCurrentLevelValue(), colorFunction);
        firstView.setChildViewAspectRatio(0.5f);
        firstView.setGoalIndicatorType(RainbowView.IndicatorType.ARC);
        firstView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                increaseCurrentLevel();
            }
        });
    }

    private void initSecondView() {
        secondView.setBackgroundArcPaintColor(Color.YELLOW);
        secondView.setGoalIndicatorType(RainbowView.IndicatorType.CIRCLE);
        secondView.setGoalPaintColor(Color.BLACK);
        secondView.setArcPaintStrokeWidth(50);
    }

    private void initThirdView() {
        thirdView.setBackgroundArcPaintColor(Color.BLACK);
        thirdView.setMinLabel("E");
        thirdView.setMaxLabel("F");
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void increaseCurrentLevel() {
        final float value = firstView.getCurrentLevelValue();
        if(firstView.getGoalValue() >= value + 8) {
            firstView.setCurrentLevelValue(value + 8);
        } else {
            firstView.setCurrentLevelValue(firstView.getGoalValue());
        }
        firstView.setCurrentLevelArcPaintColorFunction(value, colorFunction);
    }

    private void decreaseCurrentLevel() {
        final float value = firstView.getCurrentLevelValue();
        if(value - 30 > firstView.getBackgroundStartAngle()) {
            firstView.setCurrentLevelValue(value - 30);
        } else {
            firstView.setCurrentLevelValue(firstView.getBackgroundStartAngle());
        }
        firstView.setCurrentLevelArcPaintColorFunction(value, colorFunction);
    }
}
