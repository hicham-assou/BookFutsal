package com.example.bookfutsal.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.bookfutsal.R;
import com.example.bookfutsal.databinding.ActivityMainBinding;
import com.example.bookfutsal.databinding.ActivityReservationBinding;

public class ReservationActivity extends DrawerBaseActivity {

    private ActivityReservationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReservationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("mes reservations");
    }
}