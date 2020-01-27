package com.example.weatherapp_ak;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PhotosFragment extends Fragment {

    private String[] cityImageURL;

    private RecyclerView recyclerView;

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("photsFragment", "created");
        View photosView = inflater.inflate(R.layout.photos_fragment_layout, container, false);

        cityImageURL = new String[8];

        recyclerView = photosView.findViewById(R.id.imageRecyclerView);

        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(layoutManager);

        fillCityURLs(this.cityImageURL);

        adapter = new RecyclerAdapter(getContext(), this.cityImageURL);
        recyclerView.setAdapter(adapter);

        return photosView;
    }

    private void fillCityURLs(String[] cityImageURLArray) {
        final RequestQueue reqQueue = Volley.newRequestQueue(this.getContext());
        String fullCity = getActivity().getIntent().getStringExtra("justCity");
        Log.d("justCity data", fullCity);
        // TODO : Extract city name and change the URL

//        String cityURL = "http://10.0.2.2:5000/api/getCityImages?city=" + fullCity;
        String cityURL = "http://kakati-nodejs.us-east-2.elasticbeanstalk.com/api/getCityImages?city=" + fullCity;
        // Call Node server REST API for /api/getCityImages
        JsonObjectRequest cityURLRequest = new JsonObjectRequest(cityURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray items = response.getJSONArray("items");
                            Log.d("items.length()", String.valueOf(items.length()));
                            for (int i=0; i<items.length(); i++) {
                                cityImageURL[i] = items.getJSONObject(i).getString("link");
                            }
                            Log.d("cityImageURL size", String.valueOf(cityImageURL.length));

                            adapter = new RecyclerAdapter(getContext(), cityImageURL);
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error with Node API", error.getMessage());
                        }
                    });

        reqQueue.add(cityURLRequest);
    }
}
