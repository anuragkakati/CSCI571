package com.example.weatherapp_ak;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.Map;

public class FavouritesAdapter extends FragmentStatePagerAdapter {

    private final Context mContext;
    ArrayList<String> favCityList = new ArrayList<>();

    public FavouritesAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;

        // Get fav cities from shared preference
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.shared_preference_file), context.MODE_PRIVATE);

        Map<String,?> keys = sharedPref.getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()) {
            favCityList.add(entry.getKey());
        }

    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        if (position == 0) {
            fragment = new CurrentCityFragment();
        } else {
            fragment = new FavCityFragment(favCityList.get(position-1));
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return favCityList.size() + 1;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE ;
    }

    public void removeFromFav() {
        favCityList.clear();

        // Get fav cities from shared preference
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                mContext.getString(R.string.shared_preference_file), mContext.MODE_PRIVATE);

        Map<String,?> keys = sharedPref.getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()) {
            favCityList.add(entry.getKey());
        }

    }
}
