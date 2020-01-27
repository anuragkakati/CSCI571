package com.example.weatherapp_ak;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.weatherapp_ak.ui.main.SectionsPagerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailedWeather extends AppCompatActivity {

    JSONObject weatherJSONData;
    String cityData;
    String justCityData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_weather);

        // Get city and weatherData from previous activity
        String fullCity = getIntent().getStringExtra("city");
        cityData = fullCity;
        JSONObject weatherJSON = null;
        Log.d("fullCity received : ", fullCity);

        justCityData = getIntent().getStringExtra("justCity");

        try {
            weatherJSON = new JSONObject(getIntent().getStringExtra("weatherJSON"));
            weatherJSONData = weatherJSON;
            Log.d("weather data : ", weatherJSON.getString("timezone"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Set full city as title in Action Bar
        ActionBar myActionBar = getSupportActionBar();
        myActionBar.setTitle(fullCity);

        // Set back button
        myActionBar.setDisplayHomeAsUpEnabled(true);
        myActionBar.setHomeButtonEnabled(true);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // Set tab icon images
        tabs.getTabAt(0).setIcon(R.drawable.calendar_today);
        tabs.getTabAt(1).setIcon(R.drawable.trending_up);
        tabs.getTabAt(2).setIcon(R.drawable.google_photos);

        // Change color of image for selected tab
        tabs.addOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab selectedTab) {
                        super.onTabSelected(selectedTab);
                        selectedTab.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab selectedTab) {
                        super.onTabUnselected(selectedTab);
                        selectedTab.getIcon().setColorFilter(Color.parseColor("#828282"), PorterDuff.Mode.SRC_IN);
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab selectedTab) {
                        super.onTabReselected(selectedTab);
                    }
                }
        );
    }

    // Adding twitter button to the menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.twitter_menu, menu);
        return true;
    }

    // For Twitter button action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Up button is clicked
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                onBackPressed();
                return true;

            // Twitter button is clicked
            case R.id.twitterID:
                Log.d("Twitter button", "clicked");
                try {
                    String tweetText = "Check Out " +
                            this.cityData +
                            "'s Weather! It is " +
                            this.weatherJSONData.getJSONObject("currently").getString("temperature") +
                            "° F!&hashtags=CSCI571WeatherSearch";

                    String tweetURL = "https://twitter.com/intent/tweet?text=" + tweetText;

                    Uri tweetURI = Uri.parse(tweetURL);
                    Intent intent = new Intent(Intent.ACTION_VIEW, tweetURI);
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}