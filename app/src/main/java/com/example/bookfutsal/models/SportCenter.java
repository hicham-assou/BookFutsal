package com.example.bookfutsal.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SportCenter implements Serializable {
    private int id, priceHour;
    private String nameCenter, adress, image, phoneNumber, comments;
    private double latitude, longitude;
    private HashMap<String, String> openingHours;

    /*public SportCenter(int id, String nameCenter, double latitude, double longitude, String adress, String phoneNumber, int priceHour, Map<String, Object> openingHours, String comments){
        this.id = id;
        this.nameCenter = nameCenter;
        this.latitude = latitude;
        this.longitude = longitude;
        this.adress = adress;
        this.phoneNumber = phoneNumber;
        this.priceHour = priceHour;
        this.openingHours = openingHours;
        this.comments = comments;
    }

    public SportCenter(int id, String nameCenter, double latitude, double longitude, String adress){
        this.id = id;
        this.nameCenter = nameCenter;
        this.latitude = latitude;
        this.longitude = longitude;
        this.adress = adress;
    }*/

    public SportCenter(int priceHour, String nameCenter, String adress, String image, String phoneNumber, String comments, double latitude, double longitude, HashMap<String, String> openingHours) {
        this.priceHour = priceHour;
        this.nameCenter = nameCenter;
        this.adress = adress;
        this.image = image;
        this.phoneNumber = phoneNumber;
        this.comments = comments;
        this.latitude = latitude;
        this.longitude = longitude;
        this.openingHours = openingHours;
    }

    public String getComments() {
        return comments;
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
