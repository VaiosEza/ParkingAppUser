package com.example.parkingappuser;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String email;
    private String password;
    private int admin_rights;

    private double balance;
    private String status;

    User(String name , String email, String password , int admin_rights , double balance,String status){
        this.name = name;
        this.email = email;
        this.password = password;
        this.admin_rights = admin_rights;
        this.balance = balance;
        this.status = status;

    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getAdmin_rights() {
        return admin_rights;
    }

    private double getBalance() {return balance;}

    public String geStatus() {
        return status;
    }
}
