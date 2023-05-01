package com.example.bookfutsal.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
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
import com.example.bookfutsal.interfaces.OnUserFetchListener;
import com.example.bookfutsal.interfaces.ReservationsCallback;
import com.example.bookfutsal.models.Reservation;
import com.example.bookfutsal.models.SportCenter;
import com.example.bookfutsal.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SportCenterDetail extends DrawerBaseActivity {
    ActivitySportCenterDetailBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    CalendarView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySportCenterDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //recuperer l'utilisateur connecté
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //recup du centre appuyé
        SportCenter center = (SportCenter) getIntent().getSerializableExtra("center");

        //image du centre
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(center.getImage());
        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                binding.centerImage.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                showToast("Error while loading the image");
            }
        });

        //donner le nom du centre en titre de l'activity
        allocateActivityTitle(center.getNameCenter());

        // info dans la page detail
        binding.centerName.setText(center.getNameCenter());
        binding.infoAddress.setText(center.getAdress());
        binding.infoPhone.setText(center.getPhoneNumber());
        binding.infoHours.setText(showOpeningHours(center.getOpeningHours()));
        //getInfoWheather(center.getLatitude(), center.getLongitude());


        //calendrier
        // Changer le format de la date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(new Date()); // 05/04/2023
        //afficher celui d'aujourd'hui par defaut
        reservation_grid(center, formattedDate);

        calendar = binding.calendar1View;
        calendar.setMinDate(System.currentTimeMillis()); // Empecher la sélection de jours antérieurs à la date actuelle
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                Date date = calendar.getTime(); // Wed Apr 05 15:28:37 GMT 2023
                // Obtenir le jour de la semaine
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                String dayOfWeek = sdf.format(date); // Wednesday

                // Changer le format de la date
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = sdf2.format(date); // 05/04/2023

                reservation_grid(center, formattedDate);

            }
        });

    }

    private void reservation_grid(SportCenter center, String date) {

        int hours = 7;
        final String suffix ="H";
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

                if (hours == 24)
                    hours = 0;

                if (hours == 23)
                    textView.setText(hours + suffix + " - 0" + suffix);
                else
                    textView.setText(hours + suffix + " - " + (hours+1) + suffix);




                textView.setId(hours);
                textView.setGravity(Gravity.CENTER); // Centrer le texte dans le TextView
                binding.gridLayout.addView(textView);

                //colorier en rouge les place deja prise
                //recuperer les reservation qui existe deja dans la db
                getReservations(center, date, new ReservationsCallback() {
                    @Override
                    public void onReservationsReceived(List<Reservation> reservations) {
                        for (Reservation reservation : reservations) {
                            if (reservation.getHour().equals(textView.getText())) {
                                textView.setBackgroundColor(Color.RED);
                            }
                        }
                    }
                });

                // on click
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //verifier si la place est deja reservé
                        int color = ((ColorDrawable) textView.getBackground()).getColor();
                        if (color == Color.RED) {
                            showToast("already reserved ... ");
                        }
                        else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SportCenterDetail.this);
                            builder.setMessage("book on " + date+ " at " + textView.getText() +" ?")
                                    .setTitle("Reservation")
                                    .setPositiveButton("Reserve Now !", new DialogInterface.OnClickListener() {
                                        // lorsqu'il veut reserver
                                        public void onClick(DialogInterface dialog, int id) {
                                            if (currentUser == null) {
                                                showToast("must be connected first ");
                                            } else {
                                                String hour = (String) textView.getText();
                                                getCurrentUser(new OnUserFetchListener() {
                                                    @Override
                                                    public void onUserFetch(User user) {
                                                        Reservation reservation = new Reservation(hour, center.getNameCenter(), user, date);
                                                        // Ajouter la réservation à Firestore
                                                        addToFirestore(reservation);
                                                    }
                                                });
                                                textView.setBackgroundColor(Color.RED);
                                            }
                                        }
                                    })
                                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // lorsque l'utilisateur appuie sur annuler
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                    }
                });
            }
        }
    }

    private void getReservations(SportCenter center, String today, ReservationsCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reservationsRef = db.collection("reservations");

        List<Reservation> list = new ArrayList<>();

        reservationsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                String sportCenterName = documentSnapshot.getString("sportCenterName");
                String date = documentSnapshot.getString("date");
                String hour = documentSnapshot.getString("hour");

                System.out.println("date = " + date);
                System.out.println("today " + today);
                if (date.equals(today) && center.getNameCenter().equals(sportCenterName)){
                    // ajouter a la liste si meme jour
                    list.add(new Reservation(hour, sportCenterName, date));
                }
            }
            callback.onReservationsReceived(list);
        }).addOnFailureListener(e -> {
            // Gérer l'erreur ici, si nécessaire
        });
    }

    private void addToFirestore(Reservation reservation) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Créez une référence à une nouvelle collection "reservations"
        CollectionReference reservationsRef = db.collection("reservations");

        // Créez un nouveau document dans la collection "reservations"
        reservationsRef.add(reservation)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Réservation ajoutée avec ID : " + documentReference.getId());
                        // La réservation a été ajoutée avec succès à Firestore
                        // Faites ce que vous avez besoin de faire après l'ajout
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Erreur lors de l'ajout de la réservation", e);
                        // La réservation n'a pas été ajoutée à Firestore
                        // Gérez l'erreur ici
                    }
                });
    }

    //avoir l'utilisateur actuel
    public void getCurrentUser(OnUserFetchListener listener) {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Créer une instance de la classe User et initialiser ses attributs
                    User user = new User();
                    user.setUsername(documentSnapshot.getString("username"));
                    user.setEmail(documentSnapshot.getString("email"));
                    user.setPassword(documentSnapshot.getString("password"));

                    // Appeler la méthode onUserFetch() de l'interface de rappel avec l'objet User
                    listener.onUserFetch(user);
                }
            }
        });
    }


    // api meteo
    /*private void getInfoWheather(double latitude, double longitude) {
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
    }*/

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