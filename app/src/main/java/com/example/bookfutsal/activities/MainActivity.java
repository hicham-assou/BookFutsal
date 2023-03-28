package com.example.bookfutsal.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.bookfutsal.R;
import com.example.bookfutsal.models.SportCenter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private List<SportCenter> mSportCenters = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Créer des instances de SportCenter pour les centres sportifs
        mSportCenters.add(new SportCenter("Centre sportif 1", 50.8466, 4.3528));
        mSportCenters.add(new SportCenter("Centre sportif 2", 50.8951, 4.3414));
        mSportCenters.add(new SportCenter("Centre sportif 3", 50.8463, 4.3614));

        System.out.println("centre ajouter a la liste");

        // Obtenir la carte depuis la vue XML
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;


        // Ajouter un marqueur pour chaque centre sportif
        // https://www.youtube.com/watch?v=Xqyp77oMwO8
        for (SportCenter center : mSportCenters) {
            LatLng location = new LatLng(center.getLatitude(), center.getLongitude());
            map.addMarker(new MarkerOptions()
                    .position(location)
                    .title(center.getNameCenter()));

            // pour zoomer sur la partie qui nous interesse
            map.moveCamera(CameraUpdateFactory.newLatLng(location));
            map.moveCamera(CameraUpdateFactory.zoomTo(10f));
            map.animateCamera(CameraUpdateFactory.zoomTo(10f));

        }
    }
}