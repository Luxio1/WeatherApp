package com.example.astroweatherfragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager;
    FragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#031430")));
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        //f (!getResources().getBoolean(R.bool.isTablet)) {


            tabLayout = findViewById(R.id.tabLayout);
            viewPager = findViewById(R.id.viewPager);

            FragmentManager fm = getSupportFragmentManager();
            adapter = new FragmentAdapter(fm, getLifecycle());
            viewPager.setAdapter(adapter);

            tabLayout.addTab(tabLayout.newTab().setText("Sun"));
            tabLayout.addTab(tabLayout.newTab().setText("Moon"));
            tabLayout.addTab(tabLayout.newTab().setText("Weather"));
            tabLayout.addTab(tabLayout.newTab().setText("Extended"));
            tabLayout.addTab(tabLayout.newTab().setText("Future"));
            tabLayout.addTab(tabLayout.newTab().setText("Favourites"));
            tabLayout.addTab(tabLayout.newTab().setText("Settings"));


            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    tabLayout.selectTab(tabLayout.getTabAt(position));
                }
            });
        }
    //}


}