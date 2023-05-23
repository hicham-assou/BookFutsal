package com.example.bookfutsal.adapter;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookfutsal.R;
import com.example.bookfutsal.activities.ReservationActivity;
import com.example.bookfutsal.models.Reservation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        holder.price.setText(currentReservation.getPrice() + " €");
        // Charger l'image à partir de l'URL avec Picasso
        Picasso.get().load(currentReservation.getImageCenter()).into(holder.imageCenter);

        /// Obtenir la date et l'heure actuelles
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Convertir la date de la réservation en format compatible avec la comparaison
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date reservationDate = sdf.parse(currentReservation.getDate());
            if (reservationDate != null) {
                // Comparer la date actuelle avec la date de la réservation
                if (currentDate.before(reservationDate)) {
                    holder.cancelReservationButton.setVisibility(View.VISIBLE);
                } else {
                    holder.cancelReservationButton.setVisibility(View.GONE);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // boutton annulation
        holder.cancelReservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to cancel this reservation?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Annulation de la réservation
                        String reservationId = currentReservation.getId();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference reservationRef = db.collection("reservations").document(reservationId);
                        reservationRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                int currentPosition = holder.getAdapterPosition();
                                reservations.remove(currentReservation);
                                notifyItemRemoved(currentPosition);
                                notifyItemRangeChanged(currentPosition, reservations.size());
                                Toast.makeText(holder.itemView.getContext(), "Reservation canceled.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", null);
                AlertDialog dialog = builder.create();
                dialog.show();
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

