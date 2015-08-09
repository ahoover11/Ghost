package com.hooversmithmobileinnovations.ghost;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ChallengeActivity extends Activity {

    Vibrator myVib;
    int playerChallenged, playerNumbers[];
    String currentWord, currentGuess, challengeResult, playerNames[], playerTypes[];
    TextView currentWordTextView, endingTextView;
    Button backspace;
    MyDBHandler dbHandler;
    boolean isChallengeWon;
    Drawable blueGhost, redGhost, greenGhost, orangeGhost, aiBlue, aiRed, aiGreen, aiOrange;
    CountDownTimer timer;
    TextView timerTextView;
    final static long timeLimit = 60000; //30 second time limit for each turn
    Dialog dialogPlayerC;
    long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        backspace = (Button)findViewById(R.id.buttonBackspace);
        currentWordTextView = (TextView)findViewById(R.id.textViewWordStart);
        endingTextView = (TextView)findViewById(R.id.endingTextView);
        time = timeLimit;

        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackspace();
            }
        });

        if(savedInstanceState == null ) //for first time
        {
            currentGuess = "";

            Bundle bundle = getIntent().getExtras();
            if(bundle != null) {
                playerChallenged = bundle.getInt("player");
                currentWord = bundle.getString("currentWord");
                playerNames = bundle.getStringArray("playerNames");
                playerTypes = bundle.getStringArray("playerTypes");
                playerNumbers = bundle.getIntArray("playerNumbers");
            }
        }
        else {
            currentGuess = savedInstanceState.getString("currentGuess");
            playerChallenged = savedInstanceState.getInt("player");
            currentWord = savedInstanceState.getString("currentWord");
            playerNames = savedInstanceState.getStringArray("playerNames");
            playerTypes = savedInstanceState.getStringArray("playerTypes");
            playerNumbers = savedInstanceState.getIntArray("playerNumbers");
            time = savedInstanceState.getLong("time");


        }

        timerTextView = (TextView)findViewById(R.id.textViewTimerChallenge);
        timerTextView.setText(Long.toString(time / 1000));

        endingTextView.setText(currentGuess);
        currentWordTextView.setText(currentWord);

        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        dbHandler = new MyDBHandler(this,null,null,1);

        blueGhost = getResources().getDrawable(R.drawable.blueghost);
        redGhost = getResources().getDrawable(R.drawable.redghost);
        greenGhost = getResources().getDrawable(R.drawable.greenghost);
        orangeGhost = getResources().getDrawable(R.drawable.orangeghost);
        aiBlue = getResources().getDrawable(R.drawable.aiblue);
        aiRed = getResources().getDrawable(R.drawable.aired);
        aiGreen = getResources().getDrawable(R.drawable.aigreen);
        aiOrange = getResources().getDrawable(R.drawable.aiorange);

       // beginChallenge();
    }

    public void beginChallenge(){
        //Dialog that depicts which player's turn it is
        dialogPlayerC = new Dialog(ChallengeActivity.this);
        dialogPlayerC.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPlayerC.setContentView(R.layout.next_turn_popup);

        TextView text = (TextView) dialogPlayerC.findViewById(R.id.popupPlayerTextView);
        text.setText(playerNames[playerChallenged]);
        ImageView image = (ImageView) dialogPlayerC.findViewById(R.id.imageViewGhost);
        switch (playerNumbers[playerChallenged]) {
            case 0:
                if (playerTypes[playerChallenged].equals("HUMAN")) {
                    image.setImageDrawable(blueGhost);
                } else {
                    image.setImageDrawable(aiBlue);
                }
                break;
            case 1:
                if (playerTypes[playerChallenged].equals("HUMAN")) {
                    image.setImageDrawable(redGhost);
                } else {
                    image.setImageDrawable(aiRed);
                }
                break;
            case 2:
                if (playerTypes[playerChallenged].equals("HUMAN")) {
                    image.setImageDrawable(greenGhost);
                } else {
                    image.setImageDrawable(aiGreen);
                }
                break;
            case 3:
                if (playerTypes[playerChallenged].equals("HUMAN")) {
                    image.setImageDrawable(orangeGhost);
                } else {
                    image.setImageDrawable(aiOrange);
                }
                break;
        }


        //If button is clicked, close the custom dialog
        Button dialogButton = (Button) dialogPlayerC.findViewById(R.id.popupButton);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPlayerC.dismiss();

                //Countdown Timer
                timer = new CountDownTimer(time, 1000) {
                    TextView timerTextView = (TextView)findViewById(R.id.textViewTimerChallenge);

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
                        TextView timerTextView = (TextView)findViewById(R.id.textViewTimerChallenge);
                        timerTextView.setText("0");
                        onSubmit(timerTextView);
                    }
                }.start();
            }
        });
        dialogPlayerC.setCancelable(false);
        dialogPlayerC.setCanceledOnTouchOutside(false); //disable back button out
        dialogPlayerC.show();
    }


    public void LetterClicked(View v)
    {
        myVib.vibrate(80); //haptic feedback for key press
        Resources res = getResources();
        currentGuess+= res.getResourceEntryName(v.getId());
        endingTextView.setText(currentGuess);
    }

    public void onBackspace()
    {
        if (currentGuess!= null && currentGuess.length() > 0 ) {
            if(currentGuess.length() == 1)
            {
                currentGuess = "";
            }else{
                currentGuess = currentGuess.substring(0, currentGuess.length()-1);
            }

            endingTextView.setText(currentGuess);
        }

    }

    public void onSubmit(View v)
    {
        String finalGuess = currentWord+currentGuess;

        if (dialogPlayerC!= null && dialogPlayerC.isShowing())
        {
            dialogPlayerC.cancel();
            dialogPlayerC = null;
        }
        if(timer != null) {
            timer.cancel();
            timer = null;
        }

        if (finalGuess.length() < 4)
        {
            challengeResult = "Challenge Lost!";
            isChallengeWon = false;
            finish();
        }else if (dbHandler.checkWord(finalGuess.toLowerCase())) {
            challengeResult = finalGuess;
            isChallengeWon = true;
            finish();
        }else
        {
            challengeResult = "Challenge Lost!";
            isChallengeWon = false;
            finish();
        }
    }

    public void finish()
    {
        Intent data  = new Intent();
        data.putExtra("challengeResult", challengeResult);
        data.putExtra("isChallengeWon", isChallengeWon);
        setResult(RESULT_OK, data);
        super.finish();
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("currentGuess", currentGuess);
        outState.putInt("player", playerChallenged);
        outState.putString("currentWord", currentWord);
        outState.putStringArray("playerNames", playerNames);
        outState.putStringArray("playerTypes", playerTypes);
        outState.putIntArray("playerNumbers", playerNumbers);

        TextView timerTextView = (TextView)findViewById(R.id.textViewTimerChallenge);
        time = Long.parseLong(timerTextView.getText().toString());
        outState.putLong("time", time * 1000);
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onResume()
    {
        beginChallenge();
        super.onResume();
    }
    @Override
    protected void onPause()
    {

        if (dialogPlayerC!= null && dialogPlayerC.isShowing())
        {
            dialogPlayerC.cancel();
            dialogPlayerC = null;
        }
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_challenge, menu);
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
