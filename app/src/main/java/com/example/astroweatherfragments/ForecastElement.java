package com.example.astroweatherfragments;

public class ForecastElement {
    private String date;
    private String hour;
    private String temp;
    private String icon;

    public ForecastElement(String date, String hour, String temp, String icon) {
        this.date = date;
        this.hour = hour;
        this.temp = temp;
        this.icon = icon;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
