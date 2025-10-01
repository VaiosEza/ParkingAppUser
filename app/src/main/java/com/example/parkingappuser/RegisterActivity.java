package com.example.parkingappuser;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameField,emailField,passwordField,verifyPasswordField;
    private String name,email,password,verifyPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nameField = findViewById(R.id.usernameReg);
        emailField = findViewById(R.id.emailReg);
        passwordField = findViewById(R.id.passwordReg);
        verifyPasswordField = findViewById(R.id.verifyPasswordReg);
        name = email = password = verifyPassword = " ";

    }

    public void createAcc(View view) throws Exception {
        name = nameField.getText().toString().trim();
        email = emailField.getText().toString().trim();
        password = passwordField.getText().toString().trim();
        verifyPassword = verifyPasswordField.getText().toString().trim();

        if (!verifyPassword.equals(password)) {
            Toast.makeText(this, "Password Mismatch", Toast.LENGTH_SHORT).show();

        } else if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,"All fields are required ",Toast.LENGTH_SHORT).show();
        }

        else{
            PostData post = new PostData();
            String url = getResources().getString(R.string.Register_URL);
            String [] response = post.register(url,name,email,password).split("#");

            //System.out.println("My Response[0]: " + response[0]);
            //System.out.println("My Response[1]: " + response[1]);

            if (response[1].equals("Success")){
                Toast.makeText(this,"Successfully registered",Toast.LENGTH_LONG).show();
            }
            else if (response[0].contains("Duplicate entry")){
                Toast.makeText(this, "This email is already used", Toast.LENGTH_LONG).show();

            }
            else if(response[1].equals("0")){
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        }



    }


}