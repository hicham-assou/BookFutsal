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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bookfutsal.R;
import com.example.bookfutsal.databinding.ActivityMainBinding;
import com.example.bookfutsal.databinding.ActivitySportCenterDetailBinding;
import com.example.bookfutsal.models.SportCenter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

        // info dans la page detail
        binding.centerName.setText(center.getNameCenter());
        binding.infoAddress.setText(center.getAdress());
        binding.infoPhone.setText(center.getPhoneNumber());
        binding.infoHours.setText(showOpeningHours(center.getOpeningHours()));
        getInfoWheather(center.getLatitude(), center.getLongitude());


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

    private void getInfoWheather(double latitude, double longitude) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&appid=9211be69198a3f97d01c865ada5360e4";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // on recupere un JSON contenant toutes les info de la meteo de la latitude et longitude donné
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject main = jsonObject.getJSONObject("main"); // main car les info de la temperature se trouve la
                    int temperature = (int)(main.getDouble("temp")-273.15); // -273.15 pour la transformer en C°

                    int humidity = (int)(main.getDouble("humidity"));

                    String weatherValue = "température = " + temperature + "C°, Humidity = " + humidity + "%";
                    System.out.println("meteo ==> " + weatherValue);
                    binding.infoWeather.setText(weatherValue);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast("error wheather");
            }
        });
        queue.add(stringRequest);
    }

    private String showOpeningHours(HashMap<String, String> openingHours) {
        String messageToReturn = "";
        for (Map.Entry<String, String> entry : openingHours.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            messageToReturn += key + " : " + value + "\n";
            //System.out.println("Clé: " + key + ", Valeur: " + value);
        }
        return messageToReturn;
    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}