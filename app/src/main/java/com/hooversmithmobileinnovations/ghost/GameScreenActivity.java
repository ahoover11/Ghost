package com.hooversmithmobileinnovations.ghost;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Vibrator;


public class GameScreenActivity extends Activity {
    TextView currentLetterTextView;
    TextView currentWordTextView;

    int currentPlayer;
    int maxPlayerNumber;
    int numberOfPlayers;
    TextView playerScoreTextView[];
    TextView playerNameTextView[];
    String playerScore[];


    private Vibrator myVib;
    String currentWord;
    String currentLetter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);
        maxPlayerNumber = 4;
        playerScore = new String[maxPlayerNumber];

        ///////////////////////////////
        for (int i = 0; i <maxPlayerNumber;i++)
        {
            playerScore[i] = "";
        }
        currentLetter = "";
        currentWord = "";

        currentPlayer = 0;
         //TODO have this from input
        numberOfPlayers = 4;
        //TODO
    ///////////////////////////////////////////


        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        initialSetup(); //set default values
    }

    private void initialSetup()
    {

        currentLetterTextView = (TextView) findViewById(R.id.textViewCurrentLetter);
        currentWordTextView = (TextView) findViewById(R.id.textViewCurrentWord);
        playerScoreTextView = new TextView[maxPlayerNumber];
        playerScoreTextView[0] = (TextView) findViewById(R.id.textViewPlayer1Score);
        playerScoreTextView[1] = (TextView) findViewById(R.id.textViewPlayer2Score);
        playerScoreTextView[2] = (TextView) findViewById(R.id.textViewPlayer3Score);
        playerScoreTextView[3] = (TextView) findViewById(R.id.textViewPlayer4Score);

        playerNameTextView = new TextView[maxPlayerNumber];
        playerNameTextView[0] = (TextView) findViewById(R.id.textViewPlayer1Name);
        playerNameTextView[1] = (TextView) findViewById(R.id.textViewPlayer2Name);
        playerNameTextView[2] = (TextView) findViewById(R.id.textViewPlayer3Name);
        playerNameTextView[3] = (TextView) findViewById(R.id.textViewPlayer4Name);
        currentLetterTextView.setText(currentLetter);
        currentWordTextView.setText(currentWord);
        for(int i = 0; i < maxPlayerNumber; i++)
        {
            playerScoreTextView[i].setText(playerScore[i]);
        }
        for (int i = numberOfPlayers; i < maxPlayerNumber;i++)
        {
            playerScoreTextView[i].setBackgroundColor(Color.TRANSPARENT);
            playerNameTextView[i].setBackgroundColor(Color.TRANSPARENT);
            playerNameTextView[i].setText("");
        }
        playerTurn(currentPlayer);
    }
    public void LetterClicked(View v)
    {

        myVib.vibrate(80); //haptic feedback for key press
        Resources res = getResources();
        currentLetter=  res.getResourceEntryName(v.getId());
        currentLetterTextView.setText(currentLetter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_screen, menu);
        return true;
    }

    public void onSubmit(View v)
    {
        if (!currentLetter.equals("")) {
            currentWord += currentLetter;

            currentWordTextView.setText(currentWord);
            currentLetterTextView.setText("");
            if (currentWord.length() >= 3) {
                if (validWord(currentWord)) //if a word is completed start the next round
                {

                } else {
                    currentPlayer = (currentPlayer + 1) % numberOfPlayers;
                    playerTurn(currentPlayer);
                }
            } else {
                currentPlayer = (currentPlayer + 1) % numberOfPlayers;
                playerTurn(currentPlayer);
            }
        }
        currentLetter = "";//Reset current Letter;
    }
    private void playerTurn(int player)
    {
        //TODO Change if turn order different
        for (int i = 0; i < numberOfPlayers; i++)
        {
            playerNameTextView[i].setTextColor(Color.BLACK);
        }
        playerNameTextView[player].setTextColor(Color.BLUE);
    }

    private boolean validWord(String word)
    {
        return false; /// Check whether current word is a valid word
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
