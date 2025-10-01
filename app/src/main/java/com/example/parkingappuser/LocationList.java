package com.example.parkingappuser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationList {

    private ArrayList <MapLocations> locations;

    public LocationList (String url) throws IOException {
        RetrieveData data = new RetrieveData();
        locations = data.getLocations(url);
    }

    public List <String> getLocations(){
        List <String> names = new ArrayList<>();
        for(int i = 0;i<locations.size();i++){
            names.add(locations.get(i).getLocName());
        }
        return names;
    }

    public String getName(int i){
        return locations.get(i).getLocName();
    }

    public double getCostPerHour(int i){
        return locations.get(i).getCost();
    }

    public int getParkingSlots(int i){ return locations.get(i).getSlots(); }

    public String getNonStartBill(int i){
        return locations.get(i).getStartBill();
    }

    public String getNonStopBill(int i){
        return locations.get(i).getStopBill();
    }

    public String getNonWeekDays(int i){
        return locations.get(i).getWeekDays();
    }

    public double getLat(int i){
        return locations.get(i).getLatitude();
    }

    public double getLongi(int i){
        return locations.get(i).getLongitude();
    }

}

