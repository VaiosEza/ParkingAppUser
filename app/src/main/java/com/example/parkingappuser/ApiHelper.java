package com.example.parkingappuser; // Βεβαιωθείτε ότι το package είναι το σωστό

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiHelper {

    // Μπορούμε να ορίσουμε τον MediaType μία φορά εδώ
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // Η μέθοδος που ήδη έχετε (τροποποιημένη για να ταιριάζει στην κλάση)

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

    public String createPaymentIntent(String url, String email, int amountInCents) {
        try {
            OkHttpClient client = new OkHttpClient();

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("amount", amountInCents);
            jsonBody.put("user_email", email);

            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            String data = response.body().string();

            JSONObject jsonResponse = new JSONObject(data);
            if (jsonResponse.has("clientSecret")) {
                return jsonResponse.getString("clientSecret");
            } else {
                Log.e("ApiHelper", "Server response did not contain clientSecret: " + data);
                return null;
            }
        } catch (IOException | JSONException e) {
            Log.e("ApiHelper", "Error in createPaymentIntent", e);
            return null;
        }
    }
}