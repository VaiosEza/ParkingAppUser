package com.example.parkingappuser;

import android.os.StrictMode;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostData {

    public  PostData() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    String register (String url , String name, String email, String password) throws Exception {
        String status="";
        String data="";
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("email", email)
                .add("password", password)
                .add("admin_rights","0")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();

        try {
            data = response.body().string();
            JSONObject json = new JSONObject(data);
            status = json.getString("status");

            //System.out.println("Response body: " + data);

        }
        catch (JSONException e) {
            e.printStackTrace();
            status ="0";
        }

        if(status.equals("0")){
            return "Server is down"+"#"+status;
        }
        //System.out.println("My status "+status);

        return data +"#"+ status;
    }

    String postLocation (String url , String locationName , String cost , String parkingSlots , String startTime , String stopTime , String weekDays , double latitude , double longitude) throws Exception {

        String status="";
        String data="";

        OkHttpClient client = new OkHttpClient();



        FormBody.Builder builder = new FormBody.Builder()
                .add("Location", locationName)
                .add("Cost", String.valueOf(cost))
                .add("Parking_Slots", String.valueOf(parkingSlots))
                .add("Latitude", String.valueOf(latitude))
                .add("Longitude", String.valueOf(longitude))
                .add("Non_billing_start", startTime)
                .add("Non_billing_end", stopTime)
                .add("Non_billing_weekday", weekDays);


        RequestBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();

        try {
            data = response.body().string();
            JSONObject json = new JSONObject(data);
            status = json.getString("status");

            //System.out.println("Response body: " + data);

        }
        catch (JSONException e) {
            e.printStackTrace();
            status ="0";
        }

        if(status.equals("0")){
            return "Server is down"+"#"+status;
        }
        //System.out.println("My status "+status);

        return data +"#"+ status;
    }

    String updateLocation (String url ,String oldLocName ,String locationName ,String cost ,String slots ,String startTime ,String stopTime ,String weekDays ,double latitude ,double longitude) throws Exception {

        String status="";
        String data="";

        OkHttpClient client = new OkHttpClient();



        FormBody.Builder builder = new FormBody.Builder()
                .add("OldLocation",oldLocName)
                .add("Location", locationName)
                .add("Cost", String.valueOf(cost))
                .add("Parking_Slots", String.valueOf(slots))
                .add("Latitude", String.valueOf(latitude))
                .add("Longitude", String.valueOf(longitude))
                .add("Non_billing_start", startTime)
                .add("Non_billing_end", stopTime)
                .add("Non_billing_weekday", weekDays);


        RequestBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();

        try {
            data = response.body().string();
            JSONObject json = new JSONObject(data);
            status = json.getString("status");

            System.out.println("Response body: " + data);

        }
        catch (JSONException e) {
            e.printStackTrace();
            status ="0";
        }

        if(status.equals("0")){
            return "Server is down"+"#"+status;
        }
        //System.out.println("My status "+status);

        return status;
    }

    String deleteLocation (String url ,String locationName) throws Exception {

        String status="";
        String data="";

        OkHttpClient client = new OkHttpClient();

        FormBody.Builder builder = new FormBody.Builder().add("Location", locationName);

        RequestBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();

        try {
            data = response.body().string();
            JSONObject json = new JSONObject(data);
            status = json.getString("status");

            System.out.println("Response body: " + data);

        }
        catch (JSONException e) {
            e.printStackTrace();
            status ="0";
        }

        if(status.equals("0")){
            return "Server is down"+"#"+status;
        }
        //System.out.println("My status "+status);

        return status;
    }

}
