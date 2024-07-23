package com.darkcoder.weathercast;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private TextView cityName, show;
    private Button search;
    private String url;

    @SuppressLint("StaticFieldLeak")
    class getWeather extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                return new BufferedReader(new InputStreamReader(urlConnection.getInputStream())).lines().collect(Collectors.joining("\n"));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject Object = new JSONObject(result);
                String weatherInfo = Object.getString("main").replaceAll("[{}]", "").replaceAll(",", "\n").replaceAll(":", " : ")
                        .replaceAll("temp", "Temperature").replaceAll("feels_like", "Feels Like").replaceAll("temp_max", "Temperature Max.")
                        .replaceAll("temp_min", "Temperature Min.").replaceAll("pressure", "Pressure").replaceAll("humidity", "Humidity")
                        .replaceAll("sea_level", "Sea Level").replaceAll("grnd_level", "Ground Level");
                show.setText(weatherInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = findViewById(R.id.cityName);
        search = findViewById(R.id.search);
        show = findViewById(R.id.weather);
        search.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Searching ", Toast.LENGTH_SHORT).show();
            String city = cityName.getText().toString();
            try {
                url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=054ebab46df1844cffa1240c2ecb905a";
                String temp = new getWeather().execute(url).get();
                if (temp == null) show.setText("Data Not Found");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}