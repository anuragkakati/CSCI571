package com.example.weatherapp_ak;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class TodayFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View todayView = inflater.inflate(R.layout.today_fragment_layout, container, false);
        setTodayData(todayView);

        return todayView;
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

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    public void setTodayData(View view) {
        Intent detailedWeatherIntent = getActivity().getIntent();

        try {
            JSONObject weatherJSON = new JSONObject(detailedWeatherIntent.getStringExtra("weatherJSON"));
            JSONObject current = (JSONObject) weatherJSON.get("currently");

            // Set windspeed
            TextView windSpeed = view.findViewById(R.id.todayWindSpeedID);
            BigDecimal windSpeedVal = round(Float.parseFloat(current.getString("windSpeed")), 2);
            windSpeed.setText(windSpeedVal + " mph");

            // Set pressure
            TextView pressure = view.findViewById(R.id.todayPressureID);
            BigDecimal pressureVal = round(Float.parseFloat(current.getString("pressure")), 2);
            pressure.setText(pressureVal + " mb");

            // Set precipitation
            TextView precip = view.findViewById(R.id.todayPrecipID);
            BigDecimal precipVal = round(Float.parseFloat(current.getString("precipIntensity")), 2);
            precip.setText(precipVal + " mmph");

            // Set temperature
            TextView temperature = view.findViewById(R.id.todayTempID);
            int temp = Math.round(Float.parseFloat(current.getString("temperature")));
            temperature.setText(temp + "°F");

            // Set icon
            ImageView icon = view.findViewById(R.id.todayIcon);
            setImage(icon, current.getString("icon"));

            // Set summary
            TextView summary = view.findViewById(R.id.todaySummaryID);
            summary.setText(current.getString("summary"));

            // Set humidity
            int humid = Math.round(Float.parseFloat(current.getString("humidity")) * 100);
            TextView humidity = view.findViewById(R.id.todayHumidID);
            humidity.setText(humid + "%");

            // Set visibility
            TextView visibility = view.findViewById(R.id.todayVisibilityID);
            BigDecimal visibilityVal = round(Float.parseFloat(current.getString("visibility")), 2);
            visibility.setText(visibilityVal + " km");

            // Set cloud cover
            TextView cloudCover = view.findViewById(R.id.todayCloudCoverID);
            int cloudCoverVal = Math.round(Float.parseFloat(current.getString("cloudCover")) * 100);
            cloudCover.setText(cloudCoverVal + "%");

            // Set ozone
            TextView ozone = view.findViewById(R.id.todayOzoneID);
            BigDecimal ozoneVal = round(Float.parseFloat(current.getString("ozone")), 2);
            ozone.setText(ozoneVal + "DU");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
