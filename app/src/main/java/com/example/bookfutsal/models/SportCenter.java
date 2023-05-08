package com.example.bookfutsal.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SportCenter implements Serializable {
    private int id, priceHour;
    private String nameCenter, adress, image, phoneNumber;
    private List<String> comments;
    private double latitude, longitude;
    private HashMap<String, String> openingHours;

    public SportCenter(int priceHour, String nameCenter, String adress, String image, String phoneNumber, List<String> comments, double latitude, double longitude, HashMap<String, String> openingHours) {
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

    public void addComment(String comment) {
        this.comments.add(comment);
    }

    public List<String> getComments() {
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
