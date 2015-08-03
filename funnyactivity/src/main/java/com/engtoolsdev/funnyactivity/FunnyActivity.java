package com.engtoolsdev.funnyactivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Random;

public class FunnyActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    public static final String TAG_ERROR = "error";
    public static final String EXTRA_JOKE = "extra_joke";
    public static final String EXTRA_TITLE = "extra_title";

    private String joke;

    private TextToSpeech tts;

    private TextView jokeTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funny);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        jokeTextView = (TextView) findViewById(R.id.joke_textview);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if(extras != null){
            joke = extras.getString(EXTRA_JOKE);

            if(extras.containsKey(EXTRA_TITLE)){
                getSupportActionBar().setTitle(extras.getString(EXTRA_TITLE));
            }else{
                getSupportActionBar().setTitle(R.string.funny_activity_title);
            }
        }

        if(joke == null){
            Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //Inits TextToSpeech
        tts = new TextToSpeech(this, this);
    }


    @Override
    protected void onDestroy() {
        if(tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Sets joke into {@link TextView} and {@link TextToSpeech}
     * @param joke String returned from backend
     */
    public void tellJoke(@NonNull String joke){
        tts.setPitch(Math.max(new Random().nextFloat()*1.1f, 0.5f));
        tts.setSpeechRate(Math.max(new Random().nextFloat(), 0.5f));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(joke, TextToSpeech.QUEUE_ADD, null, null);
        }else{
            tts.speak(joke, TextToSpeech.QUEUE_ADD, null);
        }

        jokeTextView.setText(joke);
        jokeTextView.setContentDescription(joke);

    }


    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            int result=tts.setLanguage(Locale.US);
            if(result==TextToSpeech.LANG_MISSING_DATA ||
                    result==TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e(TAG_ERROR, getString(R.string.tts_languange_error));
            }
        }else {
            Log.e(TAG_ERROR, getString(R.string.tts_init_error));
        }

        tellJoke(joke);
    }
}
