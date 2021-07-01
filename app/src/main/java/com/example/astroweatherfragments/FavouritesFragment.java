package com.example.astroweatherfragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;


public class FavouritesFragment extends Fragment {

    Button addBtn, getBtn, deleteBtn;
    EditText etCity;
    HashSet<String> favouriteCities;
    SharedPreferences sharedPreferences;
    ListView citiesListView;
    Handler handler;

    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final  String apiKey = "e6af170b98f654ed6790f09ce43bdf7f";

    public static FavouritesFragment getInstance(){ return new FavouritesFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        addBtn = view.findViewById(R.id.btn_add);
        deleteBtn = view.findViewById(R.id.btn_delete);
        getBtn = view.findViewById(R.id.btn_get);

        citiesListView = view.findViewById(R.id.citiesList);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        favouriteCities = readCitiesFromPreferencesToSet();
        refreshListView(view);

        etCity = view.findViewById(R.id.etCity);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        String cityToAdd = etCity.getText().toString().trim().toLowerCase();
                        String pageUrl = url + "?q=" + cityToAdd + "&appid=" + apiKey;
                        URL responseUrl;
                        HttpURLConnection conn = null;
                        int responseCode = 0;


                        try {
                            responseUrl = new URL(pageUrl);
                            conn = (HttpURLConnection) responseUrl.openConnection();
                            conn.setRequestMethod("GET");
                            conn.connect();

                            responseCode = conn.getResponseCode();

                            int finalResponseCode = responseCode;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(finalResponseCode != 200) {
                                        Toast.makeText(view.getContext(), "Wrong city name!", Toast.LENGTH_LONG).show();
                                    } else {
                                        String cityToAddFirstLetterCapital = cityToAdd.substring(0, 1).toUpperCase() + cityToAdd.substring(1);
                                        favouriteCities.add(cityToAddFirstLetterCapital);
                                        saveCitiesSetToPreferences();
                                        refreshListView(view);
                                    }
                                }
                            });
                        } catch (IOException e) {
                            System.out.println("exception");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(view.getContext(), "Internet is not connected. Cannot add city.", Toast.LENGTH_SHORT).show();
                                }
                            });

                            e.printStackTrace();
                        }
                    }
                };
                thread.start();

            }
        });

        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        String cityToAdd = etCity.getText().toString().trim().toLowerCase();
                        String pageUrl = url + "?q=" + cityToAdd + "&appid=" + apiKey;
                        URL responseUrl;
                        HttpURLConnection conn = null;
                        int responseCode = 0;


                        try {
                            responseUrl = new URL(pageUrl);
                            conn = (HttpURLConnection) responseUrl.openConnection();
                            conn.setRequestMethod("GET");
                            conn.connect();

                            responseCode = conn.getResponseCode();

                            int finalResponseCode = responseCode;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(finalResponseCode != 200) {
                                        Toast.makeText(view.getContext(), "Wrong city name!", Toast.LENGTH_LONG).show();
                                    } else {
                                        SharedPreferences.Editor edit = sharedPreferences.edit();
                                        String city = etCity.getText().toString().trim();
                                        edit.putString("city", city);
                                        edit.commit();
                                    }
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();


            }
        });


        citiesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor edit = sharedPreferences.edit();
                String city = citiesListView.getItemAtPosition(position).toString();
                edit.putString("city", city);
                edit.commit();

                Toast.makeText(view.getContext(), "Loaded "+ city + " data", Toast.LENGTH_LONG).show();

                return false;
            }

        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int count = citiesListView.getCount();

                if (citiesListView.getCheckedItemPositions() != null) {
                    SparseBooleanArray positionChecker = citiesListView.getCheckedItemPositions();

                    for (int item = count - 1; item >= 0; item--) {
                        if (positionChecker.get(item)) {
                            favouriteCities.remove(citiesListView.getItemAtPosition(item).toString());
                        }
                    }

                    positionChecker.clear();
                    saveCitiesSetToPreferences();
                    refreshListView(view);

                    Toast.makeText(view.getContext(), "Deleted selected cities", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    public void saveCitiesSetToPreferences(){
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putStringSet("citiesList", favouriteCities);
        edit.commit();
    }

    public HashSet<String> readCitiesFromPreferencesToSet() {
        HashSet<String> set = (HashSet<String>)sharedPreferences.getStringSet("citiesList", new HashSet<String>());
        return new HashSet<String>(set);
    }

    public void refreshListView(View view) {
        ArrayAdapter<String> citiesAdapter = new ArrayAdapter<String>(
                view.getContext(), android.R.layout.simple_list_item_multiple_choice, new ArrayList<String>(favouriteCities));

        citiesListView.setAdapter(citiesAdapter);
    }



}