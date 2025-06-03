package com.darkcoder.weathercast;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mp;
    private VideoView videoView;
    private EditText cityName;
    private TextView show;
    private Button search;
    private TextToSpeech tts;

    // AsyncTask to fetch weather data
    @SuppressLint("StaticFieldLeak")
    class GetWeather extends AsyncTask<String, Void, String> {
        private final String city;

        public GetWeather(String city) {
            this.city = city; // Store city name to display and announce it later
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(urls[0]).openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                return new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))
                        .lines()
                        .collect(Collectors.joining("\n"));
            } catch (Exception e) {
                return null;
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject main = new JSONObject(result).getJSONObject("main");
                    String weatherData = "Temperature: " + main.getDouble("temp") + "°C\n" +
                            "Feels Like: " + main.getDouble("feels_like") + "°C\n" +
                            "Maximum Temperature: " + main.getDouble("temp_max") + "°C\n" +
                            "Minimum Temperature: " + main.getDouble("temp_min") + "°C\n" +
                            "Pressure: " + main.getInt("pressure") + " hPa\n" +
                            "Humidity: " + main.getInt("humidity") + " %";

                    show.setText(weatherData);

                    // Speak the city name first, then weather data
                    speakWeather("Here is the weather report for " + city + ": " + weatherData);

                } catch (Exception e) {
                    show.setText("Error parsing weather data.");
                    speakWeather("Error parsing weather data.");
                }
            } else {
                show.setText("Error fetching data.");
                speakWeather("Error fetching weather data.");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize media player for background sound
        mp = MediaPlayer.create(this, R.raw.sound);
        mp.setLooping(true);
        mp.start();

        // Initialize VideoView for background video
        videoView = findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video));
        videoView.start();
        videoView.setOnCompletionListener(mp -> videoView.start());

        // Initialize UI components
        cityName = findViewById(R.id.cityName);
        search = findViewById(R.id.search);
        show = findViewById(R.id.weather);

        // Initialize Text-to-Speech (TTS)
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            } else {
                Toast.makeText(this, "Text-to-Speech initialization failed!", Toast.LENGTH_SHORT).show();
            }
        });

        // Search button click listener
        search.setOnClickListener(v -> {
            String city = cityName.getText().toString().trim();
            if (!city.isEmpty()) {
                new GetWeather(city).execute("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=054ebab46df1844cffa1240c2ecb905a&units=metric");
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to speak the weather data
    private void speakWeather(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mp != null) {
            mp.stop();
            mp.release();
        }

        if (videoView != null) {
            videoView.stopPlayback();
        }

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}