package com.example.parkingappuser;

import android.os.StrictMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RetrieveData {

    public  RetrieveData() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    User getUserDetails (String url , String email , String password) throws Exception {
        String name = "";
        int admin_rights = -1;
        String status = "";

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        String data = response.body().string();
        //System.out.println("My Response: " + data);

        try {
            JSONObject json = new JSONObject(data);
            status = json.getString("status");

            if (status.equals("Success")) {
                JSONObject userData = json.getJSONObject("data");
                name = userData.getString("name");
                admin_rights = userData.getInt("admin_rights");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new User(name, email, password, admin_rights, status);
    }


    public ArrayList<MapLocations> getLocations(String url) throws IOException {
        ArrayList<MapLocations> locationItems = new ArrayList<>();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        RequestBody body = RequestBody.create("", MediaType.parse("text/plain"));
        Request request = new Request.Builder().url(url).method("POST", body).build();
        Response response = client.newCall(request).execute();
        String data = response.body().string();
        System.out.println("My Response retrieve: " + data);
        try {
            JSONObject  json = new JSONObject (data);
            Iterator<String> keys = json.keys();
            while(keys.hasNext()) {

                String id = keys.next();
                String locName = json.getJSONObject(id).getString("Location");
                double cost = json.getJSONObject(id).getDouble("Cost");
                int slots = json.getJSONObject(id).getInt("Parking_Slots");
                double latitude = json.getJSONObject(id).getDouble("Latitude");
                double longitude = json.getJSONObject(id).getDouble("Longitude");
                String billStart = json.getJSONObject(id).getString("BillStart");
                String billStop = json.getJSONObject(id).getString("BillStop");
                String weekDays = json.getJSONObject(id).getString("WeekDays");

                locationItems.add(new MapLocations(locName,cost,slots,latitude,longitude,billStart,billStop,weekDays));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return locationItems;
    }

}



