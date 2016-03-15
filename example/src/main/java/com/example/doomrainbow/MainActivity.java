package com.example.doomrainbow;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.detroitlabs.kyleofori.doomrainbow.RainbowView;

import rx.functions.Func1;

public class MainActivity extends AppCompatActivity  {

    private RainbowView rainbowView;
    private Func1<Integer, Integer> colorFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rainbowView = (RainbowView) findViewById(R.id.rainbow_view);
        colorFunction = new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer integer) {
                return Color.argb(80, 2 * integer, 0, integer);
            }
        };
        rainbowView.setMinLabel("0");
        rainbowView.setMaxLabel("100");
        rainbowView.setCurrentLevelArcPaintColorFunction(rainbowView.getCurrentLevelValue(), colorFunction);
        rainbowView.setChildViewAspectRatio(0.5f);
        rainbowView.setGoalIndicatorType(RainbowView.IndicatorType.ARC);
        rainbowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseCurrentLevel();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void increaseCurrentLevel() {
        float value = rainbowView.getCurrentLevelValue();
        if(rainbowView.getGoalValue() >= value + 8) {
            rainbowView.setCurrentLevelValue(value + 8);
        } else {
            rainbowView.setCurrentLevelValue(rainbowView.getGoalValue());
        }
        rainbowView.setCurrentLevelArcPaintColorFunction(value, colorFunction);
    }

    private void decreaseCurrentLevel() {
        float value = rainbowView.getCurrentLevelValue();
        if(value - 30 > rainbowView.getBackgroundStartAngle()) {
            rainbowView.setCurrentLevelValue(value - 30);
        } else {
            rainbowView.setCurrentLevelValue(rainbowView.getBackgroundStartAngle());
        }
        rainbowView.setCurrentLevelArcPaintColorFunction(value, colorFunction);
    }
}
