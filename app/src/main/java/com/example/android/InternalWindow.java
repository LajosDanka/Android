package com.example.android;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class InternalWindow extends AppCompatActivity implements SensorEventListener{

    private SharedPreferences sharedPref;
    TextView temperatureView;
    TextView lightView;
    TextView humidityView;
    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private Sensor lightSensor;
    private Sensor humiditySensor;

    private String username = "";

    String concatExplicitCastTemperature;
    String concatExplicitCastLight;
    String concatExplicitCastHumidity;

    Date currentTime;

    String finalAddress = "";

    final int requestPermission = 1;
    final int timeDuration = 10000;

    double latitude = 250.0;
    double longitude = 250.0;
    String city = "Debrecen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal);
        String sharedPrefFile = "sharedFile";

        sharedPref          = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        sensorManager       = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        temperatureSensor   = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        lightSensor         = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        humiditySensor      = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

        sensorManager.registerListener((SensorEventListener) this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener((SensorEventListener) this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener((SensorEventListener) this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);

        temperatureView = findViewById(R.id.textViewTemperature);
        lightView       = findViewById(R.id.textViewLight);
        humidityView    = findViewById(R.id.textViewHumidity);

        temperatureView.setText("0 °C");
        temperatureView.setBackgroundColor(Color.parseColor("#FFE800"));
        lightView.setText("0 lux");
        lightView.setBackgroundColor(Color.parseColor("#FFE800"));
        humidityView.setText("0 %");
        humidityView.setBackgroundColor(Color.parseColor("#00FF00"));

        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null){
                username = "";
            }
            else
                username = extras.getString("username");
        }
        else{
            username = (String) savedInstanceState.getSerializable("username");
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(com.example.android.InternalWindow.this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    requestPermission);
        }
        else{
            getCurrentLocation();
        }

        Loading();
    }

    public void Save(View view) {

        getCurrentLocation();
        if(city.equals("")){
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(com.example.android.InternalWindow.this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    requestPermission);
        }

        List<String> valueList = new ArrayList<>();
        String valueForTheKey = sharedPref.getString(username, "Something#SomethingElse");
        String[] spl = valueForTheKey.split("#");

        currentTime = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd\nHH:mm:ss");
        String formattedDate = df.format(Calendar.getInstance().getTime());

        valueList.add(spl[0]);
        valueList.add(spl[1]);
        valueList.add(city == null ? "Unknown" : city);
        valueList.add(currentTime == null ? "-" : formattedDate);
        valueList.add(concatExplicitCastTemperature == null ? "0 °C" : concatExplicitCastTemperature);
        valueList.add(concatExplicitCastLight == null ? "0 lux" : concatExplicitCastLight);
        valueList.add(concatExplicitCastHumidity == null ? "0 %" : concatExplicitCastHumidity);

        for (int i = 2 ; i < spl.length ; i++)
            valueList.add(spl[i]);

        StringBuilder sb = new StringBuilder();

        for (int i = 0 ; i < valueList.size() ; i++){
            if(i == 27) //17
                break;
            else if(i + 1 == valueList.size() || i + 1 == 27){
                sb.append(valueList.get(i));
            }
            else{
                sb.append(valueList.get(i)).append("#");
            }
        }

        SharedPreferences.Editor preferencesEditor = sharedPref.edit();
        preferencesEditor.putString(username, sb.toString());
        preferencesEditor.apply();

        Loading();

    }

    public void Loading(){
        List<String> valueList = new ArrayList<>();
        String valueForTheKey = sharedPref.getString(username, "Something#SomethingElse");
        String[] spl = valueForTheKey.split("#");

        for (int i = 2 ; i < spl.length ; i += 5){
            TextView city, date, temp, light, humidity;
            switch((i - 2) / 5){
                case 1:
                    city = findViewById(R.id.textViewR2C1);
                    date = findViewById(R.id.textViewR2C2);
                    temp = findViewById(R.id.textViewR2C3);
                    light = findViewById(R.id.textViewR2C4);
                    humidity = findViewById(R.id.textViewR2C5);
                    break;
                case 2:
                    city = findViewById(R.id.textViewR3C1);
                    date = findViewById(R.id.textViewR3C2);
                    temp = findViewById(R.id.textViewR3C3);
                    light = findViewById(R.id.textViewR3C4);
                    humidity = findViewById(R.id.textViewR3C5);
                    break;
                case 3:
                    city = findViewById(R.id.textViewR4C1);
                    date = findViewById(R.id.textViewR4C2);
                    temp = findViewById(R.id.textViewR4C3);
                    light = findViewById(R.id.textViewR4C4);
                    humidity = findViewById(R.id.textViewR4C5);
                    break;
                case 4:
                    city = findViewById(R.id.textViewR5C1);
                    date = findViewById(R.id.textViewR5C2);
                    temp = findViewById(R.id.textViewR5C3);
                    light = findViewById(R.id.textViewR5C4);
                    humidity = findViewById(R.id.textViewR5C5);
                    break;
                default:
                    city = findViewById(R.id.textViewR1C1);
                    date = findViewById(R.id.textViewR1C2);
                    temp = findViewById(R.id.textViewR1C3);
                    light = findViewById(R.id.textViewR1C4);
                    humidity = findViewById(R.id.textViewR1C5);
            }
            city.setText(spl[i]);
            date.setText(spl[i + 1]);
            temp.setText(spl[i + 2]);
            light.setText(spl[i + 3]);
            humidity.setText(spl[i + 4]);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float temperature   = sensorEvent.values[0];
        float light         = sensorEvent.values[0];
        float humidity      = sensorEvent.values[0];

        if(sensorEvent.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE)
        {
            if(temperature >= 0.0 && temperature < 20.0)
                temperatureView.setBackgroundColor(Color.parseColor("#FFE800"));
            else if(temperature >= 20.0 && temperature < 40.0)
                temperatureView.setBackgroundColor(Color.parseColor("#FFAA00"));
            else if(temperature >= 40.0 && temperature < 60.0)
                temperatureView.setBackgroundColor(Color.parseColor("#FF7000"));
            else if(temperature >= 60.0)
                temperatureView.setBackgroundColor(Color.parseColor("#FF0000"));
            else
                temperatureView.setBackgroundColor(Color.parseColor("#001190"));

            concatExplicitCastTemperature = "" + temperature + " °C";
            temperatureView.setText(concatExplicitCastTemperature);
        }

        else if(sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT)
        {
            if(light >= 0.0 && light < 10000.0)
                lightView.setBackgroundColor(Color.parseColor("#FFE800"));
            else if(light >= 10000.0 && light < 20000.0)
                lightView.setBackgroundColor(Color.parseColor("#FFAA00"));
            else if(light >= 20000.0 && light < 30000.0)
                lightView.setBackgroundColor(Color.parseColor("#FF7000"));
            else if(light >= 30000.0)
                lightView.setBackgroundColor(Color.parseColor("#FF0000"));
            else
                lightView.setBackgroundColor(Color.parseColor("#001190"));

            concatExplicitCastLight = "" + light + " lux";
            lightView.setText(concatExplicitCastLight);
        }

        else if(sensorEvent.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY)
        {
            if(humidity >= 20.0 && humidity < 40.0)
                humidityView.setBackgroundColor(Color.parseColor("#FFE800"));
            else if(humidity >= 40.0 && humidity < 60.0)
                humidityView.setBackgroundColor(Color.parseColor("#FFAA00"));
            else if(humidity >= 60.0 && humidity < 80.0)
                humidityView.setBackgroundColor(Color.parseColor("#FF7000"));
            else if(humidity >= 80.0)
                humidityView.setBackgroundColor(Color.parseColor("#FF0000"));
            else
                humidityView.setBackgroundColor(Color.parseColor("#001190"));

            concatExplicitCastHumidity = "" + humidity + " %";
            humidityView.setText(concatExplicitCastHumidity);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void getCurrentLocation(){
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(timeDuration);
        locationRequest.setFastestInterval(timeDuration / 4);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        LocationServices.getFusedLocationProviderClient(InternalWindow.this).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(InternalWindow.this);
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    latitude = locationResult.getLastLocation().getLatitude();
                    longitude = locationResult.getLastLocation().getLongitude();

                    Address address = getAddress();

                    city = address.getLocality();
                }
            }
        },  Looper.getMainLooper());
    }

    public Address getAddress(){
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses = null;

        try{
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException ex){ }

        return addresses.get(0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == requestPermission && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getCurrentLocation();
        }
        else{
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }
}
