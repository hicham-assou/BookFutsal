package com.example.bookfutsal.models;

public class User {

    public String username, password, email;

    public User() {}

    public User(String username, String password, String email) {

        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
