package com.hooversmithmobileinnovations.ghost;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class PlayerSelectionLocalScreenActivity extends Activity {

    Button buttonLanguage, buttonStartGame; //Button objects
    ImageView imageViewPlayer1, imageViewPlayer2, imageViewPlayer3, imageViewPlayer4; //ImageView objects
    Drawable blueGhost, redGhost, greenGhost, orangeGhost, grayGhost, aiPlayer1, aiPlayer2, aiPlayer3, aiPlayer4; //Drawable objects for player types
    EditText[] playerNamesEditText; //EditText array to store player names
    String[] playerNames, playerTypes; //String arrays to store player names and player types
    int[] playerNumbers; //Int array to store player numbers
    int numberOfPlayers; //Int that reflects number of active players
    int cycleCount1, cycleCount2, cycleCount3, cycleCount4; //Ints used to cycle through player types

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_selection_local_screen);

        playerNames = new String[4];
        playerTypes = new String[4];
        playerNumbers = new int[4];


        for(int i = 0; i < 4; i++){
            playerNames[i] = "";
            playerTypes[i] = "";
            playerNumbers[i] = 0;
        }

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
        aiPlayer1 = getResources().getDrawable(R.drawable.aiblue);
        aiPlayer2 = getResources().getDrawable(R.drawable.aired);
        aiPlayer3 = getResources().getDrawable(R.drawable.aigreen);
        aiPlayer4 = getResources().getDrawable(R.drawable.aiorange);

        playerNamesEditText = new EditText[4];
        playerNamesEditText[0] = (EditText)findViewById(R.id.editTextPlayer1Name);
        playerNamesEditText[1] = (EditText)findViewById(R.id.editTextPlayer2Name);
        playerNamesEditText[2] = (EditText)findViewById(R.id.editTextPlayer3Name);
        playerNamesEditText[3] = (EditText)findViewById(R.id.editTextPlayer4Name);

        if(savedInstanceState == null ) {
            imageViewPlayer1.setImageDrawable(grayGhost);
            playerNamesEditText[0].setText("Player 1");
            //playerNamesEditText[0].setVisibility(View.INVISIBLE);
           // imageViewPlayer2.setImageDrawable(grayGhost);
            imageViewPlayer1.setImageDrawable(blueGhost);
           // playerNamesEditText[0].setVisibility(View.VISIBLE)
            playerNamesEditText[1].setText("Player 2");
            //playerNamesEditText[1].setVisibility(View.INVISIBLE);
           // imageViewPlayer3.setImageDrawable(grayGhost);
            playerNamesEditText[2].setText("Player 3");
            playerNamesEditText[2].setVisibility(View.INVISIBLE);
            imageViewPlayer4.setImageDrawable(grayGhost);
            playerNamesEditText[3].setText("Player 4");
            playerNamesEditText[3].setVisibility(View.INVISIBLE);
            cycleCount1= 1;//Set default to human player
;
            cycleCount2= 2;//Set default to AI player
            imageViewPlayer2.setImageDrawable(aiPlayer2);

            cycleCount3 = cycleCount4 = 0;
        }
    }

    public void onButtonClickedLocalSelectionScreen(View v)
    {
        if(v == buttonLanguage){
            //Select a language
        }else if(v == buttonStartGame){
            //Populate variables pertaining to player information passed to next activity
            establishPassedVariables();

            //Start the game activity if more than 2 players activated
            if(numberOfPlayers >= 2){
                //Start the game activity and pass necessary player information
                Intent intent = new Intent(this, GameScreenActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArray("playerNames", playerNames);
                bundle.putStringArray("playerTypes", playerTypes);
                bundle.putIntArray("playerNumbers", playerNumbers);
                bundle.putInt("numberOfPlayers", numberOfPlayers);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }else{
                //Inform the player that they need at least 2 active players to start the game
                Toast toast = Toast.makeText(this, "You need at least 2 active players to start the game!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

        }
    }

    public void establishPassedVariables(){

        //Clear out player information storage
        numberOfPlayers = 0;
        for(int i = 0; i < 4; i++){
            playerNames[i] = "";
            playerTypes[i] = "";
            playerNumbers[i] = 0;
        }

        //Counter to keep track of the number of players
        int count = 0;

        //Populate values to be passed for player 1
        if(imageViewPlayer1.getDrawable().getConstantState().equals(blueGhost.getConstantState()) || imageViewPlayer1.getDrawable().getConstantState().equals(aiPlayer1.getConstantState())){
            playerNames[count] = playerNamesEditText[0].getText().toString();
            playerNumbers[count] = 0;
            if(imageViewPlayer1.getDrawable().getConstantState().equals(blueGhost.getConstantState())){
                playerTypes[count] = "HUMAN";
            }else{
                playerTypes[count] = "AI";
            }
            count++;
        }

        //Populate values to be passed for player 2
        if(imageViewPlayer2.getDrawable().getConstantState().equals(redGhost.getConstantState()) || imageViewPlayer2.getDrawable().getConstantState().equals(aiPlayer2.getConstantState())) {
            playerNames[count] = playerNamesEditText[1].getText().toString();
            playerNumbers[count] = 1;
            if (imageViewPlayer2.getDrawable().getConstantState().equals(redGhost.getConstantState())) {
                playerTypes[count] = "HUMAN";
            }else {
                playerTypes[count] = "AI";
            }
            count++;
        }

        //Populate values to be passed for player 3
        if(imageViewPlayer3.getDrawable().getConstantState().equals(greenGhost.getConstantState()) || imageViewPlayer3.getDrawable().getConstantState().equals(aiPlayer3.getConstantState())) {
            playerNames[count] = playerNamesEditText[2].getText().toString();
            playerNumbers[count] = 2;
            if (imageViewPlayer3.getDrawable().getConstantState().equals(greenGhost.getConstantState())) {
                playerTypes[count] = "HUMAN";
            }else {
                playerTypes[count] = "AI";
            }
            count++;
        }

        //Populate values to be passed for player 4
        if(imageViewPlayer4.getDrawable().getConstantState().equals(orangeGhost.getConstantState()) || imageViewPlayer4.getDrawable().getConstantState().equals(aiPlayer4.getConstantState())) {
            playerNames[count] = playerNamesEditText[3].getText().toString();
            playerNumbers[count] = 3;
            if (imageViewPlayer4.getDrawable().getConstantState().equals(orangeGhost.getConstantState())) {
                playerTypes[count] = "HUMAN";
            }else {
                playerTypes[count] = "AI";
            }
            count++;
        }

        //Set numberOfPlayers to amount of active players
        numberOfPlayers = count;

    }

    public void cyclePlayerType(View v){

        //Circulate through player types when clicking imageViews
        if(v == imageViewPlayer1){
            cycleCount1++;
            if(cycleCount1 % 3 == 0){
                imageViewPlayer1.setImageDrawable(grayGhost);
                //playerNamesEditText[0].setText("Player 1");
                playerNamesEditText[0].setVisibility(View.INVISIBLE);
            }else if(cycleCount1 % 3 == 1){
                imageViewPlayer1.setImageDrawable(blueGhost);
                playerNamesEditText[0].setVisibility(View.VISIBLE);
                //playerNamesEditText[0].setText(R.string.Player1);
            }else if(cycleCount1 % 3 == 2){
                imageViewPlayer1.setImageDrawable(aiPlayer1);
            }
        }else if(v == imageViewPlayer2){
            cycleCount2++;
            if(cycleCount2 % 3 == 0){
                imageViewPlayer2.setImageDrawable(grayGhost);
                //playerNamesEditText[1].setText("Player 2");
                playerNamesEditText[1].setVisibility(View.INVISIBLE);
            }else if(cycleCount2 % 3 == 1){
                imageViewPlayer2.setImageDrawable(redGhost);
                playerNamesEditText[1].setVisibility(View.VISIBLE);
                //playerNamesEditText[1].setText(R.string.Player2);
            }else if(cycleCount2 % 3 == 2){
                imageViewPlayer2.setImageDrawable(aiPlayer2);
            }
        }else if(v == imageViewPlayer3){
            cycleCount3++;
            if(cycleCount3 % 3 == 0){
                imageViewPlayer3.setImageDrawable(grayGhost);
                //playerNamesEditText[2].setText("Player 3");
                playerNamesEditText[2].setVisibility(View.INVISIBLE);
            }else if(cycleCount3 % 3 == 1){
                imageViewPlayer3.setImageDrawable(greenGhost);
                playerNamesEditText[2].setVisibility(View.VISIBLE);
                //playerNamesEditText[2].setText(R.string.Player3);
            }else if(cycleCount3 % 3 == 2){
                imageViewPlayer3.setImageDrawable(aiPlayer3);
            }
        }else if(v == imageViewPlayer4){
            cycleCount4++;
            if(cycleCount4 % 3 == 0){
                imageViewPlayer4.setImageDrawable(grayGhost);
                //playerNamesEditText[3].setText("Player 4");
                playerNamesEditText[3].setVisibility(View.INVISIBLE);
            }else if(cycleCount4 % 3 == 1){
                imageViewPlayer4.setImageDrawable(orangeGhost);
                playerNamesEditText[3].setVisibility(View.VISIBLE);
                //playerNamesEditText[3].setText(R.string.Player4);
            }else if(cycleCount4 % 3 == 2){
                imageViewPlayer4.setImageDrawable(aiPlayer4);
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("cycleCount1", cycleCount1);
        outState.putInt("cycleCount2", cycleCount2);
        outState.putInt("cycleCount3", cycleCount3);
        outState.putInt("cycleCount4", cycleCount4);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        cycleCount1 = savedInstanceState.getInt("cycleCount1");
        cycleCount2 = savedInstanceState.getInt("cycleCount2");
        cycleCount3 = savedInstanceState.getInt("cycleCount3");
        cycleCount4 = savedInstanceState.getInt("cycleCount4");


        if (cycleCount1 % 3 == 0) {
            imageViewPlayer1.setImageDrawable(grayGhost);
            //playerNamesEditText[0].setText("Player 1");
            playerNamesEditText[0].setVisibility(View.INVISIBLE);
        } else if (cycleCount1 % 3 == 1) {
            imageViewPlayer1.setImageDrawable(blueGhost);
            playerNamesEditText[0].setVisibility(View.VISIBLE);
            //playerNamesEditText[0].setText(R.string.Player1);
        } else if (cycleCount1 % 3 == 2) {
            imageViewPlayer1.setImageDrawable(aiPlayer1);
        }

            if(cycleCount2 % 3 == 0){
                imageViewPlayer2.setImageDrawable(grayGhost);
                //playerNamesEditText[1].setText("Player 2");
                playerNamesEditText[1].setVisibility(View.INVISIBLE);
            }else if(cycleCount2 % 3 == 1){
                imageViewPlayer2.setImageDrawable(redGhost);
                playerNamesEditText[1].setVisibility(View.VISIBLE);
                //playerNamesEditText[1].setText(R.string.Player2);
            }else if(cycleCount2 % 3 == 2){
                imageViewPlayer2.setImageDrawable(aiPlayer2);
            }



            if(cycleCount3 % 3 == 0){
                imageViewPlayer3.setImageDrawable(grayGhost);
                //playerNamesEditText[2].setText("Player 3");
                playerNamesEditText[2].setVisibility(View.INVISIBLE);
            }else if(cycleCount3 % 3 == 1){
                imageViewPlayer3.setImageDrawable(greenGhost);
                playerNamesEditText[2].setVisibility(View.VISIBLE);
                //playerNamesEditText[2].setText(R.string.Player3);
            }else if(cycleCount3 % 3 == 2){
                imageViewPlayer3.setImageDrawable(aiPlayer3);
            }

            if(cycleCount4 % 3 == 0){
                imageViewPlayer4.setImageDrawable(grayGhost);
                //playerNamesEditText[3].setText("Player 4");
                playerNamesEditText[3].setVisibility(View.INVISIBLE);
            }else if(cycleCount4 % 3 == 1){
                imageViewPlayer4.setImageDrawable(orangeGhost);
                playerNamesEditText[3].setVisibility(View.VISIBLE);
                //playerNamesEditText[3].setText(R.string.Player4);
            }else if(cycleCount4 % 3 == 2){
                imageViewPlayer4.setImageDrawable(aiPlayer4);
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
