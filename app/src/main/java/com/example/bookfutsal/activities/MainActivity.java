package com.example.bookfutsal.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.bookfutsal.R;
import com.example.bookfutsal.databinding.ActivityMainBinding;
import com.example.bookfutsal.models.SportCenter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    //private ActivityMainBinding binding;
    private GoogleMap map;
    private List<SportCenter> mSportCenters = new ArrayList<>();
    private AlertDialog.Builder markerDialogBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);

        //navbar
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

        // Créer les centres sportifs
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

    // navigetion entre item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        System.out.println("clique sur un item !!!!!!!!!!!!!");


        if (id == R.id.menuMyReservations) {
            System.out.println("clique sur reservation !!!!!!!!!!!!!");

            Intent intent = new Intent(this, SportCenterDetail.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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