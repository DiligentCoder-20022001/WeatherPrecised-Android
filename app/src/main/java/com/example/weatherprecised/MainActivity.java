package com.example.weatherprecised;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    EditText location;
    TextView Result;
    Button Go;

    public class DownloadTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1)
                {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
                return null;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //2nd step -> to process the JSON data
            try
            {
                String message ="";
                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray arr = new JSONArray(weatherInfo);
                JSONObject jsonPart = arr.getJSONObject(0);
                String imageURL = "";

                String main ="";
                String desc = "";

                main = jsonPart.getString("main");
                desc = jsonPart.getString("description");

                if(main != "" && desc != "")
                {
                    message += "Description : " + main + "\r\n";
                }

                JSONObject x = jsonObject.getJSONObject("main");
                double temp = x.getDouble("temp");
                Log.i("Temperature", String.valueOf(temp));
                message += "Temperature : " + String.valueOf(temp) + "degress celcius\r\n";
                double feelsLike = x.getDouble("feels_like");
                message += "Feels like : " + String.valueOf(feelsLike) + "degrees celcius\r\n";
                double humidity = x.getDouble("humidity");
                message += "Humidity :" + String.valueOf(humidity) + "%\r\n";
                Result.setText(message);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }
    }

    public void click(View view)
    {
        String place = String.valueOf(location.getText());
        //url : http://api.openweathermap.org/data/2.5/weather?q=Chennai&appid=a39b87fc707b5b7ff04d4f7ac35a7ff4
        DownloadTask task = new DownloadTask();
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + place + "&appid=a39b87fc707b5b7ff04d4f7ac35a7ff4&units=metric";
        task.execute(url);
        //to remove the keyboard as soon as button is clicked
        InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(location.getWindowToken(),0);
    }
//the app wont crash on wrong input due to all the exceptions we have taken
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        location = (EditText)findViewById(R.id.location);
        Result = (TextView)findViewById(R.id.Result);
        Go = (Button)findViewById(R.id.submit);
    }
}