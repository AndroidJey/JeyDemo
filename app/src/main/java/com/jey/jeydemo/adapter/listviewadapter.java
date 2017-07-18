package com.jey.jeydemo.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.jey.jeydemo.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by jey on 2017/3/23.
 */

public class listviewadapter extends RecyclerView.Adapter {
    private List<VinData> mMovies;


    public void setData(List<VinData> movies) {
        mMovies = movies;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new listviewadapter.ListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.moive_item,null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final VinData movie = mMovies.get(position);
        final listviewadapter.ListHolder movieHolder = (listviewadapter.ListHolder) holder;
        movieHolder.mImageView.setTag(R.id.image_tag,movie.getPhotoUrl());
        ImageLoader.getInstance().displayImage("http://192.168.1.130" + movie.getPhotoUrl(), movieHolder.mImageView,
                new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                ((ImageView) view).setImageResource(R.mipmap.image_default);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (view.getTag(R.id.image_tag) != null && imageUri == view.getTag(R.id.image_tag)){
                    ((ImageView) view).setImageBitmap(loadedImage);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
        movieHolder.title.setText(movie.getNum());

    }

    @Override
    public int getItemCount() {
        return mMovies == null ? 0:mMovies.size();
    }

    public static class ListHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public TextView title;
        public ListHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.movie_image);
            title = (TextView) itemView.findViewById(R.id.movie_title);
        }
    }
}
