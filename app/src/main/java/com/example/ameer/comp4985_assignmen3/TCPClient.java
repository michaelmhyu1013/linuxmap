package com.example.ameer.comp4985_assignmen3;


import android.content.Context;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/*------------------------------------------------------------------------------------------------------------------
-- SOURCE FILE: TCPClient.java - This the TCPClient class that is used in the application.
--
-- PROGRAM: COMP4985 Assignment3 - Android
--
-- MTHODS:
-- public TCPClient (String hostName, int port)
-- public boolean connectToServer(final Context context)
-- public void disconnectFromServer()
-- public void sendToServer(final String clientStr)
--
-- DATE: March 22, 2020
--
-- DESIGNER: Amir Kbah
--
-- PROGRAMMER: Amir Kbah
--
-- NOTES:
-- The TCPCLient class contains methods used to establish TCP connection with a server, send TCP packets to servers
-- and finally disconnect from a server
----------------------------------------------------------------------------------------------------------------------*/

public class TCPClient {
    InetAddress IPAddress;
    String hostName;
    int port;
    Socket clientSocket;
    boolean isConnected;

    /*------------------------------------------------------------------------------------------------------------------
    -- METHOD: TCPClient
    --
    -- DATE: March 22, 2020
    --
    -- DESIGNER: Amir Kbah
    --
    -- PROGRAMMER: Amir Kbah
    --
    -- INTERFACE: public TCPClient (String hostName, int port)
    --
    -- RETURNS: void.
    --
    -- PARAMETERS:
    -- String hostName - the host or IP address of the host to connect to
    -- int port - the port of the TCP server to connect to
    --
    -- NOTES:
    -- This is the only constructor of the TCPCLient class
    ----------------------------------------------------------------------------------------------------------------------*/
    public TCPClient (String hostName, int port) throws IOException {
        this.hostName = hostName;
        this.port = port;
        isConnected = true;
    }

    /*------------------------------------------------------------------------------------------------------------------
    -- METHOD: connectToServer
    --
    -- DATE: March 22, 2020
    --
    -- DESIGNER: Amir Kbah
    --
    -- PROGRAMMER: Amir Kbah
    --
    -- INTERFACE: public boolean connectToServer(final Context context)
    --
    -- RETURNS: boolean - whether a connection was successfully established.
    --
    -- PARAMETERS:
    -- Context context - the application context
    --
    -- NOTES:
    -- Establishes a connection to a TCP server on a separate thread so as not to stop the main or UI thread of the Android
    -- application.
    ----------------------------------------------------------------------------------------------------------------------*/
    public boolean connectToServer(final Context context) throws IOException, InterruptedException {

        Runnable runnable = new Runnable () {
            public void run() {
                try {
                    //Get by name checks whether an IP or a hostName were provided
                    IPAddress = InetAddress.getByName(hostName);
                    clientSocket = new Socket(IPAddress, port);
                } catch (UnknownHostException e) {
                    isConnected = false;
                    e.printStackTrace();
                } catch (IOException e) {
                    isConnected = false;
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread (runnable);
        thread.start();
        thread.join();
        return isConnected;
    }

    /*------------------------------------------------------------------------------------------------------------------
    -- METHOD: disconnectFromServer
    --
    -- DATE: March 22, 2020
    --
    -- DESIGNER: Amir Kbah
    --
    -- PROGRAMMER: Amir Kbah
    --
    -- INTERFACE: public void disconnectFromServer()
    --
    -- RETURNS: void.
    --
    -- PARAMETERS:
    -- none
    --
    -- NOTES:
    -- Terminates the connection with the TCP server
    ----------------------------------------------------------------------------------------------------------------------*/
    public void disconnectFromServer() throws IOException {
        IPAddress = null;
        hostName = null;
        port = 0;
        clientSocket.close();
    }


    /*------------------------------------------------------------------------------------------------------------------
    -- METHOD: sendToServer
    --
    -- DATE: March 22, 2020
    --
    -- DESIGNER: Amir Kbah
    --
    -- PROGRAMMER: Amir Kbah
    --
    -- INTERFACE: public void sendToServer(final String clientStr)
    --
    -- RETURNS: void.
    --
    -- PARAMETERS:
    -- final String clientStr - the string to be sent to the TCP server
    --
    -- NOTES:
    -- Sends a string to the server, the clientStr parameter is converted to array of bytes
    ----------------------------------------------------------------------------------------------------------------------*/
    public void sendToServer(final String clientStr) throws IOException {
        Runnable runnable = new Runnable () {
            public void run() {
                if (clientSocket != null){
                    OutputStream outToServer = null;
                    try {
                        outToServer = clientSocket.getOutputStream();
                        DataOutputStream out = new DataOutputStream (outToServer);
                        out.write(clientStr.getBytes());
                        //out.writeUTF(clientStr);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread thread = new Thread (runnable);
        thread.start();
    }
}