package com.example.parkingappuser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField , passwordField;
    private String email,password;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.emailLog);
        passwordField = findViewById(R.id.passwordLog);
        email = password =" ";
    }

    public void login(View view) throws Exception {
        String url = getResources().getString(R.string.Login_URL);
        email = emailField.getText().toString().trim();
        password = passwordField.getText().toString().trim();

        if (!email.isEmpty() && !password.isEmpty()) {

            RetrieveData data = new RetrieveData();
            User user = data.getUserDetails(url, email, password);

            if (user.geStatus().equals("Success")) {

                    intent = new Intent(LoginActivity.this, UserActivity.class);
                    intent.putExtra("user", user);

                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this,"All fields are required ",Toast.LENGTH_SHORT).show();
        }

    }

    public void register(View view){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
