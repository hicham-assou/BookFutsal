package com.example.bookfutsal.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookfutsal.R;
import com.example.bookfutsal.models.Reservation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private List<Reservation> reservations;

    public ReservationAdapter(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reservation_item, parent, false);
        return new ReservationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation currentReservation = reservations.get(position);
        holder.sportCenterName.setText(currentReservation.getSportCenterName());
        holder.date.setText(currentReservation.getDate());
        holder.hour.setText(currentReservation.getHour());
        System.out.println("adapter sout => " + currentReservation.getImageCenter());
        holder.price.setText(currentReservation.getPrice() + " €");
        // Charger l'image à partir de l'URL avec Picasso
        Picasso.get().load(currentReservation.getImageCenter()).into(holder.imageCenter);

        holder.cancelReservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String reservationId = currentReservation.getId();

                // supprimer la reservation de la db
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference reservationRef = db.collection("reservations").document(reservationId);
                reservationRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // refresh la recycleView
                        int currentPosition = holder.getAdapterPosition(); // utilisation de la variable locale
                        reservations.remove(currentReservation);
                        notifyItemRemoved(currentPosition);
                        notifyItemRangeChanged(currentPosition, reservations.size());
                        Toast.makeText(holder.itemView.getContext(), "Reservation canceled.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
        notifyDataSetChanged();
    }

    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        private TextView sportCenterName;
        private TextView date;
        private TextView hour;
        private TextView price;
        private ImageView imageCenter;
        private Button cancelReservationButton;


        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            sportCenterName = itemView.findViewById(R.id.item_center_name);
            date = itemView.findViewById(R.id.item_date_value);
            hour = itemView.findViewById(R.id.item_timeReservation_value);
            price = itemView.findViewById(R.id.item_price_value);
            imageCenter = itemView.findViewById(R.id.item_center_image);
            cancelReservationButton = itemView.findViewById(R.id.button_cancelReservation);



        }
    }
}

