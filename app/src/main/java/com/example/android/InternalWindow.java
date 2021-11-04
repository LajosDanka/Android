package com.example.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InternalWindow extends AppCompatActivity implements SensorEventListener {

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

    @SuppressLint("SetTextI18n")
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

        Loading();

    }

    public void Save(View view) {

        List<String> valueList = new ArrayList<>();
        String valueForTheKey = sharedPref.getString(username, "Something#SomethingElse");
        String[] spl = valueForTheKey.split("#");

        valueList.add(spl[0]);
        valueList.add(spl[1]);
        valueList.add(concatExplicitCastTemperature == null ? "0 °C" : concatExplicitCastTemperature);
        valueList.add(concatExplicitCastLight == null ? "0 lux" : concatExplicitCastLight);
        valueList.add(concatExplicitCastHumidity == null ? "0 %" : concatExplicitCastHumidity);

        for (int i = 2 ; i < spl.length ; i++)
            valueList.add(spl[i]);

        StringBuilder sb = new StringBuilder();

        for (int i = 0 ; i < valueList.size() ; i++){
            if(i == 17)
                break;
            else if(i + 1 == valueList.size() || i + 1 == 17){
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

        for (int i = 2 ; i < spl.length ; i += 3){
            TextView temp, light, humidity;
            switch((i - 2) / 3){
                case 1:
                    temp = findViewById(R.id.textViewR2C3);
                    light = findViewById(R.id.textViewR2C4);
                    humidity = findViewById(R.id.textViewR2C5);
                    break;
                case 2:
                    temp = findViewById(R.id.textViewR3C3);
                    light = findViewById(R.id.textViewR3C4);
                    humidity = findViewById(R.id.textViewR3C5);
                    break;
                case 3:
                    temp = findViewById(R.id.textViewR4C3);
                    light = findViewById(R.id.textViewR4C4);
                    humidity = findViewById(R.id.textViewR4C5);
                    break;
                case 4:
                    temp = findViewById(R.id.textViewR5C3);
                    light = findViewById(R.id.textViewR5C4);
                    humidity = findViewById(R.id.textViewR5C5);
                    break;
                default:
                    temp = findViewById(R.id.textViewR1C3);
                    light = findViewById(R.id.textViewR1C4);
                    humidity = findViewById(R.id.textViewR1C5);
            }
            temp.setText(spl[i]);
            light.setText(spl[i + 1]);
            humidity.setText(spl[i + 2]);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float temperature   = sensorEvent.values[0];
        float light         = sensorEvent.values[0];
        float humidity      = sensorEvent.values[0];


        if(sensorEvent.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE)
        {
            /*
             * Temperature TextView get a new color */
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
            /*
             * Light TextView get a new color */
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
            /*
             * Humidity TextView get a new color */
            humidityView.setBackgroundColor(Color.parseColor("#00FF00"));

            concatExplicitCastHumidity = "" + humidity + " %";
            humidityView.setText(concatExplicitCastHumidity);
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
