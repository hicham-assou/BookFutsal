package com.example.bookfutsal.models;

public class Reservation {
    private String hour, sportCenterName, date;
    private User booker;

    public Reservation() {
    }

    public Reservation(String hour, String sportCenterName, User booker, String date) {
        this.hour = hour;
        this.sportCenterName = sportCenterName;
        this.booker = booker;
        this.date = date;
    }

    public Reservation(String hour, String sportCenterName, String date) {
        this.hour = hour;
        this.sportCenterName = sportCenterName;
        this.date = date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }



    public void setSportCenterName(String sportCenterName) {
        this.sportCenterName = sportCenterName;
    }

    public void setBooker(User booker) {
        this.booker = booker;
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
