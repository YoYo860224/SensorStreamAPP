package com.yoyo.SensorStream;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {
    private SensorManager sm;
    private LocationManager lm;
    private String gpsCommand;
    private TextView tv;
    private Button btn;

    Location l;
    float X_axis_rotate;
    float Y_axis_rotate;
    float Z_axis_rotate;

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.textV);
        btn = findViewById(R.id.Btn);

        // Sensor
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(myAccelerometerListener,
                sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);

        // GPS
        gpsCommand = LocationManager.GPS_PROVIDER;
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1);
        }
        else {
            SetTextView();
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                l = lm.getLastKnownLocation(gpsCommand);
                lm.requestLocationUpdates(gpsCommand, 100, 0, (LocationListener) locationListener);
            }
        });
    }

    void SetTextView() {
        if (l != null) {
            tv.setText(String.format("Xaxis_speed %6f", X_axis_rotate) + "\n" +
                    String.format("Yaxis_speed %6f", Y_axis_rotate) + "\n" +
                    String.format("Zaxis_speed %6f", Z_axis_rotate) + "\n" +
                    "速度" + l.getSpeed() + "   m/s\n" +
                    "速度" + l.getSpeed() / 1000.0 * 3600.0 + " km/hr\n" +
                    "經度" + l.getLongitude() + "\n" +
                    "緯度" + l.getLatitude());
        }
        else {
            tv.setText(String.format("Xaxis_speed %6f", X_axis_rotate) + "\n" +
                    String.format("Yaxis_speed %6f", Y_axis_rotate) + "\n" +
                    String.format("Zaxis_speed %6f", Z_axis_rotate) + "\n" +
                    "GPS 訊號未取得");
        }

        WriteInfoToFile();
    }

    void WriteInfoToFile() {
        File f = new File(Environment.getExternalStorageDirectory(), "sensor.txt");
        System.out.println(f.getPath());
        try {
            FileWriter fw = new FileWriter(f);
            if (l != null)
            {
                fw.write(String.format("%6f %6f", l.getSpeed(), Z_axis_rotate));
            }
            else
            {
                fw.write(String.format("%6f %6f", 0.0, Z_axis_rotate));
            }
            fw.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    final SensorEventListener myAccelerometerListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                X_axis_rotate = sensorEvent.values[0];
                Y_axis_rotate = sensorEvent.values[1];
                Z_axis_rotate = sensorEvent.values[2];

                SetTextView();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            l = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
