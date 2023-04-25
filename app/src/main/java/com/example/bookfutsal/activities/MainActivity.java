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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends DrawerBaseActivity implements OnMapReadyCallback {

    private ActivityMainBinding binding;
    private GoogleMap map;
    private List<SportCenter> mSportCenters = new ArrayList<>();
    private AlertDialog.Builder markerDialogBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Book Futsal");

        // Créer les centres sportifs
        HashMap<String, String> openingHours = new HashMap<>();
        openingHours.put("Monday", "9:00 AM - 10:00 PM");
        openingHours.put("Tuesday", "9:00 AM - 10:00 PM");
        openingHours.put("Wednesday", "9:00 AM - 10:00 PM");
        openingHours.put("Thursday", "9:00 AM - 10:00 PM");
        openingHours.put("Friday", "9:00 AM - 10:00 PM");
        openingHours.put("Saturday", "10:00 AM - 8:00 PM");
        openingHours.put("Sunday", "close");

        mSportCenters.add(new SportCenter(1,"CITY FIVE", 50.8466, 4.3528, "rue du loisir, 24", "04 65 32 15 48", 10, openingHours, ""));
        mSportCenters.add(new SportCenter(2, "ARENA", 50.8951, 4.3414, "rue du bonheur, 24", "04 65 32 15 48", 10, openingHours, ""));
        mSportCenters.add(new SportCenter(3, "FIT FIVE", 50.8463, 4.3614, "rue du piers, 24", "04 65 32 15 48", 10, openingHours, ""));


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
                                .setNegativeButton("more details", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(MainActivity.this, SportCenterDetail.class);
                                        intent.putExtra("center", center);
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

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}