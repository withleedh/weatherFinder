package com.example.leedongho.weatherfinder;

import android.graphics.drawable.Drawable;

/**
 * Created by DONGHO on 2016. 10. 3..
 */

public class WeatherData {

    private Drawable icon;
    private String cityName;
    private String description;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
