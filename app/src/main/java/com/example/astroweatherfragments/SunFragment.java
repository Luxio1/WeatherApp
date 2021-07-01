package com.example.astroweatherfragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astrocalculator.AstroCalculator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.os.Looper.getMainLooper;

public class SunFragment extends Fragment {

    private Info info;
    private Calendar calendar;
    private TextView timeTv, latitudeTv, longitudeTv, riseTimeTv, setTimeTv, riseAzimuthTv, setAzimuthTv, civilRiseTv, civilSetTv;

    Handler handler, timeHandler;

    SharedPreferences sharedPreferences;

    String latitude, longitude;

    public static SunFragment getInstance() {
        return new SunFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sun, container, false);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        timeTv = view.findViewById(R.id.time_value);
        latitudeTv = view.findViewById(R.id.latitude_value);
        longitudeTv = view.findViewById(R.id.longitude_value);

        riseTimeTv = view.findViewById(R.id.rise_time_value);
        riseAzimuthTv = view.findViewById(R.id.rise_azimuth_value);
        setTimeTv = view.findViewById(R.id.set_time_value);
        setAzimuthTv = view.findViewById(R.id.set_azimuth_value);
        civilRiseTv = view.findViewById(R.id.rise_civil_value);
        civilSetTv = view.findViewById(R.id.set_civil_value);


        String time = sharedPreferences.getString("time", "1");

        setLocationValues();

        calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Warsaw"));
        AstroCalculator.Location location = new AstroCalculator.Location(Double.parseDouble(latitude), Double.parseDouble(longitude));

        info = new Info(calendar, location);

        setSunValues();

        timeHandler = new Handler();
        handler = new Handler(getMainLooper());

        timeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timeHandler.postDelayed(this, 1000);
                Date currentTime = calendar.getTime();
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss a");
                String formattedDate = df.format(Calendar.getInstance().getTime());

                info.updateTime();
                timeTv.setText(formattedDate);
            }
        }, 1);

        final Runnable r = new Runnable() {
            @Override
            public void run() {
                setLocationValues();

                info.updateLocation(new AstroCalculator.Location(Double.parseDouble(latitude), Double.parseDouble(longitude)));

                setSunValues();
            }
        };

        handler.postDelayed(r, (long) Long.parseLong(time) * 60000);


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timeHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);
    }

    public void setLocationValues() {
        latitude = sharedPreferences.getString("latitude", "");
        longitude = sharedPreferences.getString("longitude", "");

        latitudeTv.setText(latitude);
        longitudeTv.setText(longitude);
    }

    public void setSunValues() {
        riseTimeTv.setText(info.getSunRiseTime());
        riseAzimuthTv.setText(info.getSunRiseAzimuth());
        setTimeTv.setText(info.getSunSetTime());
        setAzimuthTv.setText(info.getSunSetAzimuth());
        civilRiseTv.setText(info.getCivilRise());
        civilSetTv.setText(info.getCivilSet());
    }
}