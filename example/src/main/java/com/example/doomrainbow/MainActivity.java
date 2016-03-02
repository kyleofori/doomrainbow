package com.example.doomrainbow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.detroitlabs.kyleofori.doomrainbow.RainbowView;

public class MainActivity extends AppCompatActivity  {

    private RainbowView rainbowView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rainbowView = (RainbowView) findViewById(R.id.rainbow_view);
        rainbowView.setMinLabel("0");
        rainbowView.setMaxLabel("100");
        rainbowView.setChildViewAspectRatio(0.5f);
        rainbowView.setGoalIndicatorType(RainbowView.IndicatorType.ARC);
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
        float angle = rainbowView.getCurrentLevelValue();
        if(rainbowView.getGoalValue() >= angle + 8) {
            rainbowView.setCurrentLevelValue(angle + 8);
        } else {
            rainbowView.setCurrentLevelValue(rainbowView.getGoalValue());
        }
    }

    private void decreaseCurrentLevel() {
        float angle = rainbowView.getCurrentLevelValue();
        if(angle - 30 > rainbowView.getBackgroundStartAngle()) {
            rainbowView.setCurrentLevelValue(angle - 30);
        } else {
            rainbowView.setCurrentLevelValue(rainbowView.getBackgroundStartAngle());
        }
    }
}
