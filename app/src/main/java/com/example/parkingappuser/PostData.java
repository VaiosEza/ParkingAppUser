package com.example.parkingappuser;

import android.os.StrictMode;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.MediaType;
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

    String start_parking_session (String url , String email, String location , String license_plate) throws Exception {
        String status="";
        String data="";
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("user_email", email)
                .add("location", location)
                .add("license_plate", license_plate)
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

    String stop_parking_session(String url, String email) throws Exception {
        String status = "";
        double totalCost = 0.0;
        double newBalance = 0.0;
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("user_email", email)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();

        // 1. ΔΙΑΒΑΖΟΥΜΕ ΤΗΝ ΑΠΑΝΤΗΣΗ ΑΠΟ ΤΟΝ SERVER (Η ΓΡΑΜΜΗ ΠΟΥ ΕΛΕΙΠΕ)
        String responseData = response.body().string();

        try {
            // 2. ΔΗΜΙΟΥΡΓΟΥΜΕ ΤΟ JSON OBJECT ΑΠΟ ΤΗΝ ΑΠΑΝΤΗΣΗ
            JSONObject json = new JSONObject(responseData);
            status = json.optString("status", "Failure"); // optString είναι πιο ασφαλές

            if ("Success".equalsIgnoreCase(status)) {
                // 3. ΔΙΑΒΑΖΟΥΜΕ ΤΙΣ ΤΙΜΕΣ ΑΠΕΥΘΕΙΑΣ ΑΠΟ ΤΟ ΚΥΡΙΟ OBJECT
                // Δεν υπάρχει "data" object μέσα στην απάντηση
                totalCost = json.getDouble("totalCost");
                newBalance = json.getDouble("newBalance");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            status = "0";
        }

        if (!"Success".equalsIgnoreCase(status)) {
            return "Server Error#0#0"; // Επιστρέφουμε default τιμές σε περίπτωση σφάλματος
        }

        // Επιστρέφουμε τις τιμές με το format που θέλετε
        return totalCost + "#" + newBalance + "#" + status;
    }


}
