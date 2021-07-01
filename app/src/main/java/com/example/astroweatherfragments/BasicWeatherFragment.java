package com.example.astroweatherfragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.os.Handler;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class BasicWeatherFragment extends Fragment {

    boolean isConnected;

    SharedPreferences sharedPreferences;
    String refreshTime;
    Handler timeHandler;

    TextView cityValue,
            tempValue,
            pressureValue,
            conditionsValue,
            longitudeValue,
            latitudeValue,
            timeLabel;

    ImageView weatherImageView;

    WeatherInfo weatherInfo;

    ScheduledExecutorService worker;

    public static BasicWeatherFragment getInstance() {
        return new BasicWeatherFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic_weather, container, false);

        cityValue = view.findViewById(R.id.cityValue);
        tempValue = view.findViewById(R.id.tempValue);
        pressureValue = view.findViewById(R.id.pressureValue);
        conditionsValue = view.findViewById(R.id.conditionsValue);
        longitudeValue = view.findViewById(R.id.longitudeValue);
        latitudeValue = view.findViewById(R.id.latitudeValue);
        timeLabel = view.findViewById(R.id.timeValue);

        weatherImageView = view.findViewById(R.id.weatherImageView);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(view.getContext());

        String city = sharedPreferences.getString("city", "Warsaw");
        refreshTime = sharedPreferences.getString("time", "1");
        timeHandler = new Handler();
        worker = Executors.newSingleThreadScheduledExecutor();

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Warsaw"));

        checkInternet();
        System.out.println(isConnected);

        timeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timeHandler.postDelayed(this, 1000);
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss a");
                String formattedDate = df.format(Calendar.getInstance().getTime());

                timeLabel.setText(formattedDate);
            }
        }, 1);

        weatherInfo = new WeatherInfo(view.getContext(), city);

       Runnable weatherInfoRunnable = new Runnable() {
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
        if (getActivity() != null) {
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
    public void onDestroy() {
        super.onDestroy();
        timeHandler.removeCallbacksAndMessages(null);
        worker.shutdown();
    }

    public void setWeatherValues() {
        cityValue.setText(weatherInfo.getCity());

        String units = sharedPreferences.getString("units_preference", "0");
        if (units.equals("0")) {
            tempValue.setText(weatherInfo.getTempCelsius());
        } else {
            tempValue.setText(weatherInfo.getTempFahrenheit());
        }

        pressureValue.setText(weatherInfo.getPressure());
        conditionsValue.setText(weatherInfo.getDescription());
        longitudeValue.setText(weatherInfo.getLongitude());
        latitudeValue.setText(weatherInfo.getLatitude());
        String iconUrl = "https://openweathermap.org/img/wn/" + weatherInfo.getIcon() + "@4x.png";
        Picasso.get().load(iconUrl).into(weatherImageView);
    }

    public void setWeatherValuesWhenIncorrectInput() {
        String none = "None";
        cityValue.setText("Incorrect input");
        tempValue.setText(none);
        pressureValue.setText(none);
        conditionsValue.setText(none);
        longitudeValue.setText(none);
        latitudeValue.setText(none);
    }
}