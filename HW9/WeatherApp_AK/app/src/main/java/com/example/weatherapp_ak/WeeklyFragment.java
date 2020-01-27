package com.example.weatherapp_ak;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeeklyFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View weeklyView  = inflater.inflate(R.layout.weekly_fragment_layout, container, false);

        Intent detailedWeatherIntent = getActivity().getIntent();
        try {
            JSONObject weatherJSON = new JSONObject(detailedWeatherIntent.getStringExtra("weatherJSON"));
            JSONObject weekly = (JSONObject) weatherJSON.get("daily");
            JSONArray dataArray = weekly.getJSONArray("data");

            // Set card data
            setWeeklyCard(weeklyView, weekly);

            // Plot the chart
            plotWeeklyChart(weeklyView, dataArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return weeklyView;
    }

    private void plotWeeklyChart(View weeklyView, JSONArray dataArray) throws JSONException {
        LineChart chart = weeklyView.findViewById(R.id.weeklyChart);

        // Get temperature data from JSON
        List<Integer> tempLowList = getTempLowData(dataArray, "temperatureLow");
        List<Integer> tempHighList = getTempLowData(dataArray, "temperatureHigh");

        // Set data for the chart
        setDataForChart(chart, tempLowList, tempHighList);

    }

    private void setDataForChart(LineChart chart, List<Integer> tempLowList, List<Integer> tempHighList) {
        ArrayList<Entry> lowEntries = new ArrayList<>();
        ArrayList<Entry> highEntries = new ArrayList<>();

        // Add Entries for low and high Lists
        for (int i=0; i<tempLowList.size(); i++) {
            lowEntries.add(new Entry(i, tempLowList.get(i)));
            highEntries.add(new Entry(i, tempHighList.get(i)));
        }

        // Add high and low entry lists to LineDataSet
        LineDataSet lowSet = new LineDataSet(lowEntries, "Minimum Temperature");
        LineDataSet highSet = new LineDataSet(highEntries, "Maximum Temperature");

        // Set chart properties
        // Set color for the lines
        lowSet.setColor(ColorTemplate.rgb("bd82fd"));
        highSet.setColor(ColorTemplate.rgb("faa100"));

        // Make the circles white
        lowSet.setCircleColor(Color.WHITE);
        highSet.setCircleColor(Color.WHITE);

        // Legend styling
        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE);
        legend.setTextSize(15f);
        legend.setXEntrySpace(30f);

        // Set X axis style
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);

        // Set Y axis style
        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        leftAxis.setDrawAxisLine(true);
        rightAxis.setDrawAxisLine(true);
        leftAxis.setTextColor(Color.WHITE);
        rightAxis.setTextColor(Color.WHITE);

        // Add high and low LineDataSets to LineData
        LineData data = new LineData(lowSet, highSet);

        // Set data for the chart
        chart.setData(data);

        // Disable interaction with graph
        chart.setTouchEnabled(false);
    }

    private List<Integer> getTempLowData(JSONArray dataArray, String key) throws JSONException {
        List<Integer> tempList = new ArrayList<>();

        for (int i=0; i<dataArray.length(); i++) {
            tempList.add(Math.round(Float.parseFloat(dataArray.getJSONObject(i).getString(key))));
        }

        return tempList;
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

    public void setWeeklyCard(View view, JSONObject weekly) {

        try {
            // Set icon
            ImageView icon = view.findViewById(R.id.weeklyIcon);
            setImage(icon, weekly.getString("icon"));

            // Set summary
            TextView summary = view.findViewById(R.id.weeklySummary);
            summary.setText(weekly.getString("summary"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
