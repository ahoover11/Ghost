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

    TextView currentLetterTextView, currentWordTextView, playerScoreTextView[], playerNameTextView[], timerTextView; //TextViews objects
    Drawable blueGhost, redGhost, greenGhost, orangeGhost, aiBlue, aiRed, aiGreen, aiOrange; //Drawable objects for player types
    int currentPlayer, numberOfPlayers, playerTurn, previousPlayer, playerNumbers[], playerRanks[],dropOutCounter; //Ints to store the current player, the total number of players, the player turn, the previous player, player numbers, player ranks, and counter used to keep track of players as they drop out of the gam
    String currentWord, currentLetter, playerScores[], playerNames[], playerTypes[], roundEndMessage; //Strings to store the current word, current letter, player scores, player names, and player types
    boolean playersInGame[], timerOff, roundDialog, challengeReturn; //Boolean array that reflects active players
    final static int MAX_NUMBER_PLAYERS = 4, CHALLENGE_REQUEST = 1; //Int that reflects the maximum possible number of players and Int used to signify a challenge
    double smartGuess = 0.7, dumbGuess = 0.95, challengeThreshold = 0.85, badChallengeThreshold = 0.03; // 70% Chance of a good guess 25% chance of a bad guess and 5% random and 85% chance of challenging when no valid words; 3% of challenging a good word
    final static long timeLimit = 30000;        //30 second time limit for each turn
    long time;
    CountDownTimer timer;                       //Timer for limiting turn length
    Vibrator myVib;                             //Vibrator object for haptic feedback
    MyDBHandler dbHandler;                      //Database object used for dictionary lookup
    Dialog dialogRoundEnd,dialogPlayer;         //Between turn dialogs

    private static final  String TAG = "LOG";//For debugging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        //Initialize some default values
        if(savedInstanceState == null ) //for first time
        {
            playerTurn = 0;         //Hold the turn order starting with the first player listed
            currentPlayer = playerTurn;
            previousPlayer = -1;    //-1 to indicate
            currentLetter = "";
            currentWord = "";       //The string on which the word is built
            timerOff = true;
            time = timeLimit;       //Set the time remaining to default time
            roundDialog = false;
            challengeReturn = false;

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
            roundEndMessage = savedInstanceState.getString("roundEndMessage");
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
            timerOff = savedInstanceState.getBoolean("timerOff");
            roundDialog = savedInstanceState.getBoolean("roundDialog");
            challengeReturn = savedInstanceState.getBoolean("challengeReturn");
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
        timerTextView = (TextView)findViewById(R.id.textViewTimer);



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

        dbHandler = new MyDBHandler(this,null,null,1);      //dbHandler to coordinate with the dictionary
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
    }

    public void LetterClicked(View v)
    {
            myVib.vibrate(80); //haptic feedback for key press
            Resources res = getResources();
            currentLetter = res.getResourceEntryName(v.getId()); //Each button named with corresponding letter
            currentLetterTextView.setText(currentLetter);
    }

    public void onSubmit(View v)
    {
        if (!currentLetter.equals("")) {
            //Append selected letter to current word and update textViews
            currentWord += currentLetter;               //Add the current letter to the word
            currentWordTextView.setText(currentWord);   //Update the text view
            currentLetterTextView.setText("");          //Reset the current letter text view

            //Stop the timer
            if(timer != null) {
                timer.cancel();
                timer = null;
            }
            timerOff = true;
            time = timeLimit;//Reset timer to default value

            //Only check if player created a word if length greater than 3
            if (currentWord.length() > 3) {
                //If a word is completed, start the next round
                if (validWord(currentWord.toLowerCase()))
                {
                    //Update variables to prepare for next round
                    boolean gameNotOver = addLetter(currentPlayer);
                    if (gameNotOver)
                    {
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
        currentLetter = ""; //Reset current letter
    }

    public void roundEndDialog(String endingWord)
    {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        timerOff = true;
        time = timeLimit;//Reset time

        roundDialog = true;              // For displaying dialog on change of state
        roundEndMessage = endingWord;    // Save the message




        //Dialog that depicts which player completed a word
        Activity a = GameScreenActivity.this; //Wait until challenge activity is closed
        while (a.getParent() != null) {
            a = a.getParent();
        }
        dialogRoundEnd = new Dialog(GameScreenActivity.this);
        dialogRoundEnd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogRoundEnd.setContentView(R.layout.round_over_popup);

        TextView text = (TextView) dialogRoundEnd.findViewById(R.id.textViewFinalWord);
        text.setText(endingWord);
        Button dialogEndButton = (Button) dialogRoundEnd.findViewById(R.id.popupButton);
        dialogEndButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Reset default values
                previousPlayer = -1;
                currentWord = "";
                currentWordTextView.setText(currentWord);
                currentPlayer = nextPlayer(playerTurn);
                playerTurn++;

                dialogRoundEnd.cancel();
                dialogRoundEnd = null;
                roundDialog = false;

                playerTurn(currentPlayer);
            }
        });
        dialogRoundEnd.setCancelable(false);                //disable back button out
        dialogRoundEnd.setCanceledOnTouchOutside(false);    //disable outside touch cancel
        dialogRoundEnd.show();
    }

    public void playerTurn(int player)
    {
        timerTextView.setTextColor(Color.WHITE);
        timerTextView.setText(Long.toString(time/1000)); //convert to seconds and set timer

        //Set all player names to black if it is not their turn
        for (int i = 0; i < numberOfPlayers; i++)
        {
            playerNameTextView[i].setTextColor(Color.BLACK);
        }

        //Set the active player's name to blue
        playerNameTextView[player].setTextColor(Color.BLUE);

        //Dialog that depicts which player's turn it is
        dialogPlayer = new Dialog(GameScreenActivity.this);
        dialogPlayer.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPlayer.setContentView(R.layout.next_turn_popup);

        TextView text = (TextView) dialogPlayer.findViewById(R.id.popupPlayerTextView);
        text.setText(playerNames[player]);
        ImageView image = (ImageView) dialogPlayer.findViewById(R.id.imageViewGhost);
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

        final int aiPlayer = player; //For use inside of onClickListener
        //If button is clicked, close the custom dialog
        Button dialogButton = (Button) dialogPlayer.findViewById(R.id.popupButton);
        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerTypes[aiPlayer].equals("AI"))
                {
                    aiTurn();       // TODO: 8/8/2015 Add Thinking dialog or change picture when AI is playing?
                } else if (playerTypes[aiPlayer].equals("HUMAN")){
                    dialogPlayer.cancel();
                    dialogPlayer = null;
                    //Countdown Timer
                    timerOff = false;
                    timer = new CountDownTimer(time, 1000) {
                        TextView timerTextView = (TextView)findViewById(R.id.textViewTimer);
                        @Override
                        public void onTick(long millisUntilFinished) {
                            timerTextView.setText(Long.toString(millisUntilFinished / 1000));
                            if (millisUntilFinished < timeLimit*0.25)//Set text color to red if 1/4 of time left
                            {
                                timerTextView.setTextColor(Color.RED);
                            }
                        }
                        @Override
                        public void onFinish() {
                            timeUp();
                            timerTextView.setText("0");
                            timerTextView.setTextColor(Color.WHITE);
                            if(timer != null) {
                                timer.cancel();
                                timer = null;
                            }
                            timerOff = true;
                            time = timeLimit;
                        }
                    }.start();
                }
            }
        });
        dialogPlayer.setCancelable(false);              //disable back button out
        dialogPlayer.setCanceledOnTouchOutside(false);
        dialogPlayer.show();
    }

    public void timeUp()
    {
        if(!timerOff) {
            boolean gameNotOver = addLetter(currentPlayer);
            if (gameNotOver) {
                roundEndDialog("Time's Up!");
            } else {
                endGame();//End the game and go to results screen
            }
        }
    }

    public void aiTurn()
    {
        Random r = new Random();                    //For random selections
        char guess= (char) (r.nextInt(26) + 'A');   //Default random letter
        boolean challenge = false;                  //Determines whether the AI will challenge

        if (currentWord.length() == 1) //If the word's length is zero, the default (random) letter is kept
        {
            List<String> list = dbHandler.getSuggestions(currentWord);  //Return all words starting with the current string
            guess = list.get(r.nextInt(list.size())).toCharArray()[1];  //Select second letter from a random word
        }else if (currentWord.length()>1){
            List<String> list = dbHandler.getSuggestions(currentWord);//For suggestions
            //Challenge some percent of time (challengeThreshold) if no valid words can be made, or randomly challenge (badChallengeThreshold)
            if ((list.isEmpty()&& r.nextDouble()< challengeThreshold)|| r.nextDouble()<badChallengeThreshold) {
                challenge = true;
                //Remove the AI player dialog before the challenge which will initiate the round end dialog
                if(dialogPlayer!=null) {
                    dialogPlayer.cancel();
                    dialogPlayer = null;
                }
                onChallenge(currentLetterTextView);
            } else if(!list.isEmpty()) {                //If there are valid words, select one to 'think' of
                List<String> smartList = new ArrayList<String>(); //For holding potentially good guesses
                List<String> stupidList = new ArrayList<String>();//For holding potentially poor guesses
                for (int i = 0; i < list.size(); i++) {
                    int aiPosition = currentWord.length() % (dropOutCounter + 1);       //Determine whether a word is potentially good or bad based on length
                    if (list.get(i).length() % (dropOutCounter + 1) - 1 == aiPosition||list.get(i).length()==currentWord.length() ) {
                        stupidList.add(list.get(i));
                    } else {
                        smartList.add(list.get(i));
                    }
                }

                Collections.shuffle(smartList);//Shuffle the two lists
                Collections.shuffle(stupidList);

                double randomProb = r.nextDouble(); //Used to determine which list to use (or the default letter)
                if (randomProb < smartGuess&&!smartList.isEmpty()) {
                        int maxWordsChecked =  5;//smartList.get(0).length();      //The maximum number of words that may be considered Larger number gives better guess, but slows down app (each word may have multiple calls to database)
                        if (maxWordsChecked > smartList.size()) {
                            maxWordsChecked = smartList.size();  //Most that can be searched
                        }
                        int index = -1;     //Initialize index for searching will start with first word
                        boolean wordChosen = false;
                        do {
                            index++;
                            boolean noSubWords = true;
                            if ( currentWord.length() != smartList.get(index).length()) { //Make sure original word is not returned
                                //Try to find a word form the smart list that has now shorter words contained in it
                                for (int i = currentWord.length(); i < smartList.get(index).length() - 1; i++) {
                                    if (dbHandler.checkWord(smartList.get(index).substring(0, i))) {
                                        noSubWords = false;
                                        break;
                                    }
                                }
                                if (noSubWords) {
                                    wordChosen = true;
                                }
                            }
                        } while (!wordChosen && index < maxWordsChecked - 1);
                        guess = smartList.get(index).toCharArray()[currentWord.length()];
                }
                else if (randomProb < dumbGuess && !stupidList.isEmpty() )
                {
                    if (stupidList.get(0).length() > currentWord.length())
                        guess = stupidList.get(0).toCharArray()[currentWord.length()]; //Select a word from the 'stupidList' (which has already been shuffled)
                }
            }
        }
        if (!challenge)     //When there has not been a challenge, add the selected letter to the current word
        {
            //Remove dialog before next turn
            if(dialogPlayer!=null) {
                dialogPlayer.cancel();
                dialogPlayer = null;
            }
            currentLetter = ("" + guess).toUpperCase();
            onSubmit(currentLetterTextView);
        }
    }

    public int nextPlayer(int lastPlayer)
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
        if (dialogRoundEnd!= null &&dialogRoundEnd.isShowing())
        {
            dialogRoundEnd.cancel();
            dialogRoundEnd= null;
        }
        if (dialogPlayer!= null && dialogPlayer.isShowing())
        {
            dialogPlayer.cancel();
            dialogPlayer = null;
        }
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

    public boolean addLetter(int player)
    {
        switch (playerScores[player].length()) //Add the next letter to the loser's score
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
                playerScores[player] += "T"; // TODO: 8/8/2015 Add player lost dialog?
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

    public boolean validWord(String word)
    {
        return dbHandler.checkWord(word);
    }

    public void onChallenge(View v)
    {
            //Check that we are not on a new word (previousPlayer = -1)
            if (previousPlayer >= 0) {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                    timerTextView.setTextColor(Color.WHITE);
                }
                timerOff = true;

                int playerBeingChallenged = previousPlayer;

                if (playerTypes[previousPlayer].equals("HUMAN")) {
                    challengeReturn = true;
                    Intent intent = new Intent(this, ChallengeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("player", playerBeingChallenged);
                    bundle.putString("currentWord", currentWord);
                    bundle.putStringArray("playerNames", playerNames);
                    bundle.putIntArray("playerNumbers", playerNumbers);
                    bundle.putStringArray("playerTypes", playerTypes);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, CHALLENGE_REQUEST);
                } else//AI
                {
                    List<String> list = dbHandler.getSuggestions(currentWord);
                    boolean gameNotOver;
                    String message;
                    if (list == null || list.isEmpty())
                    {
                        gameNotOver = addLetter(previousPlayer);
                        message = "Challenge Lost!";
                    } else {
                        gameNotOver = addLetter(currentPlayer); //add a letter to AI for failed challenge
                        message = list.get(1);
                    }
                    if (gameNotOver) {
                        roundEndDialog(message);
                    } else {
                        endGame();
                    }
                }
            } else {
                Toast toast = Toast.makeText(getBaseContext(), "No challenges yet, enter a letter!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data )
    {

        if(requestCode == CHALLENGE_REQUEST && resultCode == RESULT_OK)
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
        outState.putString("roundEndMessage",roundEndMessage);
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
        outState.putBoolean("roundDialog", roundDialog);
        outState.putBoolean("challengeReturn", challengeReturn);

        TextView timerTextView = (TextView)findViewById(R.id.textViewTimer);
        time = Long.parseLong(timerTextView.getText().toString())*1000;
        outState.putBoolean("timerOff", timerOff);
        outState.putLong("time", time);
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
         protected void onResume()
    {
        if (!roundDialog) {
            playerTurn(currentPlayer);
        }else if(!challengeReturn)
        {
            roundEndDialog(roundEndMessage);
        }
        challengeReturn = false;//For return from challenge to reactivate message
        super.onResume();
    }

    @Override
    protected void onPause()
    {
      if (dialogRoundEnd!= null &&dialogRoundEnd.isShowing())
        {
            dialogRoundEnd.cancel();
            dialogRoundEnd= null;
        }
        if (dialogPlayer!= null && dialogPlayer.isShowing())
        {
            dialogPlayer.cancel();
            dialogPlayer = null;
        }
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onPause();
    }
}