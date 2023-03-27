package com.example.bookfutsal.models;

public class SportCenter {
    private String nameCenter;
    private double latitude, longitude;

    public SportCenter(String nameCenter, double latitude, double longitude){
        this.nameCenter = nameCenter;
        this.latitude = latitude;
        this.longitude = longitude;
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
