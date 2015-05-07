package com.hooversmithmobileinnovations.ghost;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;

import java.util.Random;

public class HomeScreenActivity extends Activity {

    Drawable blueGhost, redGhost, greenGhost, orangeGhost;
    Button localGameButton, p2pGameButton, rulesButton;
    CyclicTransitionDrawable ctd;
    ImageView ghostImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        /////////////////////////////////////////////////////////////////////
        //Load Database for first run
        MyDBHandler dbHandler = new MyDBHandler(this,null,null,1);
        dbHandler.checkWord("Initiate");//Make sure it actually loads
        dbHandler.close();
        /////////////////////////////////////////////////////////////////////
        blueGhost = getResources().getDrawable(R.drawable.blueghost);
        redGhost = getResources().getDrawable(R.drawable.redghost);
        greenGhost = getResources().getDrawable(R.drawable.greenghost);
        orangeGhost = getResources().getDrawable(R.drawable.orangeghost);

        localGameButton = (Button)findViewById(R.id.buttonNewGame);
        // p2pGameButton = (Button)findViewById(R.id.buttonP2PGame);
        rulesButton = (Button)findViewById(R.id.buttonRules);

        ctd = new CyclicTransitionDrawable(new Drawable[] {blueGhost,redGhost,greenGhost,orangeGhost});

        ghostImageView = (ImageView)findViewById(R.id.imageViewGhost);

        ghostImageView.setImageDrawable(ctd);

        ctd.startTransition(1500, 1750);
    }

    public void onButtonClickedHomeScreen(View v)
    {
        if(v == localGameButton){
            //go to player selection local screen activity
            startActivity(new Intent(this, PlayerSelectionLocalScreenActivity.class));
        }else if(v == p2pGameButton){
            //go to player selection p2p screen activity

        }else if(v == rulesButton){
            //display game rules
            startActivity(new Intent(this, RulesActivity.class));
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
