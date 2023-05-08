package com.example.bookfutsal.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookfutsal.R;
import com.example.bookfutsal.adapter.ReservationAdapter;
import com.example.bookfutsal.databinding.ActivityMainBinding;
import com.example.bookfutsal.databinding.ActivityReservationBinding;
import com.example.bookfutsal.interfaces.ReservationsCallback;
import com.example.bookfutsal.models.Reservation;
import com.example.bookfutsal.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReservationActivity extends DrawerBaseActivity {

    private ActivityReservationBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    RecyclerView rvReservations;
    ReservationAdapter adapter;

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
                for (Reservation r: reservations){
                    System.out.println("date => " + r.getDate());
                    System.out.println("prix => " + r.getPrice());
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
            callback.onReservationsReceived(list);
        }).addOnFailureListener(e -> {
            showToast("Une erreur est survenue lors de la récupération des réservations.");
        });
    }

    public static void cancelReservation(){

    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
