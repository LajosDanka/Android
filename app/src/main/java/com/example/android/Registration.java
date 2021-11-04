package com.example.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class Registration extends AppCompatActivity {

    EditText username;
    EditText email;
    EditText password;
    EditText passwordAgain;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        String sharedPrefFile = "sharedFile";
        sharedPref = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        username = findViewById(R.id.editTextUsername);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        passwordAgain = findViewById(R.id.editTextPasswordAgain);
    }

    public void RegistrationButtonOnClick(View view){

        boolean firstSpecial = false;
        boolean secondSpecial = false;

        SharedPreferences.Editor preferencesEditor = sharedPref.edit();

        StringBuilder sb = new StringBuilder();

        if(!username.getText().toString().equals("") && username.getText().toString().length() >= 5){
            for (int i = 0 ; i < email.getText().toString().length() ; i++){
                if(email.getText().toString().charAt(i) == '@')
                    firstSpecial = true;
                else if(firstSpecial && email.getText().toString().charAt(i) == '.')
                    secondSpecial = true;
            }
            if(firstSpecial && secondSpecial
                    && !password.getText().toString().equals("")
                    && password.getText().toString().equals(passwordAgain.getText().toString()))
            {
                preferencesEditor.putString(
                        username.getText().toString(),
                        sb.append(email.getText().toString())
                                .append("#")
                                .append(password.getText().toString()).toString());
                preferencesEditor.apply();
            }
        }

        finish();
    }

}
