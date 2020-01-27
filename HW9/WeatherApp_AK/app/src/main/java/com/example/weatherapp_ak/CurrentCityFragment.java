package com.example.weatherapp_ak;

import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentCityFragment extends Fragment {

    JSONObject weatherJSON;
    RelativeLayout spinnerLayout;
    String justCity;
    View currentCityView;
    String fullCity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        currentCityView = inflater.inflate(R.layout.current_city_fragment_layout, container, false);

        spinnerLayout = currentCityView.findViewById(R.id.progressBarLayout);

        // Set spinner
        spinnerLayout.setVisibility(View.VISIBLE);

        getCurrentLocationWeatherData();

        return currentCityView;
    }

    private void getCurrentLocationWeatherData() {
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String ipapiURL = "http://ip-api.com/json";

        // Request a JSON response from ip-api.
        JsonObjectRequest currentLocRequest = new JsonObjectRequest(Request.Method.GET, ipapiURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String latitude;
                        String longitude;
                        // Set values to be used in UI
                        try {
                            latitude = response.getString("lat");
                            longitude = response.getString("lon");
                            TextView city = currentCityView.findViewById(R.id.city);
                            fullCity = response.getString("city") + ", " +
                                    response.getString("region") + ", " +
                                    response.getString("countryCode");
                            city.setText(fullCity);
                            getActivity().getIntent().putExtra("city", fullCity);
                            getActivity().getIntent().putExtra("justCity", response.getString("city"));

                            justCity = response.getString("city");
                            getWeatherData(latitude, longitude, requestQueue);
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

    private void getWeatherData(String lat, String lon, RequestQueue requestQueue) {
//        String getWeatherURL = "http://10.0.2.2:5000/api/getWeatherCurrentLoc?lat=" + lat + "&lon=" + lon;
        String getWeatherURL = "http://kakati-nodejs.us-east-2.elasticbeanstalk.com/api/getWeatherCurrentLoc?lat=" + lat + "&lon=" + lon;

        // Call Node server REST API for /api/getWeatherCurrentLoc
        JsonObjectRequest weatherDataRequest = new JsonObjectRequest(Request.Method.GET, getWeatherURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Set values to be used in UI
                        weatherJSON = response;
                        setHomePageData(response);

                        // Set weather Data in Intent
                        getActivity().getIntent().putExtra("weatherJSON", weatherJSON.toString());

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
        try {
            MainActivity.weatherJSON = response;
            JSONObject current = (JSONObject) response.get("currently");

            // Set icon image
            ImageView icon= currentCityView.findViewById(R.id.icon);
            setImage(icon, current.getString("icon"));

            // Set Temperature
            TextView temperature = currentCityView.findViewById(R.id.temperature);
            int temp = Math.round(Float.parseFloat(current.getString("temperature")));
            temperature.setText(temp + "°F");

            // Set Summary
            TextView summary = currentCityView.findViewById(R.id.summary);
            summary.setText(current.getString("summary"));

            // Set Humidity
            int humid = Math.round(Float.parseFloat(current.getString("humidity")) * 100);
            TextView humidity = currentCityView.findViewById(R.id.humidVal);
            humidity.setText(humid + "%");

            // Set WindSpeed
            TextView windSpeed = currentCityView.findViewById(R.id.windSpeedVal);
            BigDecimal windSpeedVal = round(Float.parseFloat(current.getString("windSpeed")), 2);
            windSpeed.setText(windSpeedVal + " mph");

            // Set Visibility
            TextView visibility = currentCityView.findViewById(R.id.visibilityVal);
            BigDecimal visibilityVal = round(Float.parseFloat(current.getString("visibility")), 2);
            visibility.setText(visibilityVal + " km");

            // Set Pressure
            TextView pressure = currentCityView.findViewById(R.id.pressureVal);
            BigDecimal pressureVal = round(Float.parseFloat(current.getString("pressure")), 2);
            pressure.setText(pressureVal + " mb");

            // Set daily table
            TableLayout dailyTable = currentCityView.findViewById(R.id.daily_table);

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

//    // On click handler for the card to go to the 2nd page
//    public void showSummary(View view) {
//        Log.d("Card has been clicked", view.toString());
//        Intent myIntent = new Intent(getContext(),   DetailedWeather.class);
//        myIntent.putExtra("city", MainActivity.fullCity);
//        myIntent.putExtra("weatherJSON", MainActivity.weatherJSON.toString());
//        myIntent.putExtra("justCity", justCity);
//        startActivity(myIntent);
//    }
}
