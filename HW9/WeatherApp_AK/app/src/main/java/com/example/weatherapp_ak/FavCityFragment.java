package com.example.weatherapp_ak;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static com.example.weatherapp_ak.MainActivity.getFavouritesAdapter;

public class FavCityFragment extends Fragment {
    String fullCity;
    String justCity;
    RelativeLayout spinnerLayout;
    View favCityView;
    JSONObject weatherJSON;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public FavCityFragment(String city) {
        fullCity = city;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        favCityView = inflater.inflate(R.layout.fav_city_fragment_layout, container, false);

        spinnerLayout = favCityView.findViewById(R.id.favProgressBarLayout);

        // Set spinner
        spinnerLayout.setVisibility(View.VISIBLE);

        setSearchResultContents(favCityView);

        // Get the shared preference of the current app
        sharedPref = getActivity().getSharedPreferences(
                getString(R.string.shared_preference_file), Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        // Set on click listener for card click
        CardView summaryCard = favCityView.findViewById(R.id.favWeatherSummary);
        summaryCard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(getActivity(), DetailedWeather.class);

                        // Set intent variables for parent activity for card view
                        myIntent.putExtra("city", fullCity);
                        myIntent.putExtra("justCity", justCity);
                        myIntent.putExtra("weatherJSON", weatherJSON.toString());

                        startActivity(myIntent);
                    }
                }
        );

        // Set on click listener for removing from favorites
        FloatingActionButton removeFavButton = favCityView.findViewById(R.id.favButton);
        removeFavButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor.remove(fullCity).commit();
                        Log.d("Clicked", "here");

                        // Add Toast
                        Toast.makeText(getContext(), fullCity+" was removed from Favorites",
                                Toast.LENGTH_LONG).show();

                        // Remove city from shared preference
                        FavouritesAdapter favAdapter = getFavouritesAdapter();
                        favAdapter.removeFromFav();
                        favAdapter.notifyDataSetChanged();
                    }
                }
        );
        return favCityView;
    }

    private void setSearchResultContents(View favCityView) {
        // Set city from search query
        TextView cityView = favCityView.findViewById(R.id.favCity);
        cityView.setText(fullCity);

        // Extract just city name for google image search
        justCity = fullCity.split(",")[0];

        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//        String getWeatherURL = "http://10.0.2.2:5000/api/getWeather?street=" + fullCity + "&city=&state=";
        String getWeatherURL = "http://kakati-nodejs.us-east-2.elasticbeanstalk.com/api/getWeather?street=" + fullCity + "&city=&state=";

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

    // To round off
    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
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

    private void setHomePageData(JSONObject response) {
        weatherJSON = response;

        try {
            JSONObject current = (JSONObject) response.get("currently");

            // Set icon image
            ImageView icon= favCityView.findViewById(R.id.favIcon);
            setImage(icon, current.getString("icon"));

            // Set Temperature
            TextView temperature = favCityView.findViewById(R.id.favTemperature);
            int temp = Math.round(Float.parseFloat(current.getString("temperature")));
            temperature.setText(temp + "°F");

            // Set Summary
            TextView summary = favCityView.findViewById(R.id.favSummary);
            summary.setText(current.getString("summary"));

            // Set Humidity
            int humid = Math.round(Float.parseFloat(current.getString("humidity")) * 100);
            TextView humidity = favCityView.findViewById(R.id.favHumidVal);
            humidity.setText(humid + "%");

            // Set WindSpeed
            TextView windSpeed = favCityView.findViewById(R.id.favWindSpeedVal);
            BigDecimal windSpeedVal = round(Float.parseFloat(current.getString("windSpeed")), 2);
            windSpeed.setText(windSpeedVal + " mph");

            // Set Visibility
            TextView visibility = favCityView.findViewById(R.id.favVisibilityVal);
            BigDecimal visibilityVal = round(Float.parseFloat(current.getString("visibility")), 2);
            visibility.setText(visibilityVal + " km");

            // Set Pressure
            TextView pressure = favCityView.findViewById(R.id.favPressureVal);
            BigDecimal pressureVal = round(Float.parseFloat(current.getString("pressure")), 2);
            pressure.setText(pressureVal + " mb");

            // Set daily table
            TableLayout dailyTable = favCityView.findViewById(R.id.fav_daily_table);

            JSONObject daily = (JSONObject) response.get("daily");
            JSONArray data = daily.getJSONArray("data");

            int jsonCounter = 0;
            for (int i=0; i<dailyTable.getChildCount(); i++) {
                if (i % 2 != 0) {
                    continue;
                }
                TableRow row = (TableRow) dailyTable.getChildAt(i);

                // Date format
                String pattern = "MM/dd/yyyy";
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
}
