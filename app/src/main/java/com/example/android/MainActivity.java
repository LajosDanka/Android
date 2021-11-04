package com.example.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void RegistrationButtonOnClick(View view) {
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

    public void LoginOnClick(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

}