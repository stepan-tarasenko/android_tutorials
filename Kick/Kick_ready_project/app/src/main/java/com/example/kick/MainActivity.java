package com.example.kick;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private boolean isGameStarted = false;
    private long startedGameTime = 0;

    private MediaPlayer mediaPlayer;

    private ArrayList<Double> accelerationLog;

    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewsAndSetListeners();
        initPlayer();
        initSensor();
    }

    private void initPlayer() {
        accelerationLog = new ArrayList<>(Arrays.asList(0.0));
        mediaPlayer = MediaPlayer.create(this, R.raw.punch);
    }

    private void initSensor() {
        SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        manager.registerListener(this, sensor, Sensor.REPORTING_MODE_ONE_SHOT);
    }


    private void findViewsAndSetListeners() {
        result = findViewById(R.id.result);

        ImageButton kickButton = findViewById(R.id.kick_button);
        kickButton.setOnClickListener(v -> {
            isGameStarted = true;
            startedGameTime = System.currentTimeMillis();
        });

        ImageButton refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(v -> {
            accelerationLog.clear();
            accelerationLog.add(0.0);
        });
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION
        && (System.currentTimeMillis() - startedGameTime)
                * Math.pow(10, -3) < 5 && isGameStarted){
            double acceleration = 0.0;
            if ((acceleration = Math.sqrt(
                            Math.pow(event.values[0], 2)
                            + Math.pow(event.values[1], 2)
                            + Math.pow(event.values[2], 2))) >= 25){
                makeSound();
                writeToArray(acceleration);
            }
        } else {
            result.setText(String.format("%.3f kg",
                    40 * accelerationLog
                            .stream()
                            .max(Double::compareTo)
                            .get() / 9.8));
            isGameStarted = false;
        }
    }

    private void writeToArray(double acceleration) {
        accelerationLog.add(acceleration);
    }

    private void makeSound() {
        mediaPlayer.start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
