package com.darkcoder.weathercast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize Text-to-Speech
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
                speak("Welcome To Our App, Feel Free To Use Me");
            } else {
                Log.e("TTS", "Text-to-Speech initialization failed!");
            }
        });

        // Move to MainActivity after 5 seconds
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 4000); // 4 seconds delay
    }

    // Function to speak text
    private void speak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
