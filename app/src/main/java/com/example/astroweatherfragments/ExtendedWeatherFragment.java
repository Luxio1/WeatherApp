package com.example.astroweatherfragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExtendedWeatherFragment extends Fragment {

    boolean isConnected;

    SharedPreferences sharedPreferences;
    String refreshTime;

    TextView feelsLikeValue,
    humidityValue,
    windSpeedValue,
    cloudsValue,
    tempMinValue,
    tempMaxValue;

    WeatherInfo weatherInfo;

    ScheduledExecutorService worker;

    Runnable weatherInfoRunnable;

    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    public static ExtendedWeatherFragment getInstance() {return new ExtendedWeatherFragment();}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_extended_weather, container, false);

        feelsLikeValue = view.findViewById(R.id.feelsLikeValue);
        humidityValue = view.findViewById(R.id.humidityValue);
        windSpeedValue= view.findViewById(R.id.windSpeedValue);
        cloudsValue = view.findViewById(R.id.cloudsValue);
        tempMinValue = view.findViewById(R.id.tempMinValue);
        tempMaxValue = view.findViewById(R.id.tempMaxValue);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(view.getContext());

        String city = sharedPreferences.getString("city", "Warsaw");
        refreshTime = sharedPreferences.getString("time", "1");
        worker = Executors.newSingleThreadScheduledExecutor();

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Warsaw"));

        checkInternet();
        System.out.println(isConnected);


        weatherInfo = new WeatherInfo(view.getContext(), city);

        weatherInfoRunnable = new Runnable() {
            @Override
            public void run() {
                if(getActivity() != null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            checkInternet();
                        }
                    });
                }

                if(isConnected){
                    weatherInfo.getWeatherDataFromAPI();
                } else {
                    weatherInfo.getWeatherDataFromFile();
                }

                if(getActivity() != null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(weatherInfo.isCityCorrect()){
                                setWeatherValues();
                                weatherInfo.saveWeatherDataToFile();
                            } else {
                                setWeatherValuesWhenIncorrectInput();
                            }
                        }
                    });
                }


            }
        };

        worker.scheduleAtFixedRate(weatherInfoRunnable, 0, Integer.parseInt(refreshTime), TimeUnit.MINUTES);

        return view;
    }

    public void checkInternet() {
        if(getActivity() != null){
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivityManager != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR");
                        isConnected = true;
                        return;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI");
                        isConnected = true;
                        return;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET");
                        isConnected = true;
                        return;
                    }
                }
            }
            Toast.makeText(getActivity().getApplicationContext(), "Internet is not connected. Data may be old.", Toast.LENGTH_LONG).show();
            isConnected = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                String city = sharedPreferences.getString("city", "Warsaw");

                if(getActivity() != null){
                    weatherInfo = new WeatherInfo(getActivity().getApplicationContext(), city);
                }

                worker.execute(weatherInfoRunnable);
            }
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        worker.shutdown();
    }


    public void setWeatherValues() {

        String units = sharedPreferences.getString("units_preference", "0");
        if (units.equals("0")) {
            feelsLikeValue.setText(weatherInfo.getFeelsLikeTempCelsius());
        } else {
            feelsLikeValue.setText(weatherInfo.getFeelsLikeTempFahrenheit());
        }

        if (units.equals("0")) {
            tempMinValue.setText(weatherInfo.getTempMinCelsius());
        } else {
            tempMinValue.setText(weatherInfo.getTempMinFahrenheit());
        }

        if (units.equals("0")) {
            tempMaxValue.setText(weatherInfo.getTempMaxCelsius());
        } else {
            tempMaxValue.setText(weatherInfo.getTempMaxFahrenheit());
        }

        humidityValue.setText(weatherInfo.getHumidity());
        windSpeedValue.setText(weatherInfo.getWindSpeed());
        cloudsValue.setText(weatherInfo.getCloudsCount());

    }

    public void setWeatherValuesWhenIncorrectInput() {
        String none = "None";
        feelsLikeValue.setText(none);
        humidityValue.setText(none);
        windSpeedValue.setText(none);
        cloudsValue.setText(none);
        tempMaxValue.setText(none);
        tempMinValue.setText(none);
    }
}