package com.example.bookfutsal.models;

public class Reservation {
    private String id, hour, sportCenterName, date, imageCenter;
    private User booker;
    private int price;



    public Reservation() {
    }

    public Reservation(String id, String hour, String sportCenterName, String date, String image, int price) {
        this.id = id;
        this.hour = hour;
        this.sportCenterName = sportCenterName;
        this.date = date;
        this.imageCenter = image;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public Reservation( String hour, String sportCenterName, User booker, String date, String imageCenter, int price) {
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

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
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
