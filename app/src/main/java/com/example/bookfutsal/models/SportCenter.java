package com.example.bookfutsal.models;

public class SportCenter {
    private int id;
    private String nameCenter, adress;
    private double latitude, longitude;

    public SportCenter(int id, String nameCenter, double latitude, double longitude, String adress){
        this.id = id;
        this.nameCenter = nameCenter;
        this.latitude = latitude;
        this.longitude = longitude;
        this.adress = adress;
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
