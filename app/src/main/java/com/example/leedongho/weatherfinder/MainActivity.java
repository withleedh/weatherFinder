package com.example.leedongho.weatherfinder;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity{

    @Bind(R.id.cityName) EditText cityName;
    @Bind(R.id.weatherListView) ListView listview;
    ListViewAdapter listViewAdapter;

    public void findWeather(View view){

        try{
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
            DownloadTask getWeather = new DownloadTask();
            getWeather.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&APPID=931ed2a2113552915849cebeb3f0f9a5");

        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        listViewAdapter = new ListViewAdapter();
        listview.setAdapter(listViewAdapter);




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

            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                String message = "";

                JSONObject weatherObject = new JSONObject(result);

                //extract weather part of JSON Object
                String weatherInfo =  weatherObject.getString("weather");
                String selectedCityName = weatherObject.getString("name");



                Log.i("aa",weatherObject.toString());
                Log.i("Weather content", weatherInfo);

                JSONArray weatherArray = new JSONArray(weatherInfo);

                for (int i = 0; i < weatherArray.length(); i++) {

                    JSONObject jsonPart = weatherArray.getJSONObject(i);

                    String main = "";
                    String description = "";
                    String icon="";


                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");
                    icon = "w"+jsonPart.getString("icon");

                    // TODO Add more item

//                    List Item should contain "Day of week", "Month-Day", "Weather", "Temperature (current, min , max)"
//                    ex) Wed, Jun 4 - Cloudy - 35 - 27 / 38

                    if (main != "" && description != "") {

                        String packName = getPackageName();
                        int imageResId = getResources().getIdentifier(icon, "drawable", packName);
                        // Test Case
                        //message += main + ": " + description + "\r\n";
                        WeatherData  weatherData= new WeatherData();

                        weatherData.setIcon(getDrawable(imageResId));
                        weatherData.setCityName(selectedCityName);
                        weatherData.setDescription(description);

                        listViewAdapter.mWeatherDataList.add(weatherData);
                        listViewAdapter.notifyDataSetChanged();

                    }
                    else{
                        // Do nothing
                    }

                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }


            Log.i("Content", "DownloadTask.onPostExecute : " + "String :" + result);
        }
    }

    private class ViewHolder{
        public ImageView iconImageView;
        public TextView cityName;
        public TextView description;

    }

    public class ListViewAdapter extends BaseAdapter {

        private ArrayList<WeatherData> mWeatherDataList = new ArrayList<WeatherData>();

        public ListViewAdapter(){
        }

        @Override
        public int getCount() {
            return mWeatherDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mWeatherDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null){
                holder = new ViewHolder();

                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                view = inflater.inflate(R.layout.weather_listview_item,null);

                holder.iconImageView = (ImageView) view.findViewById(R.id.listview_image);
                holder.cityName = (TextView) view.findViewById( R.id.listview_city_name);
                holder.description = (TextView) view.findViewById( R.id.listview_city_description);


                WeatherData weatherData = mWeatherDataList.get(position);
                holder.iconImageView.setImageDrawable(weatherData.getIcon());
                holder.cityName.setText(weatherData.getCityName());
                holder.description.setText(weatherData.getDescription());

                view.setTag(holder);
            }else{
                holder = (ViewHolder)view.getTag();
            }

            WeatherData weatherData = mWeatherDataList.get(position);

            return view;
        }
    }
}
