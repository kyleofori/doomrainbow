package com.detroitlabs.kyleofori.doomrainbow;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RainbowView rainbowView;
    private Button pressButton;

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
                    tagImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.minus_sign));
                } else {
                    tagImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.plus_sign));
                }
                rainbowView.addView(changeButton);
            }
        }

        pressButton = (Button) findViewById(R.id.press_button);
        pressButton.setOnClickListener(this);


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


    @Override
    public void onClick(View v) {
        rainbowView.setLabelColor(Color.MAGENTA);
        rainbowView.setCenterText("¡Orale!");
        float angle = rainbowView.getCurrentLevelAngle();
        if(rainbowView.getGoalAngle() >= angle + 30) {
            rainbowView.setCurrentLevelAngle(angle + 30);
        } else {
            rainbowView.setCurrentLevelAngle(rainbowView.getGoalAngle());
        }
    }
}
