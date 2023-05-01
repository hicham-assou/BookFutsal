package com.example.bookfutsal.interfaces;

import com.example.bookfutsal.models.SportCenter;

import java.util.List;

public interface CentersFirestoreCallback {
    void onCallback(List<SportCenter> listSportCenters);
}
