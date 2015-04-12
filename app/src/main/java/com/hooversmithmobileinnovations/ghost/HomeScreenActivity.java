package com.hooversmithmobileinnovations.ghost;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;

public class HomeScreenActivity extends Activity {

    Drawable blueGhost, redGhost, greenGhost, orangeGhost;
    Button buttonLocalGame, buttonP2PGame, buttonRules;
    CyclicTransitionDrawable ctd;
    ImageView ghostImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        blueGhost = getResources().getDrawable(R.drawable.blueghost);
        redGhost = getResources().getDrawable(R.drawable.redghost);
        greenGhost = getResources().getDrawable(R.drawable.greenghost);
        orangeGhost = getResources().getDrawable(R.drawable.orangeghost);

        buttonLocalGame = (Button)findViewById(R.id.buttonLocalGame);
        buttonP2PGame = (Button)findViewById(R.id.buttonP2PGame);
        buttonRules = (Button)findViewById(R.id.buttonRules);

        ctd = new CyclicTransitionDrawable(new Drawable[] {blueGhost,redGhost,greenGhost,orangeGhost});

        ghostImage = (ImageView)findViewById(R.id.imageViewGhost);

        ghostImage.setImageDrawable(ctd);

        ctd.startTransition(1000, 3000);
    }

    public void onButtonClickedHomeScreen(View v)
    {
        if(v == buttonLocalGame){
            //go to player selection local screen activity
            startActivity(new Intent(this, PlayerSelectionLocalScreenActivity.class));
        }else if(v == buttonP2PGame){
            //go to player selection p2p screen activity

        }else if(v == buttonRules){
            //display game rules

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
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
}
