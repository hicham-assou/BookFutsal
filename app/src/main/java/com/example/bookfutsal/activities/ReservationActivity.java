package com.example.bookfutsal.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

public class ReservationActivity extends DrawerBaseActivity {

    private static final int ROW_COUNT = 24;
    private static final int COLUMN_COUNT = 7;

    private ActivityReservationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReservationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Reservations");

        int hours = 0;

        for(int i=0; i<binding.gridLayout.getRowCount(); i++){
            for(int j=0; j<binding.gridLayout.getColumnCount(); j++){
                Button button = new Button(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.columnSpec = GridLayout.spec(j);
                params.rowSpec = GridLayout.spec(i);
                button.setLayoutParams(params);
                hours ++;
                button.setText(hours+"h - "+ (hours+1) + "h");
                binding.gridLayout.addView(button);
                int finalI = i;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showToast("button "+ finalI);
                    }
                });
            }
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int cellSize = screenWidth / 7; // 7 étant le nombre de colonnes de la grille
        int numColumns = screenWidth / cellSize;
        binding.gridLayout.setColumnCount(numColumns);


    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}