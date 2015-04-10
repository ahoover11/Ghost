package com.hooversmithmobileinnovations.ghost;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.view.View;

public class HomeScreenActivity extends Activity {

    Drawable blueghost, redghost, greenghost, orangeghost;
    CyclicTransitionDrawable ctd;
    ImageView ghostImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        blueghost = getResources().getDrawable(R.drawable.blueghost);
        redghost = getResources().getDrawable(R.drawable.redghost);
        greenghost = getResources().getDrawable(R.drawable.greenghost);
        orangeghost = getResources().getDrawable(R.drawable.orangeghost);

        ctd = new CyclicTransitionDrawable(new Drawable[] {blueghost,redghost,greenghost,orangeghost});

        ghostImage = (ImageView)findViewById(R.id.imageViewGhost);

        ghostImage.setImageDrawable(ctd);

        ctd.startTransition(1000, 3000);

        //startActivity(new Intent(this, GameScreenActivity.class));
    }

public void onLocalGameSelected(View v)
{
    startActivity(new Intent(this, GameScreenActivity.class));//go to game screen activity
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
