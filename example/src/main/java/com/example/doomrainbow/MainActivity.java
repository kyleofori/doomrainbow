package com.example.doomrainbow;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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
        firstView.setMinimumValueLabel("0");
        firstView.setMaximumValueLabel("100");
        firstView.setChildViewAspectRatio(0.5f);
        firstView.setGoalIndicatorType(RainbowView.IndicatorType.ARC);
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

}
