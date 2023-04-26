package com.example.bookfutsal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookfutsal.R;
import com.example.bookfutsal.models.Reservation;

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

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            sportCenterName = itemView.findViewById(R.id.item_center_name);
            date = itemView.findViewById(R.id.item_date);
            hour = itemView.findViewById(R.id.item_timeReservation);
        }
    }
}

