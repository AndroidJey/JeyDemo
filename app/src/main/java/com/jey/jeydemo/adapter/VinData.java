package com.jey.jeydemo.adapter;

import com.jey.jeydemo.movie.Movie;

/**
 * Created by jey on 2017/3/31.
 */

public class VinData {
    private String Num;
    private String PhotoUrl;

    public VinData() {
    }

    public VinData(String num, String photoUrl) {
        Num = num;
        PhotoUrl = photoUrl;
    }

    public String getNum() {
        return Num;
    }

    public void setNum(String num) {
        Num = num;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "VinData{" +
                "Num='" + Num + '\'' +
                ", PhotoUrl='" + PhotoUrl + '\'' +
                '}';
    }
}
