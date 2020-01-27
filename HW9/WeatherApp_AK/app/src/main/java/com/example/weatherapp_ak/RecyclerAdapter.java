package com.example.weatherapp_ak;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ImageViewHolder> {

    private String[] cityImageURLs;
    private Context context;

    public RecyclerAdapter(Context context, String[] cityImageURLs) {
        this.context = context;
        this.cityImageURLs = cityImageURLs;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_images_layout, parent, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(view);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
//        Picasso.with(context).load(cityImageURLs[position]).fit().into(holder.cityImage);
        Glide.with(context).load(cityImageURLs[position]).into(holder.cityImage);
    }

    @Override
    public int getItemCount() {
        return cityImageURLs.length;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView cityImage;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            cityImage = itemView.findViewById(R.id.cityImage);
        }
    }
}
