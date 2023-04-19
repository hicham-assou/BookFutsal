package com.example.bookfutsal.models;

import java.io.Serializable;
import java.util.HashMap;

public class SportCenter implements Serializable {
    private int id, priceHour;
    private String nameCenter, adress, image, phoneNumber;
    private double latitude, longitude;
    private HashMap<String, String> openingHours;

    public SportCenter(int id, String nameCenter, double latitude, double longitude, String adress, String phoneNumber, int priceHour, HashMap<String, String> openingHours){
        this.id = id;
        this.nameCenter = nameCenter;
        this.latitude = latitude;
        this.longitude = longitude;
        this.adress = adress;
        this.phoneNumber = phoneNumber;
        this.priceHour = priceHour;
        this.openingHours = openingHours;
    }

    public SportCenter(int id, String nameCenter, double latitude, double longitude, String adress){
        this.id = id;
        this.nameCenter = nameCenter;
        this.latitude = latitude;
        this.longitude = longitude;
        this.adress = adress;
    }

    public int getPriceHour() {
        return priceHour;
    }

    public String getImage() {
        return image;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public HashMap<String, String> getOpeningHours() {
        return openingHours;
    }

    public int getId() {
        return id;
    }

    public String getAdress() {
        return adress;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getNameCenter() {
        return nameCenter;
    }

    public double getLongitude() {
        return longitude;
    }

}
