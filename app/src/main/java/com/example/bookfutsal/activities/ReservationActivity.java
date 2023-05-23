package com.example.bookfutsal.activities;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.bookfutsal.R;
import com.example.bookfutsal.adapter.ReservationAdapter;
import com.example.bookfutsal.databinding.ActivityReservationBinding;
import com.example.bookfutsal.interfaces.ReservationsCallback;
import com.example.bookfutsal.models.Reservation;
import com.example.bookfutsal.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReservationActivity extends DrawerBaseActivity {

    private ActivityReservationBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    RecyclerView rvReservations;
    ReservationAdapter adapter;
    private int currentViewIndex = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReservationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Reservations");

        // récupérer l'utilisateur connecté
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // configurer la RecyclerView et l'adapter
        rvReservations = findViewById(R.id.reservations_recycler_view);
        rvReservations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReservationAdapter(new ArrayList<>());
        rvReservations.setAdapter(adapter);

        getReservationsOfUser(new ReservationsCallback() {
            @Override
            public void onReservationsReceived(List<Reservation> reservations) {
                if (reservations.size() == 0)
                    showToast("No Reservations");
                else{
                    // ajouter les réservations à la liste de l'adapter
                    adapter.setReservations(reservations);
                }

            }
        });
    }


    private void getReservationsOfUser(ReservationsCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reservationsRef = db.collection("reservations");

        List<Reservation> list = new ArrayList<>();

        reservationsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                String sportCenterName = documentSnapshot.getString("sportCenterName");
                String date = documentSnapshot.getString("date");
                String hour = documentSnapshot.getString("hour");
                String image = documentSnapshot.getString("imageCenter");
                String id = documentSnapshot.getId();
                long priceHourLong = documentSnapshot.getLong("price");
                int price = (int) priceHourLong;


                User booker = documentSnapshot.toObject(Reservation.class).getBooker();
                if (booker.getEmail().equals(currentUser.getEmail())) {
                    list.add(new Reservation(id, hour, sportCenterName, date, image, price));
                }
            }

            // Trier la liste par date
            Collections.sort(list, (reservation1, reservation2) -> {
                // Convertir les dates en format compatible avec la comparaison
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                try {
                    Date date1 = sdf.parse(reservation1.getDate());
                    Date date2 = sdf.parse(reservation2.getDate());
                    // Comparer les dates
                    if (date1 != null && date2 != null) {
                        return date1.compareTo(date2);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            });
            callback.onReservationsReceived(list);
        }).addOnFailureListener(e -> {
            showToast("Error ");
        });
    }

    public static void cancelReservation(){

    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
