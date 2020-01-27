package com.example.weatherapp_ak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static JSONObject weatherJSON;
    static String fullCity;
    String justCity;
    String[] cityArray;
    ArrayAdapter<String> newsAdapter;
    SearchView.SearchAutoComplete searchAutoComplete;
    RelativeLayout spinnerLayout;
    static FavouritesAdapter favouritesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_page);

        // For favorite tab view
        favouritesAdapter = new FavouritesAdapter(this, getSupportFragmentManager());
        ViewPager favViewPager = findViewById(R.id.fav_view_pager);
        favViewPager.setAdapter(favouritesAdapter);
        TabLayout favTabs = findViewById(R.id.fav_tabs);
        favTabs.setupWithViewPager(favViewPager);


//        spinnerLayout = findViewById(R.id.progressBarLayout);
//
//        // Set spinner
//        spinnerLayout.setVisibility(View.VISIBLE);

//        getCurrentLocationWeatherData();

    }

    private void getCurrentLocationWeatherData() {
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        String ipapiURL = "http://ip-api.com/json";

        // Request a JSON response from ip-api.
        JsonObjectRequest currentLocRequest = new JsonObjectRequest(Request.Method.GET, ipapiURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String latitude;
                        String longitude;
                        String fullCity;
                        // Set values to be used in UI
                        try {
                            latitude = response.getString("lat");
                            longitude = response.getString("lon");
                            TextView city = findViewById(R.id.city);
                            fullCity = response.getString("city") + ", " +
                                    response.getString("region") + ", " +
                                    response.getString("countryCode");
                            city.setText(fullCity);

                            MainActivity.fullCity = fullCity;
                            justCity = response.getString("city");
                            getWeatherData(latitude, longitude, requestQueue, fullCity);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error with ip-api", error.getMessage());
            }
        });

        // Add the request to the RequestQueue.
        requestQueue.add(currentLocRequest);
    }

    private void getWeatherData(String lat, String lon, RequestQueue requestQueue, final String fullCity) {
        String getWeatherURL = "http://kakati-nodejs.us-east-2.elasticbeanstalk.com/api/getWeatherCurrentLoc?lat=" + lat + "&lon=" + lon;

        // Call Node server REST API for /api/getWeatherCurrentLoc
        JsonObjectRequest weatherDataRequest = new JsonObjectRequest(Request.Method.GET, getWeatherURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Set values to be used in UI
                        setHomePageData(response, fullCity);

//                        // Disable spinner
//                        spinnerLayout.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error with Node API", error.getMessage());
            }
        });

        requestQueue.add(weatherDataRequest);
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

    private void setHomePageData(JSONObject response, String fullCity) {
        try {
            MainActivity.weatherJSON = response;
            JSONObject current = (JSONObject) response.get("currently");

            // Set icon image
            ImageView icon= findViewById(R.id.icon);
            setImage(icon, current.getString("icon"));

            // Set Temperature
            TextView temperature = findViewById(R.id.temperature);
            int temp = Math.round(Float.parseFloat(current.getString("temperature")));
            temperature.setText(temp + "°F");

            // Set Summary
            TextView summary = findViewById(R.id.summary);
            summary.setText(current.getString("summary"));

            // Set Humidity
            int humid = Math.round(Float.parseFloat(current.getString("humidity")) * 100);
            TextView humidity = findViewById(R.id.humidVal);
            humidity.setText(humid + "%");

            // Set WindSpeed
            TextView windSpeed = findViewById(R.id.windSpeedVal);
            windSpeed.setText(current.getString("windSpeed") + " mph");

            // Set Visibility
            TextView visibility = findViewById(R.id.visibilityVal);
            visibility.setText(current.getString("visibility") + " km");

            // Set Pressure
            TextView pressure = findViewById(R.id.pressureVal);
            pressure.setText(current.getString("pressure") + " mb");

            // Set daily table
            TableLayout dailyTable = findViewById(R.id.daily_table);

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

    // On click handler for the card to go to the 2nd page
    public void showSummary(View view) {
        Log.d("Card has been clicked", view.toString());
        Intent receivedIntent = getIntent();
        Intent myIntent = new Intent(getBaseContext(),   DetailedWeather.class);
        myIntent.putExtra("city", receivedIntent.getStringExtra("city"));
        myIntent.putExtra("weatherJSON", receivedIntent.getStringExtra("weatherJSON"));
        myIntent.putExtra("justCity", receivedIntent.getStringExtra("justCity"));
        startActivity(myIntent);
    }

    // Adding search button to the menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the search menu action bar.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        cityArray = new String[5];

        // Get the search menu item
        MenuItem searchMenuItem = menu.findItem(R.id.searchID);

        // Get SearchView object
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        // Get SearchView autocomplete object.
        searchAutoComplete = searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setBackgroundColor(Color.parseColor("#1e1c1f"));
        searchAutoComplete.setTextColor(Color.parseColor("#FFFFFF"));
        searchAutoComplete.setDropDownBackgroundResource(android.R.color.background_light);

        // Listen to search view item on click event.
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String queryString=(String)parent.getItemAtPosition(position);
                searchAutoComplete.setText("" + queryString);
            }
        });

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        // User hit enter
                        Log.d("Text submitted", query);
                        Intent searchIntent = new Intent(getBaseContext(),   SearchResults.class);
                        searchIntent.putExtra("query", query);
                        startActivity(searchIntent);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        //Autocompletion part
                        Log.d("Text changed", newText);
                        if (newText.length() > 0) {
                            showAutoCompleteOptions(newText);
                        }
                        return false;
                    }
                }
        );

        return super.onCreateOptionsMenu(menu);
    }

    private void showAutoCompleteOptions(String partialCity) {
        Log.d("Calling", "/api/getAutocomplete");
        final RequestQueue autocompleteRequestQueue = Volley.newRequestQueue(this);
        String autocompleteURL = "http://kakati-nodejs.us-east-2.elasticbeanstalk.com/api/getAutocomplete?input=" + partialCity;

        // Call Node server REST API for /api/getAutocomplete
        JsonObjectRequest autocompleteRequest = new JsonObjectRequest(autocompleteURL, null,
                new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray predictions = response.getJSONArray("predictions");

                                for (int i=0; i<predictions.length(); i++) {
                                    cityArray[i] = predictions.getJSONObject(i).getString("description");
                                }

                                // Create a new ArrayAdapter and add data to search auto complete object.
                                newsAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_dropdown_item_1line, cityArray);
                                searchAutoComplete.setAdapter(newsAdapter);
                                newsAdapter.notifyDataSetChanged();
//                                searchAutoComplete.setThreshold(1);

//                                Log.d("predictions.length()", String.valueOf(predictions.length()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error getAutocomplete", error.getMessage());
                    }
                });

        autocompleteRequestQueue.add(autocompleteRequest);
    }

    public void notifyFavAdapter(String city) {
        favouritesAdapter.notifyDataSetChanged();
    }

    public static FavouritesAdapter getFavouritesAdapter() {
        return favouritesAdapter;
    }
}
