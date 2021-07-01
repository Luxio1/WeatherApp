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


public class MoonFragment extends Fragment {
    private Info info;
    private Calendar calendar;
    private TextView timeTv, latitudeTv, longitudeTv, newMoonTv, fullMoonTv, riseTimeTv, setTimeTv, synodicDayTv, phaseTv;

    Handler handler, timeHandler;

    SharedPreferences sharedPreferences;

    String latitude, longitude;


    public static MoonFragment getInstance() {
        return new MoonFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moon, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        timeTv = view.findViewById(R.id.time_value);
        latitudeTv = view.findViewById(R.id.latitude_value);
        longitudeTv = view.findViewById(R.id.longitude_value);
        newMoonTv = view.findViewById(R.id.new_moon_value);
        fullMoonTv = view.findViewById(R.id.full_moon_value);
        riseTimeTv = view.findViewById(R.id.rise_time_value);
        setTimeTv = view.findViewById(R.id.set_time_value);
        synodicDayTv = view.findViewById(R.id.synodic_day_value);
        phaseTv = view.findViewById(R.id.phase_value);

        String time = sharedPreferences.getString("time", "1");

        setLocationValues();

        calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Warsaw"));
        AstroCalculator.Location location = new AstroCalculator.Location(Double.parseDouble(latitude), Double.parseDouble(longitude));

        info = new Info(calendar, location);

        setMoonValues();

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
                setMoonValues();
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

    public void setMoonValues() {
        newMoonTv.setText(info.getNewMoon());
        fullMoonTv.setText(info.getFullMoon());
        riseTimeTv.setText(info.getMoonRiseTime());
        setTimeTv.setText(info.getMoonSetTime());
        synodicDayTv.setText(info.getSynodicDay());
        phaseTv.setText(info.getMoonPhase());
    }
}
