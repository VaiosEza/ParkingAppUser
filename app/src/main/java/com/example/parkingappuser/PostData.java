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

    public Double updateUserBalance(String url, String email, double amountPaid) throws Exception {
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("amount_paid", amountPaid);
            jsonBody.put("user_email", email);
        } catch (JSONException e) {
            e.printStackTrace();
            return null; // Αποτυχία δημιουργίας JSON
        }

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        String data = response.body().string();

        try {
            JSONObject jsonResponse = new JSONObject(data);
            String status = jsonResponse.getString("status");

            if (status.equals("success")) {
                // Αν είναι επιτυχής, διάβασε και επίστρεψε το "newBalance"
                return jsonResponse.getDouble("newBalance");
            } else {
                // Αν ο server απάντησε με status "error"
                return null;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            // Αν η απάντηση δεν είναι το αναμενόμενο JSON, απέτυχε
            return null;
        }
    }


}
