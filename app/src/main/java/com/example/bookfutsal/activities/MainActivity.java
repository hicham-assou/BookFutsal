package com.example.bookfutsal.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.bookfutsal.R;
import com.example.bookfutsal.models.SportCenter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<SportCenter> mSportCenters = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Créer des instances de SportCenter pour les centres sportifs
        mSportCenters.add(new SportCenter("Centre sportif 1", 48.8534, 2.3488));
        mSportCenters.add(new SportCenter("Centre sportif 2", 48.8606, 2.3376));
        mSportCenters.add(new SportCenter("Centre sportif 3", 48.8759, 2.3543));

        // Obtenir la carte depuis la vue XML
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Ajouter un marqueur pour chaque centre sportif
        for (SportCenter center : mSportCenters) {
            LatLng location = new LatLng(center.getLatitude(), center.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(center.getNameCenter()));
        }
    }
}