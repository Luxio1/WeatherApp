package com.example.astroweatherfragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends FragmentStateAdapter {

    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 1:
                return new MoonFragment();
            case 2:
                return new BasicWeatherFragment();
            case 3:
                return new ExtendedWeatherFragment();
            case 4:
                return new FutureWeatherFragment();
            case 5:
                return new FavouritesFragment();
            case 6:
                return new SettingsFragment();
        }

            return new SunFragment();

    }

    @Override
    public int getItemCount() {
        return 7;
    }
}
