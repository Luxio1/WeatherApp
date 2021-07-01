package com.example.astroweatherfragments;

import java.util.Calendar;
import java.util.TimeZone;

import com.astrocalculator.*;

public class Info {
    private Calendar calendar;
    private AstroDateTime astroDateTime;
    private AstroCalculator astroCalculator;

    public Info(Calendar calendar, AstroCalculator.Location location)
    {
        this.calendar = calendar;
        astroDateTime = new AstroDateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), 2, false);
        astroCalculator = new AstroCalculator(astroDateTime, location);
    }

    public void updateTime() {
        calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Warsaw"));
        astroDateTime = new AstroDateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), 2, false);
        astroCalculator.setDateTime(astroDateTime);
    }

    public void updateLocation(AstroCalculator.Location location) {
        astroCalculator = new AstroCalculator(astroDateTime, location);
    }

    public String getSunRiseTime() {
        String result;
        int hour = astroCalculator.getSunInfo().getSunrise().getHour();
        int minute = astroCalculator.getSunInfo().getSunrise().getMinute();
        int second = astroCalculator.getSunInfo().getSunrise().getSecond();

        result = parseToString(hour) + ":" + parseToString(minute) + ":" + parseToString(second);
        return result;
    }

    public String getSunRiseAzimuth() {
        double azimuth = astroCalculator.getSunInfo().getAzimuthRise();
        return Double.toString(round(azimuth, 2));
    }

    public String getSunSetTime() {
        String result;
        int hour = astroCalculator.getSunInfo().getSunset().getHour();
        int minute = astroCalculator.getSunInfo().getSunset().getMinute();
        int second = astroCalculator.getSunInfo().getSunset().getSecond();

        result = parseToString(hour) + ":" + parseToString(minute) + ":" + parseToString(second);
        return result;
    }

    public String getSunSetAzimuth() {
        double azimuth = astroCalculator.getSunInfo().getAzimuthSet();
        return Double.toString(round(azimuth, 2));
    }

    public String getCivilRise()
    {
        int hour = astroCalculator.getSunInfo().getTwilightMorning().getHour();
        int minute = astroCalculator.getSunInfo().getTwilightMorning().getMinute();
        String result = parseToString(hour) + ":" + parseToString(minute);
        return result;
    }
    public String getCivilSet()
    {
        int hour = astroCalculator.getSunInfo().getTwilightEvening().getHour();
        int minute = astroCalculator.getSunInfo().getTwilightEvening().getMinute();
        String result = parseToString(hour) + ":" + parseToString(minute);
        return result;
    }


    public String getMoonRiseTime()
    {
        String result;
        int hour = astroCalculator.getMoonInfo().getMoonrise().getHour();
        int minute = astroCalculator.getMoonInfo().getMoonrise().getMinute();
        int second = astroCalculator.getMoonInfo().getMoonrise().getSecond();
        result = parseToString(hour) + ":" + parseToString(minute) + ":" + parseToString(second);
        return result;
    }
    public String getMoonSetTime()
    {
        String result;
        int hour = astroCalculator.getMoonInfo().getMoonset().getHour();
        int minute = astroCalculator.getMoonInfo().getMoonset().getMinute();
        int second = astroCalculator.getMoonInfo().getMoonset().getSecond();
        result = parseToString(hour) + ":" + parseToString(minute) + ":" + parseToString(second);
        return result;
    }
    public String getSynodicDay()
    {
        double month = astroCalculator.getMoonInfo().getAge();
        month = round(month, 2);
        return Double.toString(month);
    }

    public String getNewMoon()
    {
        int year = astroCalculator.getMoonInfo().getNextNewMoon().getYear();
        int month = astroCalculator.getMoonInfo().getNextNewMoon().getMonth();
        int day = astroCalculator.getMoonInfo().getNextNewMoon().getDay();
        return year + "." + parseToString(month) + "." + parseToString(day);
    }

    public String getFullMoon()
    {
        int year = astroCalculator.getMoonInfo().getNextFullMoon().getYear();
        int month = astroCalculator.getMoonInfo().getNextFullMoon().getMonth();
        int day = astroCalculator.getMoonInfo().getNextFullMoon().getDay();
        return year + "." + parseToString(month) + "." + parseToString(day);
    }

    public String getMoonPhase()
    {
        double phase = astroCalculator.getMoonInfo().getIllumination();
        phase = round(phase, 2);
        return Double.toString(phase);
    }

    public String parseToString(int number) {
        String result;
        if(number < 10) {
            result = "0" + number;
        }
        else {
            result = "" + number;
        }
        return result;
    }

    public static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
