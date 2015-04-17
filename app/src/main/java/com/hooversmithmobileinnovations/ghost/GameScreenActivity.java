package com.hooversmithmobileinnovations.ghost;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;
import android.os.Vibrator;
import android.view.ViewGroup.LayoutParams;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Button;
import android.view.Window;

import java.util.List;


public class GameScreenActivity extends Activity {

    TextView currentLetterTextView, currentWordTextView, playerScoreTextView[], playerNameTextView[];
    Drawable blueGhost, redGhost, greenGhost, orangeGhost;
    int currentPlayer, maxPlayerNumber, numberOfPlayers, playerTurn, lastPlayer;
    String currentWord, currentLetter, playerScore[], playerNames[];
    boolean playersInGame[];
    Vibrator myVib;
    MyDBHandler dbHandler;
    static final int Challenge_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        blueGhost = getResources().getDrawable(R.drawable.blueghost);
        redGhost = getResources().getDrawable(R.drawable.redghost);
        greenGhost = getResources().getDrawable(R.drawable.greenghost);
        orangeGhost = getResources().getDrawable(R.drawable.orangeghost);
        dbHandler = new MyDBHandler(this,null,null,1);
        //List<String> list = dbHandler.getSuggestions("zip");//For suggestions
        maxPlayerNumber = 4;
        playerScore = new String[maxPlayerNumber];
        //TODO have this from input
        numberOfPlayers = 2;

        //TODO
        playerNames = new String[maxPlayerNumber];

        ///////////////////////////////
        playersInGame = new boolean[maxPlayerNumber];
        for (int i = 0; i <maxPlayerNumber;i++)
        {
            playerScore[i] = "";
        }
        playerTurn = 0;
        currentLetter = "";
        currentWord = "";

        playersInGame[0] =true;
        playersInGame[1] =true;
        playersInGame[2] =true;
        playersInGame[3] =true;

        currentPlayer = 0; //first player

        playerNames[0] = "Player 1";
        playerNames[1] = "Player 2";
        playerNames[2] = "Player 3";
        playerNames[3] = "Player 4";

        lastPlayer = -1;

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
            playerNameTextView[i].setText(playerNames[i]);
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

            if (currentWord.length() > 3) {
                if (validWord(currentWord.toLowerCase())) //if a word is completed start the next round
                {
                    addLetter(currentPlayer);
                    currentWord = "";
                    lastPlayer = -1;
                    currentWordTextView.setText(currentWord);
                    currentPlayer = nextPlayer(playerTurn);
                    playerTurn++;
                    playerTurn(currentPlayer);
                    Toast.makeText(getBaseContext(), "Word Completed Round Finished", Toast.LENGTH_SHORT).show();
                } else {
                    lastPlayer = currentPlayer;
                    currentPlayer = nextPlayer(currentPlayer);
                    playerTurn(currentPlayer);
                }
            } else {
                lastPlayer = currentPlayer;
                currentPlayer = nextPlayer(currentPlayer);
                playerTurn(currentPlayer);
            }
        }
        currentLetter = "";//Reset current Letter;
    }
    private void playerTurn(int player)
    {

        for (int i = 0; i < numberOfPlayers; i++)
        {
            playerNameTextView[i].setTextColor(Color.BLACK);
        }
        playerNameTextView[player].setTextColor(Color.BLUE);

        // Dialog
        final Dialog dialog = new Dialog(GameScreenActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.next_turn_popup);

        TextView text = (TextView) dialog.findViewById(R.id.popupPlayerTextView);
       text.setText(playerNames[player]);
        ImageView image = (ImageView) dialog.findViewById(R.id.imageViewGhost);
        switch (player){
            case 0 :
                image.setImageResource(R.drawable.blueghost);
                break;
            case 1:
                image.setImageResource(R.drawable.redghost);
                break;
            case 2:
                image.setImageResource(R.drawable.greenghost);
                break;
            case 3:
                image.setImageResource(R.drawable.orangeghost);
                break;
        }

        Button dialogButton = (Button) dialog.findViewById(R.id.popupButton);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false); //disable back button out
        dialog.show();

    }
    private int nextPlayer(int previousPlayer)
    {
        int count = 1;
        do{
            if (playersInGame[(previousPlayer+count)%numberOfPlayers])
            {
                return (previousPlayer+count)%numberOfPlayers;
            }
            count++;
        }while(count<numberOfPlayers);
        //TODO ENDGAME ACTIVITY ACTIVATE
        Intent intent = new Intent(this, ResultsActivity.class);
        //intent.putExtra("Winner")
        startActivity(intent);


        return 0;
    }
    private void addLetter(int player)
    {
        switch (playerScore[player].length())
        {
            case 0:
                playerScore[player]+="G";
                break;
            case 1:
                playerScore[player]+="H";
                break;
            case 2:
                playerScore[player]+="O";
                break;
            case 3:
                playerScore[player]+="S";
                break;
            case 4:
                playerScore[player]+="T";
                playersInGame[player] = false;
                break;
        }
        playerScoreTextView[player].setText(playerScore[player]);
    }

    private boolean validWord(String word)
    {
        return dbHandler.checkWord(word);
    }

    public void onChallenge(View v)
    {
        if (lastPlayer>=0)//check that we are not on a new word
        {
        Intent intent = new Intent(this, ChallengeActivity.class);
        int playerBeingChallenged = lastPlayer;

            intent.putExtra("player", playerBeingChallenged);
            intent.putExtra("currentWord", currentWord);

            startActivityForResult(intent, Challenge_REQUEST);
        }
        else
        {
            Toast.makeText(getBaseContext(), "No Challenges Yet, wait until the word is started!", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data )
    {
        if((requestCode==Challenge_REQUEST)&& resultCode == RESULT_OK)
        {
            if (data.getExtras().getBoolean("isChallengeWon"))
            {
                addLetter(currentPlayer); //add a letter to the player who challenged

            }else
            {
                addLetter(lastPlayer); //add a letter for failed challenge
            }
            currentWord = "";
            lastPlayer = -1;
            currentWordTextView.setText(currentWord);
            currentPlayer = nextPlayer(playerTurn);
            playerTurn++;
            playerTurn(currentPlayer);
        }


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
