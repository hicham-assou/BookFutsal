package com.example.bookfutsal.activities;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.bookfutsal.R;
import com.example.bookfutsal.databinding.ActivityMainBinding;
import com.example.bookfutsal.interfaces.CentersFirestoreCallback;
import com.example.bookfutsal.models.SportCenter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends DrawerBaseActivity implements OnMapReadyCallback {

    private ActivityMainBinding binding;
    private GoogleMap map;

    private AlertDialog.Builder markerDialogBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Book Futsal");

        // Obtenir la carte depuis la vue XML
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

    }

    public void getSportCentersFromFirestore(CentersFirestoreCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference centersRef = db.collection("centers");

        List<SportCenter> listSportCenters = new ArrayList<>();

        centersRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                String nameCenter = documentSnapshot.getString("nameCenter");
                String adress = documentSnapshot.getString("adress");
                String comments = documentSnapshot.getString("comments");
                String image = documentSnapshot.getString("image");
                String phoneNumber = documentSnapshot.getString("phoneNumber");
                long priceHourLong = documentSnapshot.getLong("priceHour");
                int priceHour = (int) priceHourLong;

                Map<String, Object> openingHoursMap = documentSnapshot.getData();
                // transformer en hashmap
                HashMap<String, String> openingHours = (HashMap<String, String>) openingHoursMap.get("openingHours");



                GeoPoint location = documentSnapshot.getGeoPoint("location");
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                listSportCenters.add(new SportCenter(priceHour, nameCenter, adress, image, phoneNumber, comments, latitude, longitude, openingHours));
            }

            callback.onCallback(listSportCenters);
        }).addOnFailureListener(e -> {
            showToast("Une erreur est survenue lors de la récupération des réservations.");
        });
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        markerDialogBuilder = new AlertDialog.Builder(this);

        //recuperer les centres sportifs a partir de firestore
        getSportCentersFromFirestore(new CentersFirestoreCallback() {
            @Override
            public void onCallback(List<SportCenter> sportCenterList) {
                // Ajouter un marqueur pour chaque centre sportif
                for (SportCenter center : sportCenterList) {
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
        });

    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}