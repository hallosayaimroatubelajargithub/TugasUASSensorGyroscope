package com.example.tugasuassensorgyroscope;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tugasuassensorgyroscope.BroadCastReceiverServices.BroadCastService;
import com.example.tugasuassensorgyroscope.DatabaseHelpers.GyroscopeDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import android.widget.Button;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {
    LinearLayout gyroscopePage;
    SensorManager sensorManager;
    Sensor gyroscopeSensor;
    SensorEventListener gyroscopeListener;
    private GyroscopeDatabaseHelper gyroscopeDatabaseHelper;
    long timeLeftInMilliseconds = 30000;
    private Button buttonPrev;
    private Button buttonCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        startService(new Intent(this, BroadCastService.class));

        gyroscopePage = findViewById(R.id.gyroscopePageId);
        gyroscopePage.setOnClickListener(this);

        gyroscopeDatabaseHelper = new GyroscopeDatabaseHelper(this);

        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Update GUI
            updateGUI(intent);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        onSensorChangedMethod();
        registerReceiver(broadcastReceiver, new IntentFilter(BroadCastService.COUNTDOWN_BR));
//        startTimer();
        // set registerListener for each Gyroscope sensors in sensorManager
        sensorManager.registerListener(gyroscopeListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        // set unregisterListener for each Gyroscope sensors when app is paused
        sensorManager.unregisterListener(gyroscopeListener);
    }

    @Override
    public void onStop() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            Toast.makeText(MainActivity2.this, "Sensors cannot detect", Toast.LENGTH_SHORT).show();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, BroadCastService.class));
        super.onDestroy();
    }

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 30000);
            int seconds = (int) (millisUntilFinished / 1000);

            if (seconds < 0) {
                long temp3 = gyroscopeDatabaseHelper.countRows();
                for (long i = 1; i <= temp3; i++) {
                    gyroscopeDatabaseHelper.deleteData(String.valueOf(i));
                }
            }
        }
    }

    // store values of Gyroscope sensors in SQLite database
    public void onSensorChangedMethod() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
        String timeData = simpleDateFormat.format(calendar.getTime());

        // Gyroscope sensor detection code >>>>>>>>>>
        // First check if gyroscope is not null
        if (gyroscopeSensor != null) {
            gyroscopeListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                        // Show 3 axis gyroscope movement results in "degree per second" (°/s) unit
                        gyroscopeDatabaseHelper.insertData(timeData,
                                String.valueOf(event.values[0]), String.valueOf(event.values[1]), String.valueOf(event.values[2]));
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };
        }
        // If gyroscope is null
        else if (gyroscopeSensor == null) {
            gyroscopeDatabaseHelper.insertData(timeData, "not moved", "not moved", "not moved");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.gyroscopePageId) {
            Intent intent = new Intent(getApplicationContext(), GyroscopeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
    }

    public void ButtonPrev(View view) {
        Intent intent = new Intent(MainActivity2.this, MainActivity.class);
        startActivity(intent);
    }

    public void ButtonCenter(View view) {
        Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
        startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
