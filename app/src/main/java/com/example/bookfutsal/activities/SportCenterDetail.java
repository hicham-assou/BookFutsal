package com.example.bookfutsal.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

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

        //ajouter les textView
        int hours = 7;
        for(int i=0; i<binding.gridLayout.getRowCount(); i++){
            for(int j=0; j<binding.gridLayout.getColumnCount(); j++){
                hours ++;
                TextView textView = new TextView(this);
                textView.setBackgroundColor(Color.GREEN);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.columnSpec = GridLayout.spec(j, 1f);
                params.rowSpec = GridLayout.spec(i, 1f);
                params.setMargins(8, 8, 8, 8); // Ajouter des marges
                textView.setLayoutParams(params);
                if (hours >= 24){
                    hours = 0;
                    textView.setText(hours+"0h - 0"+ (hours+1) + "h");
                }else{
                    if (hours == 23)
                        textView.setText(hours+"h - 00h");
                    else
                        textView.setText(hours+"h - "+ (hours+1) + "h");
                }

                textView.setId(hours);
                textView.setGravity(Gravity.CENTER); // Centrer le texte dans le TextView
                binding.gridLayout.addView(textView);
                int id = hours;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showToast("textView "+ id);
                        AlertDialog.Builder builder = new AlertDialog.Builder(SportCenterDetail.this);
                        builder.setMessage("book on 12/10 from " + id + " to "+ (id + 1) +" ?")
                                .setTitle("Reservation")
                                .setPositiveButton("Reserve Now !", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        textView.setBackgroundColor(Color.RED);
                                    }
                                })
                                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Action à exécuter lorsque l'utilisateur appuie sur "Annuler"
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
        }
    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}