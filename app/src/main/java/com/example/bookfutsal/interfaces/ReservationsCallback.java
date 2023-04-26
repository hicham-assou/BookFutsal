package com.example.bookfutsal.interfaces;

import com.example.bookfutsal.models.Reservation;

import java.util.List;

public interface ReservationsCallback {
    void onReservationsReceived(List<Reservation> reservations);
}
