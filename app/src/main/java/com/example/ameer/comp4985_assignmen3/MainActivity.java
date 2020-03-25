package com.example.ameer.comp4985_assignmen3;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static android.location.Location.distanceBetween;


/*------------------------------------------------------------------------------------------------------------------
-- SOURCE FILE: MainActivity.java - This application connects an android smart-phone to a remote TCP server to send
--                                  information about the device's locations and updates
--
--
-- PROGRAM: COMP4985 Assignment3 - Android
--
-- METHODS:
-- protected void onCreate(Bundle savedInstanceState)
-- INTERFACE: private void initLocationListener()
-- public void startConnection(View view)
-- public void terminateConnection(View view)
-- public void startSending(View view)
-- public void stopSending(View view)
-- public void getUpdatedLongLat()
-- private void updateLabels(final double lng, final double lat, final String timeStamp)
-- private String getJSON()
-- private void sendWrapper (String str)
--
-- DATE: March 22, 2020
--
--
-- DESIGNER: Amir Kbah
--
-- PROGRAMMER: Amir Kbah
--
-- NOTES: An android application that will track the user's phone movement and report the
--        longitude, latitude, timestamp and name of user to a remote server to display
--        the user's movements on a google map's fragment on a remote website
----------------------------------------------------------------------------------------------------------------------*/
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
    TextView longLbl;
    TextView latLbl;
    TextView tsLbl;
    TCPClient client;
    UserInformation userInfo;
    boolean isConnected;
    private LocationManager locationManager;
    private LocationListener locationListener;
    boolean isSending;

    double longitude;
    double latitude;

    /*------------------------------------------------------------------------------------------------------------------
    -- METHOD: onCreate
    --
    -- DATE: March 22, 2020
    --
    -- DESIGNER: Amir Kbah
    --
    -- PROGRAMMER: Amir Kbah
    --
    -- INTERFACE: protected void onCreate(Bundle savedInstanceState)
    --
    -- RETURNS: void.
    --
    -- PARAMETERS:
    -- Bundle savedInstanceState - the saved instance of the application when the application resumes
    --
    -- NOTES:
    -- This method is called when the activity is created in Android and it marks the entry point to the application
    -- in it, we initialize all the differnt UI elements and location listener
    ----------------------------------------------------------------------------------------------------------------------*/
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

        longLbl = findViewById(R.id.lngLabel);
        latLbl = findViewById(R.id.latLabel);
        tsLbl = findViewById(R.id.tsLabel);

        initLocationListener();

    }

    /*------------------------------------------------------------------------------------------------------------------
    -- METHOD: initLocationListener
    --
    -- DATE: March 22, 2020
    --
    -- DESIGNER: Amir Kbah
    --
    -- PROGRAMMER: Amir Kbah
    --
    -- INTERFACE: private void initLocationListener()
    --
    -- RETURNS: void.
    --
    -- PARAMETERS:
    -- None
    --
    -- NOTES:
    -- This is a helper method used to initialize a location listener and location manager so that later the application can
    -- query the phone's location on demand.
    ----------------------------------------------------------------------------------------------------------------------*/
    private void initLocationListener(){
        //Initialize the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //initialize the location listener
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

    /*------------------------------------------------------------------------------------------------------------------
    -- METHOD: startConnection
    --
    -- DATE: March 22, 2020
    --
    -- DESIGNER: Amir Kbah
    --
    -- PROGRAMMER: Amir Kbah
    --
    -- INTERFACE: public void startConnection(View view)
    --
    -- RETURNS: void.
    --
    -- PARAMETERS:
    -- View view - the view that this method is attached to
    --
    -- NOTES:
    -- This method is called when the user presses the "CONNECT" button, it checks the fields to ensure there are no empty
    -- fields and then it creates a TCPCLient instance and calls the connectToServer method of the TCP client. The connection
    -- is made on a separate thead to avoid freezing the application in case of prolonged/slow connection or if the IP:PORT
    -- are incorrect which can cause the Socket to hang for while when trying to connect.
    ----------------------------------------------------------------------------------------------------------------------*/
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


    /*------------------------------------------------------------------------------------------------------------------
    -- METHOD: terminateConnection
    --
    -- DATE: March 22, 2020
    --
    -- DESIGNER: Amir Kbah
    --
    -- PROGRAMMER: Amir Kbah
    --
    -- INTERFACE: public void terminateConnection(View view)
    --
    -- RETURNS: void.
    --
    -- PARAMETERS:
    -- View view - the view that this method is attached to
    --
    -- NOTES:
    -- This method is called when the user presses the "DISCONNECT" button. it calls the client's disconnectFromServer method
    -- and sets isConnected to false as well as set the values of buttons and disable the send button and stop buttons
    ----------------------------------------------------------------------------------------------------------------------*/
    public void terminateConnection(View view) throws IOException {
        client.disconnectFromServer();
        isConnected = false;
        connectBtn.setEnabled(true);
        disconnectBtn.setEnabled(false);
        sendBtn.setEnabled(false);
        sendBtn.setText("Start Sending");
        stopBtn.setEnabled(false);
        connectBtn.setText("Connect");
    }


    /*------------------------------------------------------------------------------------------------------------------
    -- METHOD: startSending
    --
    -- DATE: March 22, 2020
    --
    -- DESIGNER: Amir Kbah
    --
    -- PROGRAMMER: Amir Kbah
    --
    -- INTERFACE: public void startSending(View view)
    --
    -- RETURNS: void.
    --
    -- PARAMETERS:
    -- View view - the view that this method is attached to
    --
    -- NOTES:
    -- This method is called when the user presses the "START SENDING" button. it starts a new thread that checks the position
    -- of the device and sends updates to server when the difference between previous update and current update is greater than
    -- 3 meters
    ----------------------------------------------------------------------------------------------------------------------*/
    public void startSending(View view) throws IOException {
        sendBtn.setText("Sending...");
        sendBtn.setEnabled(false);
        stopBtn.setEnabled(true);
        final double[] latlng = new double[2];
        latlng[0] = latitude;
        latlng[1] = longitude;
        isSending = true;
        //String json1 = getJSON();
        //sendWrapper(json1);
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


    /*------------------------------------------------------------------------------------------------------------------
    -- METHOD: stopSending
    --
    -- DATE: March 22, 2020
    --
    -- DESIGNER: Amir Kbah
    --
    -- PROGRAMMER: Amir Kbah
    --
    -- INTERFACE: public void terminateConnection(View view)
    --
    -- RETURNS: void.
    --
    -- PARAMETERS:
    -- View view - the view that this method is attached to
    --
    -- NOTES:
    -- This method is called when the user presses the "DISCONNECT" button. it calls the client's disconnectFromServer method
    -- and sets isConnected to false as well as set the values of buttons and disable the send button and stop buttons
    ----------------------------------------------------------------------------------------------------------------------*/
    public void stopSending(View view) {
        sendBtn.setText("Start Sending");
        stopBtn.setEnabled(false);
        sendBtn.setEnabled(true);
        isSending = false;
    }

    /*------------------------------------------------------------------------------------------------------------------
    -- METHOD: getUpdatedLongLat
    --
    -- DATE: March 22, 2020
    --
    -- DESIGNER: Amir Kbah
    --
    -- PROGRAMMER: Amir Kbah
    --
    -- INTERFACE: public void getUpdatedLongLat()
    --
    -- RETURNS: void.
    --
    -- PARAMETERS:
    -- none
    --
    -- NOTES:
    -- This method gets the latitude and longitude of the device and updates the longitude and latitude instance variables
    ----------------------------------------------------------------------------------------------------------------------*/
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

    /*------------------------------------------------------------------------------------------------------------------
    -- METHOD: updateLabels
    --
    -- DATE: March 25, 2020
    --
    -- DESIGNER: Amir Kbah
    --
    -- PROGRAMMER: Amir Kbah
    --
    -- INTERFACE: private void updateLabels(final double lng, final double lat, final String timeStamp)
    --
    -- RETURNS: void
    --
    -- PARAMETERS:
    -- final double lng - Device's longitude
    -- final double lat - Device's latitude
    -- final String timeStamp - current time
    --
    -- NOTES:
    -- This method returns a string in JSON format which contains the data about the current longitude nad latitude, timestamp,
    -- and name of the user. The method first updates the longitude and latitude then gets the timestamp, then updates the
    -- userInfo object and converts it into a JSON object string
    ----------------------------------------------------------------------------------------------------------------------*/
    private void updateLabels(final double lng, final double lat, final String timeStamp) {
        runOnUiThread(new Runnable() {
            public void run() {
                longLbl.setText("" + lng);
                latLbl.setText("" + lat);
                tsLbl.setText(timeStamp);
            }
        });
    }

    /*------------------------------------------------------------------------------------------------------------------
    -- METHOD: getJSON
    --
    -- DATE: March 22, 2020
    --
    -- DESIGNER: Amir Kbah
    --
    -- PROGRAMMER: Amir Kbah
    --
    -- INTERFACE: private String getJSON()
    --
    -- RETURNS: string - the JSON string to be sent to the server.
    --
    -- PARAMETERS:
    -- none
    --
    -- NOTES:
    -- This method returns a string in JSON format which contains the data about the current longitude nad latitude, timestamp,
    -- and name of the user. The method first updates the longitude and latitude then gets the timestamp, then updates the
    -- userInfo object and converts it into a JSON object string
    ----------------------------------------------------------------------------------------------------------------------*/
    private String getJSON(){
        getUpdatedLongLat();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
        String formattedTime = simpleDateFormat.format(new Date());
        userInfo.setTimeStamp(formattedTime);
        userInfo.setUserLongitude(longitude);
        userInfo.setUserLatitude(latitude);
        Gson gson = new Gson();
        updateLabels(longitude, latitude, formattedTime);
        return gson.toJson(userInfo);
    }

    /*------------------------------------------------------------------------------------------------------------------
    -- METHOD: sendWrapper
    --
    -- DATE: March 22, 2020
    --
    -- DESIGNER: Amir Kbah
    --
    -- PROGRAMMER: Amir Kbah
    --
    -- INTERFACE: private void sendWrapper()
    --
    -- RETURNS: void.
    --
    -- PARAMETERS:
    -- String str - the JSON string to send to the TCP server
    --
    -- NOTES:
    -- This is a helper private method. It is used to call the TCPClient's sendToServer method. The wrapper is needed since
    -- the sendToServer method may throw an dexception and calling this function will always require a try/catch block.
    ----------------------------------------------------------------------------------------------------------------------*/
    private void sendWrapper (String str){
        try {
            client.sendToServer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
