package com.example.astroweatherfragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class FutureWeatherFragment extends Fragment {

    boolean isConnected;

    SharedPreferences sharedPreferences;

    ForecastInfo forecastInfo;
    String city, refreshTime;


    ImageView iconDay1,
            iconDay2,
            iconDay3,
            iconDay4,
            iconDay5,
            iconDay6;

    TextView label1,
            label2,
            label3,
            label4,
            label5,
            label6;

    TextView hourLabel1,
            hourLabel2,
            hourLabel3,
            hourLabel4,
            hourLabel5,
            hourLabel6;

    TextView tempLabel1,
            tempLabel2,
            tempLabel3,
            tempLabel4,
            tempLabel5,
            tempLabel6;

    ScheduledExecutorService worker;
    Runnable forecastInfoRunnable;

    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    public static FutureWeatherFragment getInstance() {
        return new FutureWeatherFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_future_weather, container, false);

        iconDay1 = view.findViewById(R.id.iconDay1);
        iconDay2 = view.findViewById(R.id.iconDay2);
        iconDay3 = view.findViewById(R.id.iconDay3);
        iconDay4 = view.findViewById(R.id.iconDay4);
        iconDay5 = view.findViewById(R.id.iconDay5);
        iconDay6 = view.findViewById(R.id.iconDay6);

        label1 = view.findViewById(R.id.label1);
        label2 = view.findViewById(R.id.label2);
        label3 = view.findViewById(R.id.label3);
        label4 = view.findViewById(R.id.label4);
        label5 = view.findViewById(R.id.label5);
        label6 = view.findViewById(R.id.label6);

        hourLabel1 = view.findViewById(R.id.hourLabel1);
        hourLabel2 = view.findViewById(R.id.hourLabel2);
        hourLabel3 = view.findViewById(R.id.hourLabel3);
        hourLabel4 = view.findViewById(R.id.hourLabel4);
        hourLabel5 = view.findViewById(R.id.hourLabel5);
        hourLabel6 = view.findViewById(R.id.hourLabel6);

        tempLabel1 = view.findViewById(R.id.tempLabel1);
        tempLabel2 = view.findViewById(R.id.tempLabel2);
        tempLabel3 = view.findViewById(R.id.tempLabel3);
        tempLabel4 = view.findViewById(R.id.tempLabel4);
        tempLabel5 = view.findViewById(R.id.tempLabel5);
        tempLabel6 = view.findViewById(R.id.tempLabel6);

        worker = Executors.newSingleThreadScheduledExecutor();


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        city = sharedPreferences.getString("city", "Warsaw");
        refreshTime = sharedPreferences.getString("time", "1");

        forecastInfo = new ForecastInfo(view.getContext(), city);

        checkInternet();

        forecastInfoRunnable = new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            checkInternet();
                        }
                    });
                }

                if (isConnected) {
                    forecastInfo.getForecastDataFromAPI();
                } else {
                    forecastInfo.getForecastDataFromFile();
                }

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (forecastInfo.isCityCorrect()) {
                                setForecastValues();
                                forecastInfo.saveForecastDataToFile();
                            } else {
                                //setWeatherValuesWhenIncorrectInput();
                            }
                        }
                    });
                }
            }
        };

        worker.scheduleAtFixedRate(forecastInfoRunnable, 0, Integer.parseInt(refreshTime), TimeUnit.MINUTES);

        return view;
    }

    public void checkInternet() {
        if(getActivity().getApplicationContext() != null){
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

    public void setForecastValues() {
        ArrayList<ForecastElement> list = forecastInfo.getForecastsList();

        String units = sharedPreferences.getString("units_preference", "0");
        if (units.equals("0")) {
            String tempF = list.get(0).getTemp();
            Double tempFDouble = Double.parseDouble(tempF);
            tempFDouble -= 273.15;
            String tempC = tempFDouble.toString().substring(0,2) + " °C";
            tempLabel1.setText(tempC);
        } else {
            String temp = list.get(0).getTemp().substring(0,3) + " °F";
            tempLabel1.setText(temp);
        }

        label1.setText(list.get(0).getDate());
        hourLabel1.setText(list.get(0).getHour());

        String iconUrl = "https://openweathermap.org/img/wn/" + list.get(0).getIcon() + "@4x.png";
        Picasso.get().load(iconUrl).into(iconDay1);

        label2.setText(list.get(1).getDate());
        hourLabel2.setText(list.get(1).getHour());
        if (units.equals("0")) {
            String tempF = list.get(1).getTemp();
            Double tempFDouble = Double.parseDouble(tempF);
            tempFDouble -= 273.15;
            String tempC = tempFDouble.toString().substring(0,2) + " °C";
            tempLabel2.setText(tempC);
        } else {
            String temp = list.get(1).getTemp().substring(0,3) + " °F";
            tempLabel2.setText(temp);
        }

        iconUrl = "https://openweathermap.org/img/wn/" + list.get(1).getIcon() + "@4x.png";
        Picasso.get().load(iconUrl).into(iconDay2);

        label3.setText(list.get(2).getDate());
        hourLabel3.setText(list.get(2).getHour());
        if (units.equals("0")) {
            String tempF = list.get(2).getTemp();
            Double tempFDouble = Double.parseDouble(tempF);
            tempFDouble -= 273.15;
            String tempC = tempFDouble.toString().substring(0,2) + " °C";
            tempLabel3.setText(tempC);
        } else {
            String temp = list.get(2).getTemp().substring(0,3) + " °F";
            tempLabel3.setText(temp);
        }

        iconUrl = "https://openweathermap.org/img/wn/" + list.get(2).getIcon() + "@4x.png";
        Picasso.get().load(iconUrl).into(iconDay3);

        label4.setText(list.get(3).getDate());
        hourLabel4.setText(list.get(3).getHour());
        if (units.equals("0")) {
            String tempF = list.get(3).getTemp();
            Double tempFDouble = Double.parseDouble(tempF);
            tempFDouble -= 273.15;
            String tempC = tempFDouble.toString().substring(0,2) + " °C";
            tempLabel4.setText(tempC);
        } else {
            String temp = list.get(3).getTemp().substring(0,3) + " °F";
            tempLabel4.setText(temp);
        }

        iconUrl = "https://openweathermap.org/img/wn/" + list.get(3).getIcon() + "@4x.png";
        Picasso.get().load(iconUrl).into(iconDay4);

        label5.setText(list.get(4).getDate());
        hourLabel5.setText(list.get(4).getHour());
        if (units.equals("0")) {
            String tempF = list.get(4).getTemp();
            Double tempFDouble = Double.parseDouble(tempF);
            tempFDouble -= 273.15;
            String tempC = tempFDouble.toString().substring(0,2) + " °C";
            tempLabel5.setText(tempC);
        } else {
            String temp = list.get(4).getTemp().substring(0,3) + " °F";
            tempLabel5.setText(temp);
        }
        iconUrl = "https://openweathermap.org/img/wn/" + list.get(4).getIcon() + "@4x.png";
        Picasso.get().load(iconUrl).into(iconDay5);
        label6.setText(list.get(5).getDate());
        hourLabel6.setText(list.get(5).getHour());
        if (units.equals("0")) {
            String tempF = list.get(5).getTemp();
            Double tempFDouble = Double.parseDouble(tempF);
            tempFDouble -= 273.15;
            String tempC = tempFDouble.toString().substring(0,2) + " °C";
            tempLabel6.setText(tempC);
        } else {
            String temp = list.get(5).getTemp().substring(0,3) + " °F";
            tempLabel6.setText(temp);
        }
        iconUrl = "https://openweathermap.org/img/wn/" + list.get(5).getIcon() + "@4x.png";
        Picasso.get().load(iconUrl).into(iconDay6);

    }

    @Override
    public void onResume() {
        super.onResume();
        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                String city = sharedPreferences.getString("city", "Warsaw");

                if(getActivity() != null){
                    forecastInfo = new ForecastInfo(getActivity().getApplicationContext(), city);
                }

                worker.execute(forecastInfoRunnable);
            }
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }
}