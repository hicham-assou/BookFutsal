package com.example.bookfutsal.interfaces;

import com.example.bookfutsal.models.Reservation;

import java.util.List;

public interface ReservationsCallback {
    // sert a faire attendre le code jusqua la recuperation complete des donnes de la base de donnes
    void onReservationsReceived(List<Reservation> reservations);
}
