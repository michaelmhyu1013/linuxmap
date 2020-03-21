package com.example.ameer.comp4985_assignmen3;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import com.google.gson.Gson;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static android.location.Location.distanceBetween;

public class MainActivity extends AppCompatActivity {
    EditText userName;
    String userNameStr;
    EditText hostName;
    String hostNameStr;
    Button connectBtn;
    TCPClient client;
    UserInformation userInfo;
    boolean isConnected;
    private LocationManager locationManager;
    private LocationListener locationListener;

    double longitude;
    double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isConnected = false;


        userName = findViewById(R.id.usernameTextBox);
        hostName = findViewById(R.id.ipAddressTextBox);
        connectBtn = findViewById(R.id.connectBtn);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {}
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
    }


    public void startConnection(View view) throws IOException {
        hostNameStr = hostName.getText().toString();
        userNameStr = userName.getText().toString();
        userInfo = new UserInformation(userNameStr);
        client = new TCPClient("192.168.1.134", 9000);
        //client = new TCPClient(hostNameStr, 9000);
        isConnected = true;
    }

    public void terminateConnection(View view) throws IOException {
        isConnected = false;
        client.disconnectFromServer();
    }

    public void startSending(View view) throws IOException {
        final double[] latlng = new double[2];
        String json1 = getJSON();
        sendWrapper(json1);

        Runnable runnable = new Runnable () {
            public void run() {
                while (true && isConnected) {
                    String json = getJSON();
                    if ((int) latlng[0] != 0 && (int) latlng[1] != 0){
                        float result[] = new float[1];
                        distanceBetween(latitude, longitude, latlng[0], latlng[1], result);

                        //if (result[0] > 2) {
                            sendWrapper(json);

                        //}
                    }
                    latlng[0] = latitude;
                    latlng[1] = longitude;
                    // Aman doesn't like putting threads to sleep
                    // So the check above makes sure to only update when there is movement
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread (runnable);
        thread.start();
    }

    public void getUpdatedLongLat(){
        longitude = 0;
        latitude = 0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
    }

    private String getJSON(){
        getUpdatedLongLat();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
        String formattedTime = simpleDateFormat.format(new Date());
        userInfo.setTimeStamp(formattedTime);
        userInfo.setUserLongitude(longitude);
        userInfo.setUserLatitude(latitude);
        Gson gson = new Gson();
        return gson.toJson(userInfo);
    }

    private void sendWrapper (String str){
        try {
            client.sendToServer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
