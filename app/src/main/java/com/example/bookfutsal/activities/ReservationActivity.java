package com.example.bookfutsal.activities;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReservationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Reservations");

        // récupérer l'utilisateur connecté
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        getReservationsOfUser(new ReservationsCallback() {
            @Override
            public void onReservationsReceived(List<Reservation> reservations) {
                for (Reservation reservation : reservations) {
                    // a poursuivre
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
                User booker = documentSnapshot.toObject(Reservation.class).getBooker();
                if (booker.getEmail().equals(currentUser.getEmail())) {
                    list.add(new Reservation(hour, sportCenterName, date));
                }
            }
            System.out.println("list => " + list);
            callback.onReservationsReceived(list);
        }).addOnFailureListener(e -> {
            // Gérer l'erreur ici, si nécessaire
        });
    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
