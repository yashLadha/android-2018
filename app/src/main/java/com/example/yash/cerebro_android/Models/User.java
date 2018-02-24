package com.example.yash.cerebro_android.Models;

/**
 * Created by yash on 25/2/18.
 */

public class User {

    private String ID;
    private String Email;
    private String Name;
    private String Number;

    public User() {
    }

    public User(String ID, String email, String name, String number) {
        this.ID = ID;
        Email = email;
        Name = name;
        Number = number;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }
}
