package com.example.bookfutsal.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.bookfutsal.R;
import com.example.bookfutsal.databinding.ActivityMainBinding;
import com.example.bookfutsal.databinding.ActivitySportCenterDetailBinding;
import com.example.bookfutsal.models.SportCenter;

public class SportCenterDetail extends DrawerBaseActivity {
    ActivitySportCenterDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySportCenterDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //recup du centre appuyé
        SportCenter center = (SportCenter) getIntent().getSerializableExtra("center");

        allocateActivityTitle(center.getNameCenter());//donner le nom du centre en titre de l'activity


    }
}