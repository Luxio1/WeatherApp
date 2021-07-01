package com.example.astroweatherfragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

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
import java.util.Scanner;

public class WeatherInfo {
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String apiKey = "e6af170b98f654ed6790f09ce43bdf7f";

    private String cityName;
    DecimalFormat df = new DecimalFormat("#.##");
    private final Context context;
    String tempUrl = "";

    JSONObject jsonWeather;

    private String description;
    private String longitude;
    private String latitude;
    private String tempCelsius;

    private String tempFahrenheit;
    private String windSpeed;
    private String countryName;
    private String city;
    private String cloudsCount;
    private String feelsLikeTempCelsius;
    private String feelsLikeTempFahrenheit;
    private String tempMinCelsius;
    private String tempMinFahrenheit;


    private String tempMaxCelsius;
    private String tempMaxFahrenheit;
    private String pressure;
    private String humidity;

    private String icon;

    private boolean isCityCorrect;

    public WeatherInfo(Context context, String cityName) {
        this.context = context;
        this.cityName = cityName;
    }


    public void getWeatherDataFromAPI() {
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

                jsonWeather = new JSONObject(content);

                System.out.println(jsonWeather);
                setWeatherInfo();
                isCityCorrect = true;
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void getWeatherDataFromFile() {
        try {
            String filename = cityName.substring(0, 1).toUpperCase() + cityName.substring(1) + ".json";
            FileInputStream fis = context.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            System.out.println("Odczytano plik JSON");
            jsonWeather = new JSONObject(sb.toString());
            setWeatherInfo();
            isCityCorrect = true;
        } catch (IOException | JSONException fileNotFound) {
            System.out.println("Bład odczytu pliku JSON");
            isCityCorrect = false;
        }

    }

    public void saveWeatherDataToFile() {
        String filename = cityName.substring(0, 1).toUpperCase() + cityName.substring(1) + ".json";
        String fileContents = jsonWeather.toString();

        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
            fos.close();
            System.out.println("Zapisano plik JSON");
        } catch (IOException e) {
            System.out.println("Bład zapisu pliku JSON");
            e.printStackTrace();
        }
    }

    public void setWeatherInfo() {
        try {
            JSONArray jsonArray = jsonWeather.getJSONArray("weather");
            JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
            JSONObject jsonObjectMain = jsonWeather.getJSONObject("main");
            JSONObject jsonObjectWind = jsonWeather.getJSONObject("wind");
            JSONObject jsonObjectClouds = jsonWeather.getJSONObject("clouds");
            JSONObject jsonObjectSys = jsonWeather.getJSONObject("sys");
            JSONObject jsonObjectCoord = jsonWeather.getJSONObject("coord");


            longitude = Double.toString(jsonObjectCoord.getDouble("lon"));
            latitude = Double.toString(jsonObjectCoord.getDouble("lat"));

            description = jsonObjectWeather.getString("description");
            windSpeed = jsonObjectWind.getString("speed") + " m/s";
            countryName = jsonObjectSys.getString("country");
            city = jsonWeather.getString("name");
            cloudsCount = jsonObjectClouds.getString("all");

            tempCelsius = Double.toString(jsonObjectMain.getDouble("temp") - 273.15).substring(0, 2) + " °C";
            tempFahrenheit = Double.toString(jsonObjectMain.getDouble("temp")).substring(0, 3) + " °F";

            tempMinCelsius = Double.toString(jsonObjectMain.getDouble("temp_min") - 273.15).substring(0, 2) + " °C";
            tempMinFahrenheit = Double.toString(jsonObjectMain.getDouble("temp_min")).substring(0, 3) + " °F";

            tempMaxCelsius = Double.toString(jsonObjectMain.getDouble("temp_max") - 273.15).substring(0, 2) + " °C";
            tempMaxFahrenheit = Double.toString(jsonObjectMain.getDouble("temp_max")).substring(0, 3) + " °F";

            feelsLikeTempCelsius = Double.toString(jsonObjectMain.getDouble("feels_like") - 273.15).substring(0, 2) + " °C";
            feelsLikeTempFahrenheit = Double.toString(jsonObjectMain.getDouble("feels_like")).substring(0, 3) + " °F";

            pressure = jsonObjectMain.getString("pressure") + "hPa";
            humidity = jsonObjectMain.getString("humidity");

            icon = jsonObjectWeather.getString("icon");

        } catch (JSONException e) {
            System.out.println("Cannot parse JSON");
        }
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public JSONObject getJsonWeather() {
        return jsonWeather;
    }

    public void setJsonWeather(JSONObject jsonWeather) {
        this.jsonWeather = jsonWeather;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getTempCelsius() {
        return tempCelsius;
    }

    public void setTempCelsius(String tempCelsius) {
        this.tempCelsius = tempCelsius;
    }

    public String getTempFahrenheit() {
        return tempFahrenheit;
    }

    public void setTempFahrenheit(String tempFahrenheit) {
        this.tempFahrenheit = tempFahrenheit;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCloudsCount() {
        return cloudsCount;
    }

    public void setCloudsCount(String cloudsCount) {
        this.cloudsCount = cloudsCount;
    }

    public String getFeelsLikeTempCelsius() {
        return feelsLikeTempCelsius;
    }

    public void setFeelsLikeTempCelsius(String feelsLikeTempCelsius) {
        this.feelsLikeTempCelsius = feelsLikeTempCelsius;
    }

    public String getFeelsLikeTempFahrenheit() {
        return feelsLikeTempFahrenheit;
    }

    public void setFeelsLikeTempFahrenheit(String feelsLikeTempFahrenheit) {
        this.feelsLikeTempFahrenheit = feelsLikeTempFahrenheit;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public boolean isCityCorrect() {
        return isCityCorrect;
    }

    public void setCityCorrect(boolean cityCorrect) {
        isCityCorrect = cityCorrect;
    }

    public String getTempMinCelsius() {
        return tempMinCelsius;
    }

    public void setTempMinCelsius(String tempMinCelsius) {
        this.tempMinCelsius = tempMinCelsius;
    }

    public String getTempMinFahrenheit() {
        return tempMinFahrenheit;
    }

    public void setTempMinFahrenheit(String tempMinFahrenheit) {
        this.tempMinFahrenheit = tempMinFahrenheit;
    }

    public String getTempMaxCelsius() {
        return tempMaxCelsius;
    }

    public void setTempMaxCelsius(String tempMaxCelsius) {
        this.tempMaxCelsius = tempMaxCelsius;
    }

    public String getTempMaxFahrenheit() {
        return tempMaxFahrenheit;
    }

    public void setTempMaxFahrenheit(String tempMaxFahrenheit) {
        this.tempMaxFahrenheit = tempMaxFahrenheit;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
