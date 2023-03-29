package com.example.bookfutsal.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.bookfutsal.R;
import com.example.bookfutsal.models.SportCenter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private List<SportCenter> mSportCenters = new ArrayList<>();
    private AlertDialog.Builder markerDialogBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Créer des instances de SportCenter pour les centres sportifs
        mSportCenters.add(new SportCenter(1,"Centre sportif 1", 50.8466, 4.3528, "rue du loisir, 24"));
        mSportCenters.add(new SportCenter(2, "Centre sportif 2", 50.8951, 4.3414, "rue du bonheur, 24"));
        mSportCenters.add(new SportCenter(3, "Centre sportif 3", 50.8463, 4.3614, "rue du piers, 24"));

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
        markerDialogBuilder = new AlertDialog.Builder(this);

        // Ajouter un marqueur pour chaque centre sportif
        for (SportCenter center : mSportCenters) {
            LatLng location = new LatLng(center.getLatitude(), center.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(location)
                    .title(center.getNameCenter())
                    .snippet(String.valueOf(center.getId())); // Ajouter l'ID du centre sportif comme snippet
            Marker marker = map.addMarker(markerOptions);
            marker.setTag(center); // Ajouter le centre sportif comme tag du marqueur

            // Créer un popup pour chaque marqueur
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    SportCenter center = (SportCenter) marker.getTag(); // Récupérer le centre sportif à partir du tag du marqueur
                    if (center != null) {
                        markerDialogBuilder.setTitle(center.getNameCenter())
                                .setMessage(center.getAdress())
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("plus de detail", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(MainActivity.this, SportCenterDetail.class);
                                        startActivity(intent);
                                    }
                                })
                                .show();
                    }
                    return true;
                }
            });

            // pour zoomer sur la partie qui nous interesse
            map.moveCamera(CameraUpdateFactory.newLatLng(location));
            map.moveCamera(CameraUpdateFactory.zoomTo(10f));
            map.animateCamera(CameraUpdateFactory.zoomTo(10f));

        }
    }


}