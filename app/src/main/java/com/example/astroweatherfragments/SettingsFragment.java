package com.example.astroweatherfragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    EditTextPreference longitude, latitude, time;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        longitude = findPreference("longitude");
        latitude = findPreference("latitude");
        time = findPreference("time");
    }

    @Override
    public void onResume() {
        setPreferenceScreen(null);
        addPreferencesFromResource(R.xml.root_preferences);
        super.onResume();
    }
}