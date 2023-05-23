package com.example.bookfutsal.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.bookfutsal.adapter.CommentsAdapter;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SportCenterDetail extends DrawerBaseActivity {
    private ActivitySportCenterDetailBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private User userConnected;
    private CalendarView calendar;
    private SportCenter center;
    private int daySelectedInCalendar = 0;
    private AlertDialog.Builder markerDialogBuilder;
    private String channelId = "bookfitsal_channel";
    private static int notificationIdCounter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySportCenterDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        markerDialogBuilder = new AlertDialog.Builder(this);

        //recuperer l'utilisateur connecté
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //recup du centre appuyé
        center = (SportCenter) getIntent().getSerializableExtra("center");

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
        reservation_grid(formattedDate);

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

                //obtenir la meteo pour le jour appuyé
                daySelectedInCalendar = getDifferenceDays(formattedDate) * -1; // *-1 pour obtenir la date positif
                getInfoWheather(center.getLatitude(), center.getLongitude());

                //grid
                reservation_grid(formattedDate);

            }
        });

        //commentaire
        loadComment();

        createNotificationChannel(); // Création du canal de notification

    }

    // Méthode pour générer un identifiant unique pour la notification
    private int generateUniqueNotificationId() {
        notificationIdCounter++;
        return notificationIdCounter;
    }

    private void showNotification(String title, String message) {
        int uniqueId = generateUniqueNotificationId();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(uniqueId, builder.build());
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reservation";
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // fait grace a chatGPT
    private int getDifferenceDays(String daySelected) {
        // Convertir les chaînes de date en LocalDate
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        }
        LocalDate today = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            today = LocalDate.now();
        }
        LocalDate dateSelected = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateSelected = LocalDate.parse(daySelected, formatter);
        }

        // Calculer la différence en nombre de jours
        long differenceInDays = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            differenceInDays = ChronoUnit.DAYS.between(dateSelected, today);
        }
        int days = (int) differenceInDays;

        return days;

        /*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today = LocalDate.now();
        LocalDate dateSelected = LocalDate.parse(daySelected, formatter);

        // Calculer la différence en nombre de jours
        long differenceInDays = ChronoUnit.DAYS.between(dateSelected, today);
        int days = (int) differenceInDays;

        return days;*/
    }

    // avvir les 3 premiers commentaires
    private void loadComment() {
        List<String> comments = center.getComments();

        if (comments.size() >= 1) {
            showComment(binding.comments1, comments.get(comments.size()-1));
        }

        if (comments.size() >= 2) {
            showComment(binding.comments2, comments.get(comments.size()-2));
        }

        if (comments.size() >= 3) {
            showComment(binding.comments3, comments.get(comments.size()-3));
        }
        if (comments.size() >= 4) {
            binding.textViewMoreComments.setVisibility(View.VISIBLE );
        }
    }

    private void showComment(TextView commentTextView, String comment) {
        if (comment != null){
            commentTextView.setText(comment);
            commentTextView.setVisibility(View.VISIBLE);

            // mettre en evidence ses commentaire
            getCurrentUser(new OnUserFetchListener() {
                @Override
                public void onUserFetch(User user) {

                    if (comment.startsWith(user.getUsername()))
                        commentTextView.setTextColor(Color.BLUE);
                }
            });

        }
    }

    // afficher tous les commentaires
    public void loadAllComments(View v){
        showToast("more comments");
        // popup des commentaire
        View popupComment = getLayoutInflater().inflate(R.layout.popup_comments, null);
        RecyclerView commentRecyclerView = popupComment.findViewById(R.id.comments_list_view);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //adapter
        CommentsAdapter commentAdapter = new CommentsAdapter(center.getComments(), userConnected);
        commentRecyclerView.setAdapter(commentAdapter);

        markerDialogBuilder.setView(popupComment).show();
    }

    public void postComment(View v){
        if (currentUser == null) {
            showToast("must be connected first ");
        } else {
            // recuperer l'utilisateur qui a posté le commentaire
            getCurrentUser(new OnUserFetchListener() {
                @Override
                public void onUserFetch(User user) {
                    //ajouter a la db
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    // Récupérer l'enregistrement Firestore
                    DocumentReference documentReference = db.collection("centers").document(center.getNameCenter().toLowerCase());

                    // Créer une Map pour stocker les mises à jour à apporter
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("comments", FieldValue.arrayUnion(user.getUsername() + " : " + getTime()+ " : " + binding.commentEdittext.getText()));

                    // Mettre à jour l'enregistrement Firestore avec les modifications
                    documentReference.update(updates)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void v) {
                                    showToast("comment posted ");
                                    center.addComment(user.getUsername() + " : "  + getTime()+ " : " + binding.commentEdittext.getText());
                                    loadComment();
                                    binding.commentEdittext.setText(null);

                                    //remettre les couleurs de base
                                    binding.comments1.setTextColor(Color.BLACK);
                                    binding.comments2.setTextColor(Color.BLACK);
                                    binding.comments3.setTextColor(Color.BLACK);


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showToast("Error " + e);
                                }
                            });
                }
            });
        }

    }

    private void reservation_grid(String date) {

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

                                                        Reservation reservation = new Reservation( hour, center.getNameCenter(), user, date, center.getImage(), center.getPriceHour());
                                                        // Ajouter la réservation à Firestore
                                                        addToFirestore(reservation);

                                                        // ---------------------------------- notification ----------------------------------------
                                                        // recuperer debut heure reservé (exemple 8h-9h => 8)
                                                        int startHour = getStartHour(hour);

                                                        // Récupérer la date et l'heure actuelles
                                                        Calendar calendar = Calendar.getInstance();
                                                        calendar.setTimeInMillis(System.currentTimeMillis());

                                                        // Définir la date spécifique exemple => 25/05/2023
                                                        String[] parts = date.split("/");
                                                        String day = parts[0].trim();
                                                        String month = parts[1].trim();
                                                        String year = parts[2].trim();

                                                        calendar.set(Calendar.YEAR, Integer.parseInt(year)); // Année
                                                        calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1); // Mois -1 car janvier correspand a 0
                                                        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day)); // Jour

                                                        // Définir l'heure spécifique
                                                        calendar.set(Calendar.HOUR_OF_DAY, startHour); // Heure

                                                        // Soustraire 24 heures à la date et l'heure de la reservation
                                                        int minuteBeforNotification = -1440; // 60 min * 24 => 1440 => 24h

                                                        calendar.set(Calendar.MINUTE, 0); // Réinitialiser les minutes à 0
                                                        calendar.add(Calendar.MINUTE, minuteBeforNotification);

                                                        // Calculer la différence de temps entre l'heure actuelle et l'heure de la reservation
                                                        long timeDifference = calendar.getTimeInMillis() - System.currentTimeMillis();

                                                        if (timeDifference > 0) {
                                                            // Créer un objet Handler pour afficher la notification 24 heures avant la date et l'heure spécifiées
                                                            Handler handler = new Handler();
                                                            handler.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    // Afficher la notification
                                                                    showNotification("Reservation", "Reservation : " + center.getNameCenter() +" - " + date + " at " + hour);
                                                                }
                                                            }, timeDifference);
                                                        }

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

    private int getStartHour(String hour) {
        String[] parts = hour.split("-");
        String startTime = parts[0].trim();
        String startTimeWithoutHour = startTime.replaceAll("H", "");
        return Integer.parseInt(startTimeWithoutHour);
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
                        showToast("reservation successfully completed ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Error reservation ");

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
                    // Créer User et initialiser ses attributs
                    User user = new User();
                    user.setUsername(documentSnapshot.getString("username"));
                    user.setEmail(documentSnapshot.getString("email"));
                    user.setPassword(documentSnapshot.getString("password"));

                    userConnected = user;

                    // Appeler la méthode onUserFetch() de l'interface de rappel avec l'objet User
                    listener.onUserFetch(user);
                }
            }
        });
    }


    // api meteo
    private void getInfoWheather(double latitude, double longitude) {
        final String apiKey = "9211be69198a3f97d01c865ada5360e4";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&cnt="+daySelectedInCalendar+"&appid=" + apiKey;
        //https://api.openweathermap.org/data/2.5/forecast?lat=50.8503396&lon=4.3517103&cnt=3&appid=9211be69198a3f97d01c865ada5360e4
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
    }

    private String showOpeningHours(HashMap<String, String> openingHours) {
        String messageToReturn = "";
        for (Map.Entry<String, String> entry : openingHours.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            messageToReturn += key + " : " + value + "\n";
        }
        return messageToReturn;
    }




    private String getTime() {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String currentDateTime = dateFormat.format(now);

        return currentDateTime;
    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}