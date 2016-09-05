package com.example.leedongho.weatherfinder;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends Activity {

    @Bind(R.id.cityName) EditText cityName;
    @Bind(R.id.resultTextView) TextView resultTextView;

    public void findWeather(View view){

        try{
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
            DownloadTask getWeather = new DownloadTask();
            getWeather.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&APPID=931ed2a2113552915849cebeb3f0f9a5");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);


    }


    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                String message = "";

                JSONObject jsonObject = new JSONObject(result);

                //extract weather part of JSON Object
                String weatherInfo =  jsonObject.getString("weather");

                Log.i("Weather content", weatherInfo);

                JSONArray weatherArray = new JSONArray(weatherInfo);

                for (int i = 0; i < weatherArray.length(); i++) {

                    JSONObject jsonPart = weatherArray.getJSONObject(i);

                    String main = "";
                    String description = "";

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if (main != "" && description != "") {

                        message += main + ": " + description + "\r\n";

                    }

                    if ( message != "" && description != "") {

                        resultTextView.setText(message);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.i("Content", "DownloadTask.onPostExecute : " + "String :" + result);
        }
    }
}
