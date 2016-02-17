package com.detroitlabs.kyleofori.doomrainbow;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RainbowView rainbowView;
    private Button pressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rainbowView = (RainbowView) findViewById(R.id.rainbow_view);

        LayoutInflater layoutInflater = getLayoutInflater();
        String tag;
        for(int i = 0; i <= 1; i++) {
            if(i == 0) {
                tag = "-";
            } else {
                tag = "+";
            }
            View tagView = layoutInflater.inflate(R.layout.tag_layout, rainbowView, false);

            TextView tagTextView = (TextView) tagView.findViewById(R.id.tagTextView);
            tagTextView.setText(tag);
            //This is where a view is added as a child to our view group!
            rainbowView.addView(tagView);
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
        rainbowView.setCircleColor(Color.RED);
        rainbowView.setLabelColor(Color.MAGENTA);
        rainbowView.setCenterText("¡Orale!");
        float angle = rainbowView.getCurrentLevelAngle();
        angle += 30;
        rainbowView.setCurrentLevelAngle(angle);
        rainbowView.increaseGoal();
    }
}
