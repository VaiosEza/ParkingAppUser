package com.example.parkingappuser;

public class MapLocations {
    private String locName;
    private double cost;

    private int slots;
    private double latitude;
    private double longitude;
    private String startBill;
    private String stopBill;
    private String weekDays;

    MapLocations(String locName, double cost, int slots, double latitude, double longitude, String startBill, String stopBill, String weekDays) {
        this.locName = locName;
        this.cost = cost;
        this.slots = slots;
        this.latitude = latitude;
        this.longitude = longitude;
        this.startBill = startBill;
        this.stopBill = stopBill;
        this.weekDays = weekDays;
    }

    public String getLocName() {
        return locName;
    }

    public double getCost() {
        return cost;
    }

    public int getSlots() {return slots; }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getStartBill() {
        return startBill;
    }

    public String getStopBill() {
        return stopBill;
    }

    public String getWeekDays() {
        return weekDays;
    }

}
