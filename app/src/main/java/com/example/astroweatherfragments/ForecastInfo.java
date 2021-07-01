package com.example.astroweatherfragments;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class ForecastInfo {
    private final String url = "https://api.openweathermap.org/data/2.5/forecast";
    private final String apiKey = "e6af170b98f654ed6790f09ce43bdf7f";

    private final String cityName;
    DecimalFormat df = new DecimalFormat("#.##");
    private final Context context;
    String tempUrl = "";



    ArrayList<ForecastElement> forecastList;

    JSONObject jsonForecast;

    private boolean isCityCorrect;

    public ForecastInfo(Context context, String cityName) {
        this.context = context;
        this.cityName = cityName;
    }


    public void getForecastDataFromAPI() {
        isCityCorrect = false;
        String pageUrl = url + "?q=" + cityName + "&appid=" + apiKey;
        URL responseUrl;
        HttpURLConnection conn = null;
        int responseCode = 0;


        try {
            responseUrl = new URL(pageUrl);
            conn = (HttpURLConnection) responseUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                isCityCorrect = false;
            } else {
                StringBuilder inline = new StringBuilder();
                Scanner scanner = new Scanner(responseUrl.openStream());
                while (scanner.hasNext()) {
                    inline.append(scanner.nextLine());
                }

                String content = inline.toString();

                scanner.close();

                jsonForecast = new JSONObject(content);
                setForecastInfo();
                isCityCorrect = true;
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void getForecastDataFromFile() {
        try {
            String filename = cityName.substring(0, 1).toUpperCase() + cityName.substring(1) + "Forecast.json";
            FileInputStream fis = context.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            System.out.println("Odczytano plik JSON");
            jsonForecast = new JSONObject(sb.toString());
            setForecastInfo();
            isCityCorrect = true;
        } catch (IOException | JSONException fileNotFound) {
            System.out.println("Bład odczytu pliku JSON");
            isCityCorrect = false;
        }

    }

    public void saveForecastDataToFile() {
        String filename = cityName.substring(0, 1).toUpperCase() + cityName.substring(1) + "Forecast.json";
        String fileContents = jsonForecast.toString();

        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
            fos.close();
            System.out.println("Zapisano plik JSON");
        } catch (IOException e) {
            System.out.println("Bład zapisu pliku JSON");
            e.printStackTrace();
        }
    }

    public void setForecastInfo() {
        try {
            JSONArray jsonArray = jsonForecast.getJSONArray("list");

            forecastList = new ArrayList<ForecastElement>();

            for(int i = 0; i < 6; i++){
                JSONObject oneElement = jsonArray.getJSONObject(i);

                JSONArray weatherArray = oneElement.getJSONArray("weather");
                JSONObject weatherElement = weatherArray.getJSONObject(0);
                String icon = weatherElement.getString("icon");


                JSONObject mainObject = oneElement.getJSONObject("main");
                String temp = mainObject.getString("temp");


                String date = oneElement.getString("dt_txt");

                ForecastElement element = new ForecastElement(date.substring(5, 10), date.substring(11, 16), temp, icon);

                forecastList.add(element);
            }

        } catch (JSONException e) {
            System.out.println("Cannot parse JSON");
        }
    }

    public boolean isCityCorrect() {
        return isCityCorrect;
    }

    public void setCityCorrect(boolean cityCorrect) {
        isCityCorrect = cityCorrect;
    }

    public ArrayList<ForecastElement> getForecastsList() {
        return forecastList;
    }

    public void setForecastsList(ArrayList<ForecastElement> forecastsList) {
        this.forecastList = forecastsList;
    }
}
