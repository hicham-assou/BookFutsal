package com.example.bookfutsal.activities;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends DrawerBaseActivity implements OnMapReadyCallback {

    private ActivityMainBinding binding;
    private GoogleMap map;
    private long backPressedTimestamp = 0;


    private AlertDialog.Builder markerDialogBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Book Futsal");


        //pour les notif
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

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
                String image = documentSnapshot.getString("image");
                String phoneNumber = documentSnapshot.getString("phoneNumber");
                long priceHourLong = documentSnapshot.getLong("priceHour");
                int priceHour = (int) priceHourLong;

                // commentaires
                List<String> comments = (List<String>) documentSnapshot.get("comments");

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

                               // popup personnaliser
                                View popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);

                                TextView nameTextView = popupView.findViewById(R.id.popup_centerNameTextView);
                                TextView addressTextView = popupView.findViewById(R.id.popup_centerAddressTextView);
                                TextView priceTextView = popupView.findViewById(R.id.popup_price);
                                ImageView centerImageView = popupView.findViewById(R.id.popup_centerImage);

                                // Charger l'image à partir de l'URL avec Picasso
                                Picasso.get().load(center.getImage()).into(centerImageView);

                                // Définir les txtView
                                nameTextView.setText(center.getNameCenter());
                                addressTextView.setText(center.getAdress());
                                priceTextView.setText(center.getPriceHour() + " €/H");

                                // Afficher popup
                                markerDialogBuilder.setView(popupView)
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

    @Override
    public void onBackPressed() {
        // il faut appuyer 2fois pour quitter l'application
        long currentTime = System.currentTimeMillis();

        if (currentTime - backPressedTimestamp > 2000) {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            backPressedTimestamp = currentTime;
        } else {
            // Fermer completement l'application
            finishAffinity();
        }
    }


    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}