package com.example.weatherapp_ak;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

public class SearchResults extends AppCompatActivity {

    JSONObject weatherJSON;
    String fullCity;
    String justCity;
    RelativeLayout spinnerLayout;
    HashSet<String> favCities = new HashSet<>();
    FloatingActionButton favButton;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        spinnerLayout = findViewById(R.id.searchProgressBarLayout);

        // Set spinner
        spinnerLayout.setVisibility(View.VISIBLE);

        // Get the shared preference of the current app
        sharedPref = this.getBaseContext().getSharedPreferences(
                getString(R.string.shared_preference_file), this.getBaseContext().MODE_PRIVATE);
        editor = sharedPref.edit();

        Log.d("Created", "SearchResults");
        displaySearchResults();
    }

    private void displaySearchResults() {
        Intent intent = getIntent();
        String query = intent.getStringExtra("query");
        setSearchResultContents(query);

        Log.d("query", query);
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//
//            setSearchResultContents(query);
//        };
    }

    private void setSearchResultContents(String city) {
        // Set city from search query
        TextView cityView = findViewById(R.id.searchCity);
        cityView.setText(city);
        fullCity = city;

        // Set fav button image
        favButton = this.findViewById(R.id.favButton);
        if (sharedPref.contains(fullCity)) {
            favButton.setImageResource(R.drawable.map_marker_minus);
        }

        // Extract just city name for google image search
        justCity = city.split(",")[0];
        Log.d("justCity in search", justCity);

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
//        String getWeatherURL = "http://10.0.2.2:5000/api/getWeather?street=" + city + "&city=&state=";

        String getWeatherURL = "http://kakati-nodejs.us-east-2.elasticbeanstalk.com/api/getWeather?street=" + city + "&city=&state=";
        // Call Node server REST API for /api/getWeatherCurrentLoc
        JsonObjectRequest weatherDataRequest = new JsonObjectRequest(Request.Method.GET, getWeatherURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Set values to be used in UI
                        setHomePageData(response);

                        // Disable spinner
                        spinnerLayout.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error with Node API", error.getMessage());
            }
        });

        requestQueue.add(weatherDataRequest);

    }

    private void setHomePageData(JSONObject response) {
        weatherJSON = response;
        try {
            JSONObject current = (JSONObject) response.get("currently");

            // Set icon image
            ImageView icon= findViewById(R.id.searchIcon);
            setImage(icon, current.getString("icon"));

            // Set Temperature
            TextView temperature = findViewById(R.id.searchTemperature);
            int temp = Math.round(Float.parseFloat(current.getString("temperature")));
            temperature.setText(temp + "°F");

            // Set Summary
            TextView summary = findViewById(R.id.searchSummary);
            summary.setText(current.getString("summary"));

            // Set Humidity
            int humid = Math.round(Float.parseFloat(current.getString("humidity")) * 100);
            TextView humidity = findViewById(R.id.searchHumidVal);
            humidity.setText(humid + "%");

            // Set WindSpeed
            TextView windSpeed = findViewById(R.id.searchWindSpeedVal);
            windSpeed.setText(current.getString("windSpeed") + " mph");

            // Set Visibility
            TextView visibility = findViewById(R.id.searchVisibilityVal);
            visibility.setText(current.getString("visibility") + " km");

            // Set Pressure
            TextView pressure = findViewById(R.id.searchPressureVal);
            pressure.setText(current.getString("pressure") + " mb");

            // Set daily table
            TableLayout dailyTable = findViewById(R.id.searchDaily_table);

            JSONObject daily = (JSONObject) response.get("daily");
            JSONArray data = daily.getJSONArray("data");

            int jsonCounter = 0;
            for (int i=0; i<dailyTable.getChildCount(); i++) {
                if (i % 2 != 0) {
                    continue;
                }
                TableRow row = (TableRow) dailyTable.getChildAt(i);

                // Date format
                String pattern = "MM-dd-yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                Date date = new Date(Long.parseLong(data.getJSONObject(jsonCounter).getString("time")) * 1000);
                String dateString = simpleDateFormat.format(date);

                Log.d("dateString : ", dateString);
                Log.d("row.getChildCount() : ", String.valueOf(row.getChildCount()));

                // Prepare temperature
                int tempLow = Math.round(Float.parseFloat(((JSONObject)data.getJSONObject(jsonCounter)).getString("temperatureLow")));
                int tempHi= Math.round(Float.parseFloat(((JSONObject)data.getJSONObject(jsonCounter)).getString("temperatureHigh")));

                // Set date
                TextView dateView = (TextView) row.getChildAt(0);
                dateView.setText(dateString);

                // Set icon image
                ImageView iconView = (ImageView) row.getChildAt(1);
                setImage(iconView, ((JSONObject)data.getJSONObject(jsonCounter)).getString("icon"));

                // Set Low temperature
                TextView tempLowView = (TextView) row.getChildAt(2);
                tempLowView.setText(String.valueOf(tempLow));

                // Set High temperature
                TextView tempHighView = (TextView) row.getChildAt(3);
                tempHighView.setText(String.valueOf(tempHi));

                jsonCounter++;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setImage(ImageView imageObj, String value) {
        if (value.equals("clear-day")) {
            imageObj.setImageResource(R.drawable.weather_sunny);
        } else if (value.equals("clear-night")) {
            imageObj.setImageResource(R.drawable.weather_night);
        } else if (value.equals("rain")) {
            imageObj.setImageResource(R.drawable.weather_rainy);
        } else if (value.equals("sleet")) {
            imageObj.setImageResource(R.drawable.weather_snowy_rainy);
        } else if (value.equals("snow")) {
            imageObj.setImageResource(R.drawable.weather_snowy);
        } else if (value.equals("wind")) {
            imageObj.setImageResource(R.drawable.weather_windy_variant);
        } else if (value.equals("fog")) {
            imageObj.setImageResource(R.drawable.weather_fog);
        } else if (value.equals("cloudy")) {
            imageObj.setImageResource(R.drawable.weather_cloudy);
        } else if (value.equals("partly-cloudy-night")) {
            imageObj.setImageResource(R.drawable.weather_night_partly_cloudy);
        } else if (value.equals("partly-cloudy-day")) {
            imageObj.setImageResource(R.drawable.weather_partly_cloudy);
        }
    }

    // On click handler for the card to go to the 2nd page
    public void showSearchSummary(View view) {
        Log.d("Card has been clicked", view.toString());
        Intent myIntent = new Intent(getBaseContext(),   DetailedWeather.class);
        myIntent.putExtra("city", fullCity);
        myIntent.putExtra("weatherJSON", weatherJSON.toString());
        myIntent.putExtra("justCity", justCity);
        startActivity(myIntent);
    }

    // On click handler for adding to/removing from favorites
    public void handleFavorites(View view) {
        if (sharedPref.contains(fullCity)) {
            // Remove from favorites
            Toast.makeText(getBaseContext(), fullCity+" was removed from Favorites",
                    Toast.LENGTH_LONG).show();
            favButton.setImageResource(R.drawable.map_marker_plus);
            favCities.remove(fullCity);

            // Remove city from shared preference
            editor.remove(fullCity);

            editor.commit();
            Log.d("Inside search delete" , "here");


        } else {
            // Add to favorites
            Toast.makeText(getBaseContext(), fullCity+" was added to Favorites",
                    Toast.LENGTH_LONG).show();
            favButton.setImageResource(R.drawable.map_marker_minus);
            favCities.add(fullCity);

            // Add city and json data to shared preference
            editor.putString(fullCity, weatherJSON.toString());

            editor.commit();
        }
    }
}
