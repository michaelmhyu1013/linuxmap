package com.example.ameer.comp4985_assignmen3;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.os.strictmode.CleartextNetworkViolation;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static android.location.Location.distanceBetween;

public class MainActivity extends AppCompatActivity {
    public EditText userName;
    public String userNameStr;
    public EditText hostName;
    public EditText portInput;
    String hostNameStr;
    int portInputInt;
    Button connectBtn;
    Button disconnectBtn;
    Button sendBtn;
    Button stopBtn;
    TCPClient client;
    UserInformation userInfo;
    boolean isConnected;
    private LocationManager locationManager;
    private LocationListener locationListener;
    boolean isSending;

    double longitude;
    double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isConnected = false;
        isSending = false;

        //User input parameters
        userName = findViewById(R.id.usernameTextBox);
        hostName = findViewById(R.id.ipAddressTextBox);
        portInput = findViewById(R.id.portTextEdit);

        //Buttons
        connectBtn =  findViewById(R.id.connectBtn);
        sendBtn =  findViewById(R.id.sendBtn);
        stopBtn =  findViewById(R.id.stopBtn);
        disconnectBtn =  findViewById(R.id.disconnectBtn);

        //Initialize button status
        disconnectBtn.setEnabled(false);
        sendBtn.setEnabled(false);
        stopBtn.setEnabled(false);





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


    public void startConnection(View view) throws IOException, InterruptedException {
        hostNameStr = hostName.getText().toString();
        userNameStr = userName.getText().toString();
        String portStr = portInput.getText().toString();

        if (hostNameStr.length() <= 0 || userNameStr.length() <= 0 || portStr.length() <= 0) {
            Toast.makeText(MainActivity.this, "Please make sure all fields are filled out", Toast.LENGTH_LONG).show();
            isConnected = false;
            return;
        }
        portInputInt = Integer.parseInt(portStr);
        userInfo = new UserInformation(userNameStr);
        connectBtn.setText("Connecting...");
        connectBtn.setEnabled(false);
        client = new TCPClient(hostNameStr, portInputInt);

        Runnable runnable = new Runnable () {
            public void run() {
                Looper.prepare();
                try {
                    isConnected = client.connectToServer(getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (isConnected) {
                            connectBtn.setText("Connected");
                            sendBtn.setEnabled(true);
                            disconnectBtn.setEnabled(true);
                        } else {

                            Toast.makeText(MainActivity.this, "Failed to connect\nPlease check IP/PORT", Toast.LENGTH_LONG).show();
                            connectBtn.setEnabled(true);
                            connectBtn.setText("Connect");
                        }
                    }
                });
                Looper.loop();

            }
        };
        Thread thread = new Thread (runnable);
        thread.start();
    }

    public void terminateConnection(View view) throws IOException {
        client.disconnectFromServer();
        isConnected = false;
        connectBtn.setEnabled(true);
        disconnectBtn.setEnabled(false);
        stopBtn.setEnabled(false);
        connectBtn.setText("Connect");
    }

    public void startSending(View view) throws IOException {
        sendBtn.setText("Sending");
        sendBtn.setEnabled(false);
        stopBtn.setEnabled(true);
        final double[] latlng = new double[2];
        latlng[0] = latitude;
        latlng[1] = longitude;
        isSending = true;
        String json1 = getJSON();
        sendWrapper(json1);
        Runnable runnable = new Runnable () {
            public void run() {
                while (isConnected && isSending) {
                    String json = getJSON();
                    if ((int) latlng[0] != 0 && (int) latlng[1] != 0){
                        float result[] = new float[1];
                        distanceBetween(latitude, longitude, latlng[0], latlng[1], result);
                        if (result[0] > 3) {
                            sendWrapper(json);
                            latlng[0] = latitude;
                            latlng[1] = longitude;

                        }
                    }
                }
            }
        };
        Thread thread = new Thread (runnable);
        thread.start();
    }

    public void stopSending(View view) {
        sendBtn.setText("Start Sending");
        stopBtn.setEnabled(false);
        sendBtn.setEnabled(true);
        isSending = false;
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
