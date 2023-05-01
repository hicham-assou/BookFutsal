package com.example.bookfutsal.models;

public class Reservation {
    private String hour, sportCenterName, date, imageCenter;
    private User booker;
    private int price;

    public Reservation() {
    }

    public int getPrice() {
        return price;
    }

    public Reservation(String hour, String sportCenterName, User booker, String date, String imageCenter, int price) {
        this.hour = hour;
        this.sportCenterName = sportCenterName;
        this.booker = booker;
        this.date = date;
        this.imageCenter = imageCenter;
        this.price = price;
    }

    public Reservation(String hour, String sportCenterName, String date) {
        this.hour = hour;
        this.sportCenterName = sportCenterName;
        this.date = date;
    }

    public Reservation(String hour, String sportCenterName, String date, String imageCenter, int price) {
        this.hour = hour;
        this.sportCenterName = sportCenterName;
        this.date = date;
        this.imageCenter = imageCenter;
        this.price = price;
    }


    public String getDate() {
        return date;
    }


    public String getImageCenter() {
        return imageCenter;
    }



    public String getSportCenterName() {
        return sportCenterName;
    }
    public String getHour() {
        return hour;
    }



    public User getBooker() {
        return booker;
    }

}
