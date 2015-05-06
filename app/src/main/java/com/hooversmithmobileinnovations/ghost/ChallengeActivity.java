package com.hooversmithmobileinnovations.ghost;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class ChallengeActivity extends Activity {

    boolean challengeWon;
    Vibrator myVib;
    int playerChallenged;
    String currentWord, currentGuess;
    TextView currentWordTextView, endingTextView;
    Button backspace;
    MyDBHandler dbHandler;
    boolean isChallengeWon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        backspace = (Button) findViewById(R.id.buttonBackspace);
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {onBackspace();}
    });

        currentWordTextView = (TextView) findViewById(R.id.textViewWordStart);
        endingTextView =(TextView) findViewById(R.id.endingTextView);
        currentGuess = "";
        endingTextView.setText(currentGuess);
        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        Bundle extras = getIntent().getExtras();
        if (extras ==null)
        {
            return;
        }

        playerChallenged = extras.getInt("player");
        currentWord = extras.getString("currentWord");

        currentWordTextView.setText(currentWord);

        dbHandler = new MyDBHandler(this,null,null,1);

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

    if (finalGuess.length() < 4)
    {
        Toast toast = Toast.makeText(getBaseContext(), "You lose the challenge,the word is too short!", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        isChallengeWon = false;
        finish();
    }else if (dbHandler.checkWord(finalGuess.toLowerCase())) {
        Toast toast = Toast.makeText(getBaseContext(), "You win the Challenge!", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        isChallengeWon = true;
        finish();
    }else
    {
        Toast toast = Toast.makeText(getBaseContext(), "Not a valid word.", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        isChallengeWon = false;
        finish();
    }
}
    public void finish()
    {
        Intent data  = new Intent();
        data.putExtra("isChallengeWon", isChallengeWon);
        setResult(RESULT_OK, data);
        super.finish();
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
