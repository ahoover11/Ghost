package com.hooversmithmobileinnovations.ghost;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


public class PlayerSelectionLocalScreenActivity extends Activity {

    Button buttonLanguage, buttonStartGame;
    ImageView imageViewPlayer1, imageViewPlayer2, imageViewPlayer3, imageViewPlayer4;
    Drawable blueGhost, redGhost, greenGhost, orangeGhost, grayGhost, aiPlayer1, aiPlayer2, aiPlayer3, aiPlayer4;
    EditText[] playerNames;
    int cycleCount1, cycleCount2, cycleCount3, cycleCount4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_selection_local_screen);

        buttonLanguage = (Button)findViewById(R.id.buttonLanguage);
        buttonStartGame = (Button)findViewById(R.id.buttonStartGame);

        imageViewPlayer1 = (ImageView)findViewById(R.id.imageViewBlueGhost);
        imageViewPlayer2 = (ImageView)findViewById(R.id.imageViewRedGhost);
        imageViewPlayer3 = (ImageView)findViewById(R.id.imageViewGreenGhost);
        imageViewPlayer4 = (ImageView)findViewById(R.id.imageViewOrangeGhost);

        blueGhost = getResources().getDrawable(R.drawable.blueghost);
        redGhost = getResources().getDrawable(R.drawable.redghost);
        greenGhost = getResources().getDrawable(R.drawable.greenghost);
        orangeGhost = getResources().getDrawable(R.drawable.orangeghost);
        grayGhost = getResources().getDrawable(R.drawable.grayghost);
        aiPlayer1 = getResources().getDrawable(R.drawable.aiplayer1);
        aiPlayer2 = getResources().getDrawable(R.drawable.aiplayer2);
        aiPlayer3 = getResources().getDrawable(R.drawable.aiplayer3);
        aiPlayer4 = getResources().getDrawable(R.drawable.aiplayer4);

        playerNames = new EditText[4];
        playerNames[0] = (EditText)findViewById(R.id.editTextPlayer1Name);
        playerNames[1] = (EditText)findViewById(R.id.editTextPlayer2Name);
        playerNames[2] = (EditText)findViewById(R.id.editTextPlayer3Name);
        playerNames[3] = (EditText)findViewById(R.id.editTextPlayer4Name);
        playerNames[0].setEnabled(false);
        playerNames[0].setInputType(InputType.TYPE_NULL);
        playerNames[0].setFocusable(false);
        playerNames[1].setEnabled(false);
        playerNames[1].setInputType(InputType.TYPE_NULL);
        playerNames[1].setFocusable(false);
        playerNames[2].setEnabled(false);
        playerNames[2].setInputType(InputType.TYPE_NULL);
        playerNames[2].setFocusable(false);
        playerNames[3].setEnabled(false);
        playerNames[3].setInputType(InputType.TYPE_NULL);
        playerNames[3].setFocusable(false);
    }

    public void onButtonClickedLocalSelectionScreen(View v)
    {
        if(v == buttonLanguage){
            //select a language
        }else if(v == buttonStartGame){
            //go to game screen activity
            startActivity(new Intent(this, GameScreenActivity.class));
        }
    }

    public void cyclePlayerType(View v){

        if(v == imageViewPlayer1){
            cycleCount1++;
            if(cycleCount1 % 3 == 0){
                imageViewPlayer1.setImageDrawable(grayGhost);
                playerNames[0].setText("");
                playerNames[0].setEnabled(false);
                playerNames[0].setInputType(InputType.TYPE_NULL);
                playerNames[0].setFocusable(false);
            }else if(cycleCount1 % 3 == 1){
                imageViewPlayer1.setImageDrawable(blueGhost);
                playerNames[0].setEnabled(true);
                playerNames[0].setInputType(InputType.TYPE_CLASS_TEXT);
                playerNames[0].setFocusable(true);
            }else if(cycleCount1 % 3 == 2){
                imageViewPlayer1.setImageDrawable(aiPlayer1);
            }
        }else if(v == imageViewPlayer2){
            cycleCount2++;
            if(cycleCount2 % 3 == 0){
                imageViewPlayer2.setImageDrawable(grayGhost);
                playerNames[1].setText("");
                playerNames[1].setEnabled(false);
                playerNames[1].setInputType(InputType.TYPE_NULL);
                playerNames[1].setFocusable(false);
            }else if(cycleCount2 % 3 == 1){
                imageViewPlayer2.setImageDrawable(redGhost);
                playerNames[1].setEnabled(true);
                playerNames[1].setInputType(InputType.TYPE_CLASS_TEXT);
                playerNames[1].setFocusable(true);
            }else if(cycleCount2 % 3 == 2){
                imageViewPlayer2.setImageDrawable(aiPlayer2);
            }
        }else if(v == imageViewPlayer3){
            cycleCount3++;
            if(cycleCount3 % 3 == 0){
                imageViewPlayer3.setImageDrawable(grayGhost);
                playerNames[2].setText("");
                playerNames[2].setEnabled(false);
                playerNames[2].setInputType(InputType.TYPE_NULL);
                playerNames[2].setFocusable(false);
            }else if(cycleCount3 % 3 == 1){
                imageViewPlayer3.setImageDrawable(greenGhost);
                playerNames[2].setEnabled(true);
                playerNames[2].setInputType(InputType.TYPE_CLASS_TEXT);
                playerNames[2].setFocusable(true);
            }else if(cycleCount3 % 3 == 2){
                imageViewPlayer3.setImageDrawable(aiPlayer3);
            }
        }else if(v == imageViewPlayer4){
            cycleCount4++;
            if(cycleCount4 % 3 == 0){
                imageViewPlayer4.setImageDrawable(grayGhost);
                playerNames[3].setText("");
                playerNames[3].setEnabled(false);
                playerNames[3].setInputType(InputType.TYPE_NULL);
                playerNames[3].setFocusable(false);
            }else if(cycleCount4 % 3 == 1){
                imageViewPlayer4.setImageDrawable(orangeGhost);
                playerNames[3].setEnabled(true);
                playerNames[3].setInputType(InputType.TYPE_CLASS_TEXT);
                playerNames[3].setFocusable(true);
            }else if(cycleCount4 % 3 == 2){
                imageViewPlayer4.setImageDrawable(aiPlayer4);
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player_selection_local_screen, menu);
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
