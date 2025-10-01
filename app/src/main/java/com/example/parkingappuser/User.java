package com.example.parkingappuser;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String email;
    private String password;
    private int admin_rights;
    private String status;

    User(String name , String email, String password , int admin_rights , String status){
        this.name = name;
        this.email = email;
        this.password = password;
        this.admin_rights = admin_rights;
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

    public String geStatus() {
        return status;
    }
}
