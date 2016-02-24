package com.example.doomrainbow;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.detroitlabs.kyleofori.doomrainbow.RainbowView;

public class MainActivity extends AppCompatActivity  {

    private RainbowView rainbowView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rainbowView = (RainbowView) findViewById(R.id.rainbow_view);

        if(rainbowView.hasChangeButtons) {
            LayoutInflater layoutInflater = getLayoutInflater();
            for(int i = 0; i <= 1; i++) {
                View changeButton = layoutInflater.inflate(R.layout.change_button_layout, rainbowView, false);
                ImageView tagImageView = (ImageView) changeButton.findViewById(R.id.image);
                if (i == 0) {
                    changeButton.setTag(getString(R.string.decrease));
                    tagImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.minus_sign));
                    changeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            decreaseCurrentLevel();
                        }
                    });
                } else {
                    changeButton.setTag(getString(R.string.increase));
                    tagImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.plus_sign));
                    changeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            increaseCurrentLevel();
                        }
                    });
                }
                rainbowView.addView(changeButton);
            }
        }
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
        float angle = rainbowView.getCurrentLevelAngle();
        if(rainbowView.getGoalAngle() >= angle + 30) {
            rainbowView.setCurrentLevelAngle(angle + 30);
        } else {
            rainbowView.setCurrentLevelAngle(rainbowView.getGoalAngle());
        }
    }

    private void decreaseCurrentLevel() {
        float angle = rainbowView.getCurrentLevelAngle();
        if(angle - 30 > rainbowView.getBackgroundStartAngle()) {
            rainbowView.setCurrentLevelAngle(angle - 30);
        } else {
            rainbowView.setCurrentLevelAngle(rainbowView.getBackgroundStartAngle());
        }
    }
}
