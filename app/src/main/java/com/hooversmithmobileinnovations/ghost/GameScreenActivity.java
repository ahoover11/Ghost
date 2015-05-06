package com.hooversmithmobileinnovations.ghost;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;
import android.os.Vibrator;
import android.widget.Button;
import android.view.Window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class GameScreenActivity extends Activity {

    TextView currentLetterTextView, currentWordTextView, playerScoreTextView[], playerNameTextView[]; //TextViews objects
    Drawable blueGhost, redGhost, greenGhost, orangeGhost, aiBlue, aiRed, aiGreen, aiOrange; //Drawable objects for player types
    int currentPlayer, numberOfPlayers, playerTurn, previousPlayer, playerNumbers[], playerRanks[],dropOutCounter; //Ints to store the current player, the total number of players, the player turn, the previous player, player numbers, player ranks, and counter used to keep track of players as they drop out of the gam
    String currentWord, currentLetter, playerScores[], playerNames[], playerTypes[]; //Strings to store the current word, current letter, player scores, player names, and player types
    boolean playersInGame[]; //Boolean array that reflects active players
    Vibrator myVib; //Vibrator object for haptic feedback
    MyDBHandler dbHandler; //Database object used for dictionary lookup
    final static int MAX_NUMBER_PLAYERS = 4; //Int that reflects the maximum possible number of players
    final static int CHALLENGE_REQUEST = 1; //Int used to signify a challenge
    double smartGuess = 0.7;
    double dumbGuess = 0.95;//todo input or final
    double challengeThreshold = 0.85;
    CountDownTimer timer;
    long time = 30000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        //Initialize some default values
        if(savedInstanceState == null ) //for first time
        {
            playerTurn = 0;
            currentLetter = "";
            currentWord = "";
            currentPlayer = 0;
            previousPlayer = -1;


            //Initialize arrays to store player information
            playerNames = new String[MAX_NUMBER_PLAYERS];
            playerTypes = new String[MAX_NUMBER_PLAYERS];
            playerNumbers = new int[MAX_NUMBER_PLAYERS];
            playerScores = new String[MAX_NUMBER_PLAYERS];
            playersInGame = new boolean[MAX_NUMBER_PLAYERS];
            playerRanks = new int[MAX_NUMBER_PLAYERS];

            //Retrieve the passed in data from player selection screen
            Intent intent = getIntent();
            if(intent != null) {
                Bundle bundle = intent.getExtras();
                playerNames = bundle.getStringArray("playerNames");
                playerTypes = bundle.getStringArray("playerTypes");
                playerNumbers = bundle.getIntArray("playerNumbers");
                numberOfPlayers = bundle.getInt("numberOfPlayers");

                //Populate the playersInGame array to reflect the active players
                for(int i = 0; i < numberOfPlayers; i++){
                    playersInGame[i] = true;
                }

                dropOutCounter = numberOfPlayers - 1;

                //Populate the playerScores array to reflect starting score (ie no letters of GHOST)
                for (int i = 0; i < MAX_NUMBER_PLAYERS; i++)
                {
                    playerScores[i] = "";
                }
            }

        }else //Restore state
        {
            playerTurn = savedInstanceState.getInt("playerTurn");
            currentLetter = savedInstanceState.getString("currentLetter");
            currentWord = savedInstanceState.getString("currentWord");
            currentPlayer = savedInstanceState.getInt("currentPlayer");
            previousPlayer = savedInstanceState.getInt("previousPlayer");
            dropOutCounter = savedInstanceState.getInt("dropOutCounter");

            playerNames = savedInstanceState.getStringArray("playerNames");
            playerTypes = savedInstanceState.getStringArray("playerTypes");
            playerNumbers = savedInstanceState.getIntArray("playerNumbers");
            numberOfPlayers = savedInstanceState.getInt("numberOfPlayers");

            playersInGame = savedInstanceState.getBooleanArray("playersInGame");
            playerScores = savedInstanceState.getStringArray("playerScores");

            playersInGame = savedInstanceState.getBooleanArray("playersInGame");
            playerRanks = savedInstanceState.getIntArray("playerRanks");

            time = savedInstanceState.getLong("time");
            timer = (CountDownTimer) getLastNonConfigurationInstance();
            if(timer != null) {
                timer.cancel();
            }
        }

        playerScoreTextView = new TextView[MAX_NUMBER_PLAYERS];
        playerScoreTextView[0] = (TextView) findViewById(R.id.textViewPlayer1Score);
        playerScoreTextView[1] = (TextView) findViewById(R.id.textViewPlayer2Score);
        playerScoreTextView[2] = (TextView) findViewById(R.id.textViewPlayer3Score);
        playerScoreTextView[3] = (TextView) findViewById(R.id.textViewPlayer4Score);

        playerNameTextView = new TextView[MAX_NUMBER_PLAYERS];
        playerNameTextView[0] = (TextView) findViewById(R.id.textViewPlayer1Name);
        playerNameTextView[1] = (TextView) findViewById(R.id.textViewPlayer2Name);
        playerNameTextView[2] = (TextView) findViewById(R.id.textViewPlayer3Name);
        playerNameTextView[3] = (TextView) findViewById(R.id.textViewPlayer4Name);

        currentLetterTextView = (TextView) findViewById(R.id.textViewCurrentLetter);
        currentWordTextView = (TextView) findViewById(R.id.textViewCurrentWord);

        currentLetterTextView.setText(currentLetter);
        currentWordTextView.setText(currentWord);


        blueGhost = getResources().getDrawable(R.drawable.blueghost);
        redGhost = getResources().getDrawable(R.drawable.redghost);
        greenGhost = getResources().getDrawable(R.drawable.greenghost);
        orangeGhost = getResources().getDrawable(R.drawable.orangeghost);
        aiBlue = getResources().getDrawable(R.drawable.aiblue);
        aiRed = getResources().getDrawable(R.drawable.aired);
        aiGreen = getResources().getDrawable(R.drawable.aigreen);
        aiOrange = getResources().getDrawable(R.drawable.aiorange);


        dbHandler = new MyDBHandler(this,null,null,1);

        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        //Update textViews to current player scores
        for(int i = 0; i < MAX_NUMBER_PLAYERS; i++)
        {
            playerScoreTextView[i].setText(playerScores[i]);
            playerNameTextView[i].setText(playerNames[i]);
        }

        //Update textViews to reflect active players
        for (int i = numberOfPlayers; i < MAX_NUMBER_PLAYERS; i++)
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

    public void onSubmit(View v)
    {
        if (!currentLetter.equals("")) {
            //Append selected letter to current word and update textViews
            currentWord += currentLetter;
            currentWordTextView.setText(currentWord);
            currentLetterTextView.setText("");

            //Stop the timer
            if(timer != null) {
                timer.cancel();
            }
            time = 30000;

            //Only check if player created a word if length greater than 3
            if (currentWord.length() > 3) {
                //If a word is completed, start the next round
                if (validWord(currentWord.toLowerCase()))
                {
                    //Update variables to prepare for next round
                    boolean gameNotOver = addLetter(currentPlayer);
                    if (gameNotOver)
                    {
                        //Toast.makeText(getBaseContext(), "Word Completed Round Finished", Toast.LENGTH_SHORT).show();
                        roundEndDialog(currentWord);
                    }else
                    {
                        endGame();//End the game and go to results screen
                    }
                } else {
                    //Update fields to prepare for next player's turn
                    previousPlayer = currentPlayer;
                    currentPlayer = nextPlayer(currentPlayer);
                    playerTurn(currentPlayer);
                }
            } else {
                //Update fields to prepare for next player's turn
                previousPlayer = currentPlayer;
                currentPlayer = nextPlayer(currentPlayer);
                playerTurn(currentPlayer);
            }
        }

        //Reset current letter;
        currentLetter = "";
    }
    public void roundEndDialog(String endingWord)
    {
        if(timer != null) {
            timer.cancel();
        }

        previousPlayer = -1;
        currentWord = "";
        currentWordTextView.setText(currentWord);
        currentPlayer = nextPlayer(playerTurn);
        playerTurn++;
        time = 30000;

        //Dialog that depicts which player completed a word
        final Dialog dialog = new Dialog(GameScreenActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.round_over_popup);

        TextView text = (TextView) dialog.findViewById(R.id.textViewFinalWord);
        text.setText(endingWord);
        //ImageView image = (ImageView) dialog.findViewById(R.id.imageViewGhost);
        Button dialogButton = (Button) dialog.findViewById(R.id.popupButton);
        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                playerTurn(currentPlayer);
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false); //disable back button out
        dialog.show();
    }
    public void playerTurn(int player)
    {
        TextView timerTextView = (TextView)findViewById(R.id.textViewTimer);
        timerTextView.setText(Long.toString(time/1000));

        //Set all player names to black if it is not their turn
        for (int i = 0; i < numberOfPlayers; i++)
        {
            playerNameTextView[i].setTextColor(Color.BLACK);
        }

        //Set the active player's name to blue
        playerNameTextView[player].setTextColor(Color.BLUE);


        //Dialog that depicts which player's turn it is
        final Dialog dialog = new Dialog(GameScreenActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.next_turn_popup);

        TextView text = (TextView) dialog.findViewById(R.id.popupPlayerTextView);
        text.setText(playerNames[player]);
        ImageView image = (ImageView) dialog.findViewById(R.id.imageViewGhost);
        switch (playerNumbers[player]) {
            case 0:
                if (playerTypes[player].equals("HUMAN")) {
                    image.setImageDrawable(blueGhost);
                } else {
                    image.setImageDrawable(aiBlue);
                }
                break;
            case 1:
                if (playerTypes[player].equals("HUMAN")) {
                    image.setImageDrawable(redGhost);
                } else {
                    image.setImageDrawable(aiRed);
                }
                break;
            case 2:
                if (playerTypes[player].equals("HUMAN")) {
                    image.setImageDrawable(greenGhost);
                } else {
                    image.setImageDrawable(aiGreen);
                }
                break;
            case 3:
                if (playerTypes[player].equals("HUMAN")) {
                    image.setImageDrawable(orangeGhost);
                } else {
                    image.setImageDrawable(aiOrange);
                }
                break;
        }


        final int aiPlayer = player;

        //If button is clicked, close the custom dialog
        Button dialogButton = (Button) dialog.findViewById(R.id.popupButton);
        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerTypes[aiPlayer].equals("AI"))
                {
                    aiTurn(aiPlayer);
                }

                dialog.dismiss();

                //Start the timer for a human player
                if (playerTypes[aiPlayer].equals("HUMAN")){
                    //Countdown Timer
                    timer = new CountDownTimer(time, 1000) {
                        TextView timerTextView = (TextView)findViewById(R.id.textViewTimer);

                        @Override
                        public void onTick(long millisUntilFinished) {
                            timerTextView.setText(Long.toString(millisUntilFinished / 1000));
                        }

                        @Override
                        public void onFinish() {
                            boolean gameNotOver = addLetter(currentPlayer);
                            if (gameNotOver)
                            {
                                roundEndDialog("Time's Up!");
                            }else
                            {
                                endGame();//End the game and go to results screen
                            }
                            if(timer != null) {
                                timer.cancel();
                            }
                        }
                    }.start();
                }

            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false); //disable back button out
        dialog.show();
    }

    public void aiTurn(int player)
    {
        Random r = new Random();
        char guess= (char) (r.nextInt(26) + 'A');
        boolean challenge = false;
        if (currentWord.length()==0)//Randomly select first letter
        {//Do nothing
        }else if (currentWord.length() == 1)
        {
            List<String> list = dbHandler.getSuggestions(currentWord);
            guess = list.get(r.nextInt(list.size())).toCharArray()[currentWord.length()];
        }else {
            List<String> list = dbHandler.getSuggestions(currentWord);//For suggestions
            if (list.size() == 0&& r.nextDouble()< challengeThreshold) {
                challenge = true;
                onChallenge(currentLetterTextView);
            } else {
                List<String> smartList = new ArrayList<String>(); //For holding potentially good guesses
                List<String> stupidList = new ArrayList<String>();//For holding potentially poor guesses
                for (int i = 0; i < list.size(); i++) {
                    int aiPosition = currentWord.length() % (dropOutCounter + 1);
                    if (list.get(i).length() % (dropOutCounter + 1) - 1 == aiPosition||list.get(i).length()==currentWord.length() ) {
                        stupidList.add(list.get(i));
                    } else {
                        smartList.add(list.get(i));
                    }
                }

                //Shuffle the two lists
                Collections.shuffle(smartList);
                Collections.shuffle(stupidList);

                double randomProb =r.nextDouble();
                if (randomProb < smartGuess) {
                    if (smartList.size() > 0 && smartList.get(0).length() > currentWord.length()) {
                        int minLength = smartList.get(0).length();
                        int numSearch = 5;//How many words to search through
                        if (minLength > numSearch) {
                            minLength = numSearch;//most searched
                        }
                        int index = -1;
                        boolean wordChosen = false;

                        do {
                            index++;
                            boolean noSubwords = true;
                            for (int i = currentWord.length(); i < smartList.get(index).length() - 1; i++) {
                                if (dbHandler.checkWord(smartList.get(index).substring(0, i))) {
                                    noSubwords = false;
                                    break;
                                }
                            }
                            if (noSubwords) {
                                wordChosen = true;
                            }
                        } while (!wordChosen && index < minLength - 1);

                        guess = smartList.get(index).toCharArray()[currentWord.length()];
                    }
                }
                else if (randomProb > smartGuess  && randomProb < dumbGuess&& stupidList.size()> 0 )
                {
                    if (stupidList.get(0).length() > currentWord.length())
                        guess = stupidList.get(0).toCharArray()[currentWord.length()];
                }

            }
        }

        if (!challenge) {
            currentLetter = ("" + guess).toUpperCase();
            onSubmit(currentLetterTextView);
        }
    }

    private int nextPlayer(int lastPlayer)
    {
        int count = 1;

        do{
            if (playersInGame[(lastPlayer+count) % numberOfPlayers])
            {
                return (lastPlayer+count) % numberOfPlayers;
            }
            count++;
        }while(count<numberOfPlayers);

        return -1;
    }

    public void endGame()
    {
        Toast toast = Toast.makeText(getBaseContext(), "Game Over!", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        for (int i = 0; i < numberOfPlayers; i++) {
            if(playersInGame[i])
                playerRanks[i] = dropOutCounter;
        }

        Intent intent = new Intent(this, ResultsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArray("playerNames", playerNames);
        bundle.putStringArray("playerTypes", playerTypes);
        bundle.putIntArray("playerNumbers", playerNumbers);
        bundle.putStringArray("playerScores", playerScores);
        bundle.putIntArray("playerRanks", playerRanks);
        bundle.putInt("numberOfPlayers", numberOfPlayers);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private boolean addLetter(int player)
    {
        switch (playerScores[player].length())
        {
            case 0:
                playerScores[player] += "G";
                break;
            case 1:
                playerScores[player] += "H";
                break;
            case 2:
                playerScores[player] += "O";
                break;
            case 3:
                playerScores[player] += "S";
                break;
            case 4:
                playerScores[player] += "T";
                playerScoreTextView[player].setText(playerScores[player]);
                playersInGame[player] = false;
                playerRanks[player] = dropOutCounter;
                dropOutCounter--;
                if (dropOutCounter == 0)
                    return false;
                break;
        }
        playerScoreTextView[player].setText(playerScores[player]);
        return true;
    }

    private boolean validWord(String word)
    {
        return dbHandler.checkWord(word);
    }

    public void onChallenge(View v)
    {
        //Check that we are not on a new word (previousPlayer = -1)
        if (previousPlayer >= 0)
        {
            if(timer != null) {
                timer.cancel();
            }
            Intent intent = new Intent(this, ChallengeActivity.class);
            int playerBeingChallenged = previousPlayer;

            intent.putExtra("player", playerBeingChallenged);
            intent.putExtra("currentWord", currentWord);
            if (playerTypes[previousPlayer].equals("HUMAN")) {
                startActivityForResult(intent, CHALLENGE_REQUEST);
            }else//AI
            {
                List<String> list = dbHandler.getSuggestions(currentWord);
                boolean gameNotOver;
                String message;
                if(list.size()<1)//todo chance bad guess
                {
                    gameNotOver = addLetter(previousPlayer);
                    message = "Challenge Lost!";
                }else
                {
                    gameNotOver = addLetter(currentPlayer); //add a letter to AI for failed challenge
                    message = list.get(1);
                }
                if (gameNotOver) {
                    roundEndDialog(message);
                }
                else
                {
                    endGame();
                }
            }
        } else {
            Toast toast = Toast.makeText(getBaseContext(), "No Challenges Yet, wait until the word is started!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data )
    {
        if(requestCode== CHALLENGE_REQUEST && resultCode == RESULT_OK)
        {
            boolean gameNotOver;
            if (data.getExtras().getBoolean("isChallengeWon"))
            {
                gameNotOver = addLetter(currentPlayer); //add a letter to the player who challenged
            }else{
                gameNotOver = addLetter(previousPlayer); //add a letter for failed challenge
            }
            if (gameNotOver) {
                roundEndDialog(data.getExtras().getString("challengeResult"));
            }
            else
            {
                endGame();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_screen, menu);
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
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt("playerTurn", playerTurn);
        outState.putString("currentLetter", currentLetter);
        outState.putString("currentWord", currentWord);
        outState.putInt("currentPlayer", currentPlayer);
        outState.putInt("previousPlayer", previousPlayer);
        outState.putInt("dropOutCounter", dropOutCounter);

        outState.putStringArray("playerNames", playerNames);
        outState.putStringArray("playerTypes", playerTypes);
        outState.putInt("numberOfPlayers", numberOfPlayers);
        outState.putBooleanArray("playersInGame", playersInGame);
        outState.putStringArray("playerScores", playerScores);
        outState.putIntArray("playerNumbers", playerNumbers);

        outState.putIntArray("playerRanks", playerRanks);
        outState.putBooleanArray("playersInGame", playersInGame);

        TextView timerTextView = (TextView)findViewById(R.id.textViewTimer);
        time = Long.parseLong(timerTextView.getText().toString());
        outState.putLong("time",  time*1000);
        if(timer != null) {
            timer.cancel();
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return timer;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
