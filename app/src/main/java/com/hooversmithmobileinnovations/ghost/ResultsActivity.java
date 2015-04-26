package com.hooversmithmobileinnovations.ghost;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class ResultsActivity extends Activity {

    int numberOfPlayers, playerNumbers[], playerRanks[]; //Ints to store the total number of players, player numbers, and player ranks
    String playerScores[], playerNames[], playerTypes[]; //Strings to store player scores, player names, and player types
    ImageView playersImageView[]; //ImageView objects
    TextView playerNamesTextView[], playerScoresTextView[]; //TextView objects
    Button newGameButton, homeScreenButton; //Button objects
    Drawable blueGhost, redGhost, greenGhost, orangeGhost, aiBlue, aiRed, aiGreen, aiOrange; //Drawable objects for player types
    final static int MAX_NUMBER_PLAYERS = 4; //Int that reflects the maximum possible number of players

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        newGameButton = (Button)findViewById(R.id.buttonNewGame);
        homeScreenButton = (Button)findViewById(R.id.buttonHomeScreen);

        blueGhost = getResources().getDrawable(R.drawable.blueghost);
        redGhost = getResources().getDrawable(R.drawable.redghost);
        greenGhost = getResources().getDrawable(R.drawable.greenghost);
        orangeGhost = getResources().getDrawable(R.drawable.orangeghost);
        aiBlue = getResources().getDrawable(R.drawable.aiblue);
        aiRed = getResources().getDrawable(R.drawable.aired);
        aiGreen = getResources().getDrawable(R.drawable.aigreen);
        aiOrange = getResources().getDrawable(R.drawable.aiorange);

        playersImageView = new ImageView[MAX_NUMBER_PLAYERS];
        playersImageView[0] = (ImageView)findViewById(R.id.imageViewBlueGhost);
        playersImageView[1] = (ImageView)findViewById(R.id.imageViewRedGhost);
        playersImageView[2] = (ImageView)findViewById(R.id.imageViewGreenGhost);
        playersImageView[3] = (ImageView)findViewById(R.id.imageViewOrangeGhost);

        playerNamesTextView = new TextView[MAX_NUMBER_PLAYERS];
        playerNamesTextView[0] = (TextView)findViewById(R.id.textViewPlayer1Name);
        playerNamesTextView[1] = (TextView)findViewById(R.id.textViewPlayer2Name);
        playerNamesTextView[2] = (TextView)findViewById(R.id.textViewPlayer3Name);
        playerNamesTextView[3] = (TextView)findViewById(R.id.textViewPlayer4Name);

        playerScoresTextView = new TextView[MAX_NUMBER_PLAYERS];
        playerScoresTextView[0] = (TextView)findViewById(R.id.textViewPlayer1Score);
        playerScoresTextView[1] = (TextView)findViewById(R.id.textViewPlayer2Score);
        playerScoresTextView[2] = (TextView)findViewById(R.id.textViewPlayer3Score);
        playerScoresTextView[3] = (TextView)findViewById(R.id.textViewPlayer4Score);

        //Initialize arrays to store player information
        playerNames = new String[MAX_NUMBER_PLAYERS];
        playerTypes = new String[MAX_NUMBER_PLAYERS];
        playerNumbers = new int[MAX_NUMBER_PLAYERS];
        playerScores = new String[MAX_NUMBER_PLAYERS];
        playerRanks = new int[MAX_NUMBER_PLAYERS];
        numberOfPlayers = 0;

        //Retrieve the passed in data from player selection screen
        Intent intent = getIntent();
        if(intent != null) {
            Bundle bundle = intent.getExtras();
            playerNames = bundle.getStringArray("playerNames");
            playerTypes = bundle.getStringArray("playerTypes");
            playerNumbers = bundle.getIntArray("playerNumbers");
            playerScores = bundle.getStringArray("playerScores");
            playerRanks = bundle.getIntArray("playerRanks");
            numberOfPlayers = bundle.getInt("numberOfPlayers");
        }

        for(int i = 0; i < MAX_NUMBER_PLAYERS; i++){
            playersImageView[i].setVisibility(View.INVISIBLE);
            playerNamesTextView[i].setVisibility(View.INVISIBLE);
            playerScoresTextView[i].setVisibility(View.INVISIBLE);
        }

        //Populate the information fields
        populateFields();
    }

    public void populateFields(){
        int count = 0;
        for(int i = numberOfPlayers - 1; i >= 0; i--){
            int rank = playerRanks[i];
            if(playerTypes[rank].equals("HUMAN")){
                if(playerNumbers[rank] == 0){
                    playersImageView[count].setImageDrawable(blueGhost);
                }else if(playerNumbers[rank] == 1){
                    playersImageView[count].setImageDrawable(redGhost);
                }else if(playerNumbers[rank] == 2){
                    playersImageView[count].setImageDrawable(greenGhost);
                }else if(playerNumbers[rank] == 3){
                    playersImageView[count].setImageDrawable(orangeGhost);
                }
            }else if(playerTypes[rank].equals("AI")){
                if(playerNumbers[rank] == 0){
                    playersImageView[count].setImageDrawable(aiBlue);
                }else if(playerNumbers[rank] == 1){
                    playersImageView[count].setImageDrawable(aiRed);
                }else if(playerNumbers[rank] == 2){
                    playersImageView[count].setImageDrawable(aiGreen);
                }else if(playerNumbers[rank] == 3){
                    playersImageView[count].setImageDrawable(aiOrange);
                }
            }
            playerNamesTextView[count].setText(playerNames[rank]);
            playerScoresTextView[count].setText(playerScores[rank]);
            playersImageView[count].setVisibility(View.VISIBLE);
            playerNamesTextView[count].setVisibility(View.VISIBLE);
            playerScoresTextView[count].setVisibility(View.VISIBLE);
            count++;
        }
    }

    public void onButtonClickedResultsScreen(View v)
    {
        if(v == newGameButton){
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
        }else if(v == homeScreenButton){
            //Start the home screen activity
            startActivity(new Intent(this, HomeScreenActivity.class));
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_results, menu);
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
