package com.threess.summership.treasurehunt.fragment.home_menu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.threess.summership.treasurehunt.MainActivity;
import com.threess.summership.treasurehunt.R;
import com.threess.summership.treasurehunt.adapter.TreasureAdapter;
import com.threess.summership.treasurehunt.logic.ApiController;
import com.threess.summership.treasurehunt.logic.SavedData;
import com.threess.summership.treasurehunt.model.Treasure;
import com.threess.summership.treasurehunt.navigation.FragmentNavigation;
import com.threess.summership.treasurehunt.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteTreasureFragment extends Fragment {
    
    private RecyclerView recycle;
    private TreasureAdapter adapter;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Handler handler;
    private Runnable runnable;

    public FavoriteTreasureFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_treasure, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recycle = view.findViewById(R.id.recycler_view_contacts);
        adapter = new TreasureAdapter(this.getContext());
        recycle.setAdapter(adapter);
        recycle.setLayoutManager(new LinearLayoutManager(this.getContext()));
        getAllActiveTreasures();
        setupHandler();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    private LocationRequest mLocationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(TimeUnit.SECONDS.toMillis(1));


    private LocationCallback mLocationCallback =  new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            List<Location> locations = locationResult.getLocations();
            if (!locations.isEmpty()) {
                Location location = locations.get(0);
                if(adapter.getSelectedTreasure() != null){
                    LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());
                    LatLng treasurePosition = new LatLng( adapter.getSelectedTreasure().getLocation_lat(),
                            adapter.getSelectedTreasure().getLocation_lon());
                        if(Util.distanceBetweenLatLngInMeter(currentPosition,treasurePosition) <= 10){
                            startActivity(new Intent(getContext(), MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                            claimTreasure();
                        }
                }
            }
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
        }
    };

    private void setupHandler(){
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if(getContext()!=null){
                    Treasure treasure = adapter.getSelectedTreasure();
                    adapter.clearSelectedTreasure();
                    FragmentNavigation.getInstance(getContext()).showClaimTreasureFragment(
                            new SavedData(getContext()).readStringData("UserName"),
                            treasure.getUsername());
                }
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    private void getAllActiveTreasures(){
        ApiController.getInstance().getAllTreasures(new Callback<ArrayList<Treasure>>() {
            @Override
            public void onResponse(Call<ArrayList<Treasure>> call, Response<ArrayList<Treasure>> response) {
                adapter.refreshTreasure(response.body());
            }
            @Override
            public void onFailure(Call<ArrayList<Treasure>> call, Throwable t) {

            }
        });
    }

    private void claimTreasure(){
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 500);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

}

