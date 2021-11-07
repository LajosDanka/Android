package com.example.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    EditText username;
    EditText password;

    public String passesUsername = "";

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String sharedPrefFile = "sharedFile";
        sharedPref = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        username = findViewById(R.id.usernameLogin);
        password = findViewById(R.id.passwordLogin);
    }

    public void LoginOnClick(View view){

        String valueForTheKey = sharedPref.getString(username.getText().toString(), "Something#SomethingElse");

        String[] spl = valueForTheKey.split("#");
        if (!spl[0].equals("Something") && spl[1].equals(password.getText().toString())) {
            passesUsername = username.getText().toString();
            Intent intent = new Intent(this, InternalWindow.class);
            intent.putExtra("username", username.getText().toString());
            startActivity(intent);
        }

        finish();
    }

}
